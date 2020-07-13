package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Card;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.encryption.Seed128;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCorporationService {

    private final UserRepository repoUser;
    private final CorpRepository repoCorp;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final D1000Repository repoD1000;
    private final RiskConfigRepository repoRiskConfig;
    private final RiskRepository repoRisk;
    private final D1100Repository repoD1100;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final CeoInfoRepository repoCeo;
    private final VentureBusinessRepository repoVenture;
    private final StockholderFileRepository repoFile;
	private final ResAccountRepository repoResAccount;
	private final ConsentRepository repoConsent;
	private final ConsentMappingRepository repoConsentMapping;
    private final D1400Repository repoD1400;

    private final AwsS3Service s3Service;
    private final GwUploadService gwUploadService;

	@Value("${stockholder.file.size}")
	private Long STOCKHOLDER_FILE_SIZE;

    /**
     * 법인정보 업종종류 조회
     *
     * @return 벤처기업사 목록
     */
    @Transactional(readOnly = true)
    public List<UserCorporationDto.BusinessType> getBusinessType() {
        return repoCodeDetail.findAllByCode(CommonCodeType.BUSINESS_1).stream().map(UserCorporationDto.BusinessType::from).collect(Collectors.toList());
    }

    /**
     * 법인정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.CorporationRes updateCorporation(Long idx_user, UserCorporationDto.RegisterCorporation dto, Long idx_CardInfo) {
        User user = findUser(idx_user);

        D1000 d1000 = getD1000(user.corp().idx());
        Corp corp = repoCorp.save(user.corp()
                .resCompanyEngNm(dto.getEngCorName())
                .resCompanyNumber(dto.getCorNumber())
                .resBusinessCode(dto.getBusinessCode())
                .resUserType(d1000 != null ? d1000.getD009() : null)
        );

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().build();
        }

        if (d1000 != null) {
            String[] corNumber = dto.getCorNumber().split("-");
            repoD1000.save(d1000
                    .setD006(!StringUtils.hasText(d1000.getD006()) ? dto.getEngCorName() : d1000.getD006())
                    .setD008(!StringUtils.hasText(d1000.getD008()) ? dto.getBusinessCode() : d1000.getD008())
                    .setD026(!StringUtils.hasText(d1000.getD026()) ? corNumber[0] : d1000.getD026())
                    .setD027(!StringUtils.hasText(d1000.getD027()) ? corNumber[1] : d1000.getD027())
                    .setD028(!StringUtils.hasText(d1000.getD028()) ? corNumber[2] : d1000.getD028())
            );
        }
        return UserCorporationDto.CorporationRes.from(corp, cardInfo.idx());
    }

    /**
     * 벤처기업정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.VentureRes registerVenture(Long idx_user, UserCorporationDto.RegisterVenture dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        String investorName = repoVenture.findEqualsName(dto.getInvestorName());
        cardInfo.venture(Venture.builder()
                .investAmount(dto.getAmount())
                .isVC(dto.getIsVC())
                .isVerifiedVenture(dto.getIsVerifiedVenture())
                .investor(investorName != null ? investorName : dto.getInvestorName())
                .isExist(investorName != null ? true : false)
                .build()
        );
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRiskConfig.save(riskConfig.get()
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .vcInvestment(dto.getIsVC())
            );
        } else {
            repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .enabled(true)
                    .vcInvestment(dto.getIsVC())
                    .build()
            );
        }

        return UserCorporationDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 주주정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.VentureRes registerStockholder(Long idx_user, UserCorporationDto.RegisterStockholder dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo.stockholder(Stockholder.builder()
                .isStockHold25(dto.getIsHold25())
                .isStockholderList(dto.getIsStockholderList())
                .isStockholderPersonal(dto.getIsPersonal())
                .stockholderName(dto.getName())
                .stockholderEngName(dto.getEngName())
                .stockholderBirth(dto.getBirth())
                .stockholderNation(dto.getNation())
                .stockRate(dto.getRate())
                .build());

        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRiskConfig.save(riskConfig.get()
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
            );
        } else {
            repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
                    .build()
            );
        }

        D1000 d1000 = getD1000(user.corp().idx());
        if (d1000 != null) {
            repoD1000.save(d1000.setD044("0113")
                    .setD059(dto.getName())
                    .setD060(dto.getEngName())
                    .setD061(dto.getBirth())
                    .setD062(dto.getNation())
                    .setD064(getCorpOwnerCode(dto))
                    .setD065(dto.getRate())
                    .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y")
            );
        }

        D1400 d1400 = getD1400(user.corp().idx());
        if (d1400 != null) {
            repoD1400.save(d1400.setD018("0113")
                    .setD019(dto.getName())
                    .setD020(dto.getEngName())
                    .setD021(dto.getBirth())
                    .setD022(dto.getNation())
                    .setD023(getCorpOwnerCode(dto))
                    .setD024(dto.getRate())
            );
        }

        return UserCorporationDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private String getCorpOwnerCode(UserCorporationDto.RegisterStockholder dto) {
        if (dto.getIsHold25()) {
            return Const.CORP_OWNER_CODE_1;
        } else {
            if (dto.getIsPersonal()) {
                return Const.CORP_OWNER_CODE_2;
            } else {
                if (dto.getIsStockholderList()) {
                    return Const.CORP_OWNER_CODE_5;
                } else {
                    return Const.CORP_OWNER_CODE_2;
                }
            }
        }
    }

    /**
     * 주주명부 파일 등록
     *
     * @param idx_user     등록하는 User idx
	 * @param file_1       파일1
     * @param file_2       파일2
     * @param type         file type
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
	@Transactional(noRollbackFor = FileUploadException.class)
	public List<UserCorporationDto.StockholderFileRes> registerStockholderFile(Long idx_user, MultipartFile[] file_1, MultipartFile[] file_2, String type, Long idx_CardInfo) throws IOException {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        if (ObjectUtils.isEmpty(file_1) && ObjectUtils.isEmpty(file_2)) {
            throw EmptyResxException.builder().build();
        }

        StockholderFileType fileType = StockholderFileType.valueOf(type);
        List<StockholderFile> fileList = repoFile.findAllByCorpAndType(user.corp(), fileType);
        if (!ObjectUtils.isEmpty(fileList)) {
            for (StockholderFile file : fileList) {
                repoFile.delete(file);
                s3Service.s3FileDelete(file.s3Key());
                gwUploadService.delete(file.fname(), cardInfo.cardCompany().getCode());
            }
        }
        List<UserCorporationDto.StockholderFileRes> resultList = new ArrayList<>();
        int gwUploadCount = 0;
        if (!ObjectUtils.isEmpty(file_1)) {
            resultList.addAll(uploadStockholderFile(file_1, fileType, cardInfo, fileType.getCode() + "00" + (++gwUploadCount)));
        }
        if (!ObjectUtils.isEmpty(file_2)) {
            resultList.addAll(uploadStockholderFile(file_2, fileType, cardInfo, fileType.getCode() + "00" + (++gwUploadCount)));
        }

        return resultList;
    }

    private List<UserCorporationDto.StockholderFileRes> uploadStockholderFile(MultipartFile[] files, StockholderFileType type, CardIssuanceInfo cardInfo, String seq) throws IOException {
        if (files.length > 2) {
            throw BadRequestedException.builder().category(BadRequestedException.Category.EXCESS_UPLOAD_FILE_LENGTH).build();
        }
        boolean sendGwUpload = false;
        String licenseNo = cardInfo.corp().resCompanyIdentityNo().replaceAll("-", "");
        List<UserCorporationDto.StockholderFileRes> resultList = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = licenseNo + Const.STOCKHOLDER_GW_FILE_CODE + seq + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            if (file.getSize() > STOCKHOLDER_FILE_SIZE || sendGwUpload) {
                fileName = licenseNo + Const.STOCKHOLDER_GW_FILE_CODE + seq + "_back." + FilenameUtils.getExtension(file.getOriginalFilename());
            }

            File uploadFile = new File(fileName);
            uploadFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(uploadFile);
            fos.write(file.getBytes());
            fos.close();

            String s3Key = "stockholder/" + cardInfo.idx() + "/" + fileName;
            try {
                String s3Link = s3Service.s3FileUpload(uploadFile, s3Key);

                if (file.getSize() <= STOCKHOLDER_FILE_SIZE && !sendGwUpload) {
                    gwUploadService.upload(uploadFile, cardInfo.cardCompany().getCode(), Const.STOCKHOLDER_GW_FILE_CODE, licenseNo);
                    sendGwUpload = true;
                } else {
                    sendGwUpload = false;
                }

                uploadFile.delete();

                resultList.add(UserCorporationDto.StockholderFileRes.from(repoFile.save(StockholderFile.builder()
                        .cardIssuanceInfo(cardInfo)
                        .corp(cardInfo.corp())
                        .fname(fileName)
                        .type(type)
                        .s3Link(s3Link)
                        .s3Key(s3Key)
                        .size(file.getSize())
                        .isTransferToGw(sendGwUpload)
                        .orgfname(file.getOriginalFilename()).build()), cardInfo.idx()));

            } catch (Exception e) {
				uploadFile.delete();
				s3Service.s3FileDelete(s3Key);
                gwUploadService.delete(fileName, cardInfo.cardCompany().getCode());

				log.error("[uploadStockholderFile] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                throw FileUploadException.builder().category(FileUploadException.Category.UPLOAD_STOCKHOLDER_FILE).build();
            }
        }

        return resultList;
    }

    /**
     * 주주명부 파일 삭제
     *
     * @param idx_user 등록하는 User idx
     * @param idx_file 삭제대상 StockholderFile 식별자
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteStockholderFile(Long idx_user, Long idx_file, Long idx_CardInfo) throws IOException {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        StockholderFile file = findStockholderFile(idx_file);
        if (file.cardIssuanceInfo().idx() != idx_CardInfo) {
            throw MismatchedException.builder().category(MismatchedException.Category.STOCKHOLDER_FILE).build();
        }
        gwUploadService.delete(file.fname(), cardInfo.cardCompany().getCode());
        s3Service.s3FileDelete(file.s3Key());
        repoFile.delete(file);
    }

    /**
     * 카드발급정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.CardRes registerCard(Long idx_user, UserCorporationDto.RegisterCard dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo.card(Card.builder()
                .addressBasic(dto.getAddressBasic())
                .addressDetail(dto.getAddressDetail())
                .zipCode(dto.getZipCode())
                .addressKey(dto.getAddressKey())
                .hopeLimit(dto.getAmount())
                .calculatedLimit(dto.getCalAmount())
                .grantLimit(dto.getGrantAmount())
                .receiveType(dto.getReceiveType())
                .requestCount(dto.getCount())
                .build());

        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRiskConfig.save(riskConfig.get()
                    .calculatedLimit(dto.getCalAmount())
                    .hopeLimit(dto.getAmount())
                    .grantLimit(dto.getGrantAmount())
            );
        } else {
            repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .calculatedLimit(dto.getCalAmount())
                    .hopeLimit(dto.getAmount())
                    .grantLimit(dto.getGrantAmount())
                    .enabled(true)
                    .build()
            );
        }



        Integer intLimitrepoRisk = Integer.parseInt(String.valueOf(Math.round(repoRisk.findCardLimitNowFirst(idx_user, CommonUtil.getNowYYYYMMDD()))));
        Integer intAmount = Integer.parseInt(dto.getAmount());

        D1000 d1000 = getD1000(user.corp().idx());
        if (d1000 != null) {
            repoD1000.save(d1000
                    .setD022(dto.getZipCode().substring(0, 3))
                    .setD022(dto.getZipCode().substring(0, 3))
                    .setD023(dto.getZipCode().substring(3))
                    .setD024(dto.getAddressBasic())
                    .setD025(dto.getAddressDetail())
                    .setD050(intLimitrepoRisk<intAmount?intLimitrepoRisk.toString():intAmount.toString())
                    .setD055(dto.getAddressKey())
            );
        }

        D1100 d1100 = getD1100(user.corp().idx());
        if (d1100 != null) {
            repoD1100.save(d1100
                    .setD029(dto.getReceiveType().getCode())
                    .setD031(dto.getZipCode().substring(0, 3))
                    .setD032(dto.getZipCode().substring(3))
                    .setD033(dto.getAddressBasic())
                    .setD034(dto.getAddressDetail())
                    .setD020(dto.getGrantAmount())
                    .setD039(dto.getCount() + "")
                    .setD046(Const.CARD_RECEIVE_ADDRESS_CODE)
                    .setD047(dto.getAddressKey())
            );
        }

        D1400 d1400 = getD1400(user.corp().idx());
        if (d1400 != null) {
            repoD1400.save(d1400
                    .setD014(intLimitrepoRisk<intAmount?intLimitrepoRisk.toString():intAmount.toString())
            );
        }

        return UserCorporationDto.CardRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 결제 계좌정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.AccountRes registerAccount(Long idx_user, UserCorporationDto.RegisterAccount dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

		ResAccount account = findResAccount(dto.getAccountIdx());
		String bankCode = account.organization();

        cardInfo.bankAccount(BankAccount.builder()
                .bankAccount(account.resAccount())
				.bankCode(bankCode)
                .bankAccountHolder(dto.getAccountHolder())
                .build());

        String bankName = null;
        CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.BANK_1, bankCode);
        if (commonCodeDetail != null) {
            bankName = commonCodeDetail.value1();
        }

		if (bankCode.length() > 3) {
			bankCode = bankCode.substring(bankCode.length() - 3);
		}

		D1100 d1100 = getD1100(user.corp().idx());
		if (d1100 != null) {
			repoD1100.save(d1100
                    .setD024(bankCode)
                    .setD025(Seed128.encryptEcb(account.resAccount()))
                    .setD026(dto.getAccountHolder())
            );
        }

        return UserCorporationDto.AccountRes.from(repoCardIssuance.save(cardInfo), bankName);
    }

    /**
     * 대표자 정보
     *
     * @param idx_user 조회하는 User idx
     * @return 등록 정보
     */
    @Transactional(readOnly = true)
    public UserCorporationDto.CeoTypeRes getCeoType(Long idx_user) {
        User user = findUser(idx_user);
        if (user.corp() == null) {
            throw EntityNotFoundException.builder().entity("Corp").build();
        }
        D1000 d1000 = getD1000(user.corp().idx());
        Integer count = 1;
        String ceoType = "1";
        if (d1000 != null) {
            ceoType = d1000.getD009();
            if (StringUtils.hasText(d1000.getD010()) && StringUtils.hasText(d1000.getD014()) && StringUtils.hasText(d1000.getD018())) {
                count = 3;
            } else if (StringUtils.hasText(d1000.getD010()) && StringUtils.hasText(d1000.getD014()) && !StringUtils.hasText(d1000.getD018())) {
                count = 2;
            }
        }
        return UserCorporationDto.CeoTypeRes.builder()
                .type(ceoType)
                .count(count)
                .build();
    }

    /**
     * 대표자 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public UserCorporationDto.CeoRes registerCeo(Long idx_user, UserCorporationDto.RegisterCeo dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        CeoInfo ceo = null;
        if (!ObjectUtils.isEmpty(dto.getCeoIdx())) {
            ceo = findCeoInfo(dto.getCeoIdx());
            if (!cardInfo.ceoInfos().contains(ceo)) {
                throw MismatchedException.builder().category(MismatchedException.Category.CEO).build();
            }
        }

        Integer ceoNum = 0;
        D1000 d1000 = getD1000(user.corp().idx());
        if (d1000 != null) {
            if (!StringUtils.hasText(d1000.getD012()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
                repoD1000.save(d1000
                        .setD010(dto.getName())
                        .setD012(dto.getEngName())
                        .setD013(dto.getNation())
                        .setD035(dto.getName())
                        .setD036(dto.getPhoneNumber().substring(0, 3))
                        .setD037(dto.getPhoneNumber().substring(3, 7))
                        .setD038(dto.getPhoneNumber().substring(7))
                        .setD040(dto.getPhoneNumber().substring(0, 3))
                        .setD041(dto.getPhoneNumber().substring(3, 7))
                        .setD042(dto.getPhoneNumber().substring(7))
                );
                ceoNum = 1;

                D1100 d1100 = getD1100(user.corp().idx());
                if (d1100 != null) {
                    repoD1100.save(d1100
                            .setD035(dto.getPhoneNumber().substring(0, 3))
                            .setD036(dto.getPhoneNumber().substring(3, 7))
                            .setD037(dto.getPhoneNumber().substring(7))
                    );
                }

            } else if (!StringUtils.hasText(d1000.getD016()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
                repoD1000.save(d1000
                        .setD014(dto.getName())
                        .setD016(dto.getEngName())
                        .setD017(dto.getNation())
                );
                ceoNum = 2;

            } else if (!StringUtils.hasText(d1000.getD020()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
                repoD1000.save(d1000
                        .setD018(dto.getName())
                        .setD020(dto.getEngName())
                        .setD021(dto.getNation())
                );
                ceoNum = 3;
            }
        }

        if (ObjectUtils.isEmpty(ceo)) {
            ceo = CeoInfo.builder()
                    .cardIssuanceInfo(cardInfo)
                    .engName(dto.getEngName())
                    .name(dto.getName())
                    .nationality(dto.getNation())
                    .isForeign("KR".equalsIgnoreCase(dto.getNation()) ? false : true)
                    .phoneNumber(dto.getPhoneNumber())
                    .agencyCode(dto.getAgency())
                    .genderCode(dto.getGenderCode())
                    .birth(dto.getBirth())
                    .certificationType(dto.getIdentityType())
                    .type(CeoType.from(d1000.getD009()))
                    .ceoNumber(ceoNum)
                    .build();
        } else {
            ceo.engName(dto.getEngName())
                    .name(dto.getName())
                    .nationality(dto.getNation())
                    .isForeign("KR".equalsIgnoreCase(dto.getNation()) ? false : true)
                    .phoneNumber(dto.getPhoneNumber())
                    .agencyCode(dto.getAgency())
                    .genderCode(dto.getGenderCode())
                    .birth(dto.getBirth())
                    .certificationType(dto.getIdentityType())
                    .type(CeoType.from(d1000.getD009()))
                    .ceoNumber(ceoNum);
        }

        return UserCorporationDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
    }

    /**
     * 카드발급정보 전체 조회
     *
     * @param idx_user 조회하는 User idx
     * @return 카드발급정보
     */
    @Transactional(readOnly = true)
    public UserCorporationDto.CardIssuanceInfoRes getCardIssuanceInfoByUser(Long idx_user) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findTopByCorpAndDisabledFalseOrderByIdxDesc(user.corp()).orElse(null);

        List<UserCorporationDto.ConsentRes> consentInfo = new ArrayList<>();

        List<BrandConsentDto> consents = repoConsent.findAllByEnabledOrderByConsentOrderAsc(true)
                .map(BrandConsentDto::from)
                .collect(Collectors.toList());

        consents.forEach(item -> {
            ConsentMapping consentMapping = repoConsentMapping.findTopByIdxUserAndIdxConsentOrderByIdxDesc(idx_user, item.getIdx());

            UserCorporationDto.ConsentRes resTemp = UserCorporationDto.ConsentRes.builder()
                    .consentIdx(item.getIdx())
                    .title(item.getTitle())
                    .boolConsent(consentMapping != null ? consentMapping.status() : false)
                    .consentType(item.getTypeCode())
                    .build();
            consentInfo.add(resTemp);
        });

        if (cardIssuanceInfo != null) {
            String bankName = null;
            if (cardIssuanceInfo.bankAccount() != null) {
                CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.BANK_1, cardIssuanceInfo.bankAccount().getBankCode());
                if (commonCodeDetail != null) {
                    bankName = commonCodeDetail.value1();
                }
            }

            UserCorporationDto.CorporationRes CorporationResDto = UserCorporationDto.CorporationRes.from(cardIssuanceInfo.corp(), cardIssuanceInfo.idx());
            if (!ObjectUtils.isEmpty(CorporationResDto.getBusinessCode())) {
                CommonCodeDetail codeDetailData = repoCodeDetail.getByCode1AndCode5(CorporationResDto.getBusinessCode().substring(0, 1), CorporationResDto.getBusinessCode().substring(1));
                CorporationResDto.setBusinessCodeValue(codeDetailData.value5());
            }
            return UserCorporationDto.CardIssuanceInfoRes.builder()
                    .consentRes(consentInfo)
                    .corporationRes(CorporationResDto)
                    .ventureRes(UserCorporationDto.VentureRes.from(cardIssuanceInfo))
                    .stockholderRes(UserCorporationDto.StockholderRes.from(cardIssuanceInfo))
                    .cardRes(UserCorporationDto.CardRes.from(cardIssuanceInfo))
                    .accountRes(UserCorporationDto.AccountRes.from(cardIssuanceInfo, bankName))
                    .ceoRes(cardIssuanceInfo.ceoInfos() != null ? cardIssuanceInfo.ceoInfos().stream().map(UserCorporationDto.CeoRes::from).collect(Collectors.toList()) : null)
                    .stockholderFileRes(cardIssuanceInfo.stockholderFiles() != null ? cardIssuanceInfo.stockholderFiles().stream().map(file -> UserCorporationDto.StockholderFileRes.from(file, cardIssuanceInfo.idx())).collect(Collectors.toList()) : null)
                    .build();
        } else {
            return UserCorporationDto.CardIssuanceInfoRes.builder()
                    .consentRes(consentInfo)
                    .corporationRes(UserCorporationDto.CorporationRes.from(user.corp(), null)).build();
        }
    }

    /**
     * 벤처기업사 조회
     *
     * @return 벤처기업사 목록
     */
    @Transactional(readOnly = true)
    public List<String> getVentureBusiness() {
        return repoVenture.findAllByOrderByNameAsc().stream().map(VentureBusiness::name).collect(Collectors.toList());
    }


    private User findUser(Long idx_user) {
        return repoUser.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
                        .build()
        );
    }

    private CeoInfo findCeoInfo(Long idx_ceo) {
        return repoCeo.findById(idx_ceo).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CeoInfo")
                        .idx(idx_ceo)
                        .build()
        );
    }

    private CardIssuanceInfo findCardIssuanceInfo(Corp corp) {
        return repoCardIssuance.findTopByCorpAndDisabledFalseOrderByIdxDesc(corp).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("CardIssuanceInfo")
                        .build()
        );
    }

    private D1000 getD1000(Long idx_corp) {
        return repoD1000.getTopByIdxCorpOrderByIdxDesc(idx_corp);
    }

    private D1100 getD1100(Long idx_corp) {
        return repoD1100.getTopByIdxCorpOrderByIdxDesc(idx_corp);
    }

    private D1400 getD1400(Long idx_corp) {
        return repoD1400.getTopByIdxCorpOrderByIdxDesc(idx_corp);
    }

    private StockholderFile findStockholderFile(Long idx_file) {
        return repoFile.findById(idx_file).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("StockholderFile")
                        .idx(idx_file)
                        .build()
        );
    }

	private ResAccount findResAccount(Long idx_resAccount) {
		return repoResAccount.findById(idx_resAccount).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("ResAccount")
						.idx(idx_resAccount)
						.build()
		);
	}
}
