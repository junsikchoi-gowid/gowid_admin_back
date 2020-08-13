package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.service.AwsS3Service;
import com.nomadconnection.dapp.api.service.GwUploadService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.VentureBusiness;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.VentureBusinessRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1400Repository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.shinhan.D1100;
import com.nomadconnection.dapp.core.domain.shinhan.D1400;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
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
public class ShinhanCardService {

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
    public List<CardIssuanceDto.BusinessType> getBusinessType() {
        return repoCodeDetail.findAllByCode(CommonCodeType.BUSINESS_1).stream().map(CardIssuanceDto.BusinessType::from).collect(Collectors.toList());
    }

    /**
     * 법인정보 등록
     *
     * @param idx_user     등록하는 User idx
     * @param dto          등록정보
     * @param idx_CardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CorporationRes updateCorporation(Long idx_user, CardIssuanceDto.RegisterCorporation dto, Long idx_CardInfo) {
        User user = findUser(idx_user);

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        D1000 d1000 = updateD1000Corp(user.corp().idx(), dto);
        updateD1400Corp(user.corp().idx(), dto);

        Corp corp = repoCorp.save(user.corp()
                .resCompanyEngNm(dto.getEngCorName())
                .resCompanyNumber(dto.getCorNumber())
                .resBusinessCode(dto.getBusinessCode())
                .resUserType(d1000 != null ? d1000.getD009() : null)
        );

        return CardIssuanceDto.CorporationRes.from(corp, cardInfo.idx());
    }

    private D1400 updateD1400Corp(Long idx_corp, CardIssuanceDto.RegisterCorporation dto) {
        D1400 d1400 = getD1400(idx_corp);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        String[] corNumber = dto.getCorNumber().split("-");
        return repoD1400.save(d1400
                .setD029(dto.getEngCorName())       // 법인영문명
                .setD048(corNumber[0])              // 직장전화지역번호
                .setD049(corNumber[1])              // 직장전화국번호
                .setD050(corNumber[2])              // 직장전화고유번호
                .setD058(corNumber[0])              // 신청관리자전화지역번호
                .setD059(corNumber[1])              // 신청관리자전화국번호
                .setD060(corNumber[2])              // 신청관리자전화고유번호
        );
    }


    private D1000 updateD1000Corp(Long idx_corp, CardIssuanceDto.RegisterCorporation dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        String[] corNumber = dto.getCorNumber().split("-");
        return repoD1000.save(d1000
                .setD006(dto.getEngCorName())           //법인영문명
                .setD008(dto.getBusinessCode())         //업종코드
                .setD026(corNumber[0])                  //직장전화지역번호
                .setD027(corNumber[1])                  //직장전화국번호
                .setD028(corNumber[2])                  //직장전화고유번호
                .setD036(corNumber[0])                  //신청관리자전화지역번호
                .setD037(corNumber[1])                  //신청관리자전화국번호
                .setD038(corNumber[2])                  //신청관리자전화고유번호
        );
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
    public CardIssuanceDto.VentureRes registerVenture(Long idx_user, CardIssuanceDto.RegisterVenture dto, Long idx_CardInfo) {
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

        updateRiskConfigVenture(user, dto);

        return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigVenture(User user, CardIssuanceDto.RegisterVenture dto) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .vcInvestment(dto.getIsVC())
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .enabled(true)
                    .vcInvestment(dto.getIsVC())
                    .build()
            );
        }
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
    public CardIssuanceDto.VentureRes registerStockholder(Long idx_user, CardIssuanceDto.RegisterStockholder dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        updateRiskConfigStockholder(user, dto);
        updateD1000Stockholder(user.corp().idx(), dto);
        updateD1400Stockholder(user.corp().idx(), dto);

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

        return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigStockholder(User user, CardIssuanceDto.RegisterStockholder dto) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
                    .build()
            );
        }
    }

    private D1400 updateD1400Stockholder(Long idx_corp, CardIssuanceDto.RegisterStockholder dto) {
        D1400 d1400 = getD1400(idx_corp);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        return repoD1400.save(d1400.setD018("0113")
                .setD019(dto.getName())
                .setD020(dto.getEngName())
                .setD021(dto.getBirth())
                .setD022(dto.getNation())
                .setD023(getCorpOwnerCode(dto))
                .setD024(dto.getRate())
        );
    }

    private D1000 updateD1000Stockholder(Long idx_corp, CardIssuanceDto.RegisterStockholder dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        return repoD1000.save(d1000.setD044("0113")
                .setD059(dto.getName())
                .setD060(dto.getEngName())
                .setD061(dto.getBirth())
                .setD062(dto.getNation())
                .setD064(getCorpOwnerCode(dto))
                .setD065(dto.getRate())
                .setD066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y")
        );
    }

    private String getCorpOwnerCode(CardIssuanceDto.RegisterStockholder dto) {
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
    public List<CardIssuanceDto.StockholderFileRes> registerStockholderFile(Long idx_user, MultipartFile[] file_1, MultipartFile[] file_2, String type, Long idx_CardInfo) throws IOException {
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

        List<CardIssuanceDto.StockholderFileRes> resultList = new ArrayList<>();
        int gwUploadCount = 0;
        if (!ObjectUtils.isEmpty(file_1)) {
            resultList.addAll(uploadStockholderFile(file_1, fileType, cardInfo, ++gwUploadCount));
        }
        if (!ObjectUtils.isEmpty(file_2)) {
            resultList.addAll(uploadStockholderFile(file_2, fileType, cardInfo, ++gwUploadCount));
        }

        return resultList;
    }

    private List<CardIssuanceDto.StockholderFileRes> uploadStockholderFile(MultipartFile[] files, StockholderFileType type, CardIssuanceInfo cardInfo, int gwUploadCount) throws IOException {
        if (files.length > 2) {
            throw BadRequestedException.builder().category(BadRequestedException.Category.EXCESS_UPLOAD_FILE_LENGTH).build();
        }

        boolean sendGwUpload = false;
        String licenseNo = cardInfo.corp().resCompanyIdentityNo().replaceAll("-", "");
        String sequence = type.getCode() + "00" + gwUploadCount;

        List<CardIssuanceDto.StockholderFileRes> resultList = new ArrayList<>();
        for (MultipartFile file : files) {
            CardIssuanceDto.StockholderFileRes stockholderFileRes = uploadFile(cardInfo, file, licenseNo, sendGwUpload, sequence, type);
            sendGwUpload = stockholderFileRes.getIsTransferToGw();
            resultList.add(stockholderFileRes);
        }

        return resultList;
    }

    private CardIssuanceDto.StockholderFileRes uploadFile(CardIssuanceInfo cardInfo, MultipartFile file, String licenseNo, boolean sendGwUpload, String sequence, StockholderFileType type) throws IOException {
        String fileName = makeStockholderFileName(file, sendGwUpload, licenseNo, sequence);
        String s3Key = "stockholder/" + cardInfo.idx() + "/" + fileName;

        File uploadFile = new File(fileName);
        uploadFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(uploadFile);
        fos.write(file.getBytes());
        fos.close();

        try {
            String s3Link = s3Service.s3FileUpload(uploadFile, s3Key);

            if (file.getSize() <= STOCKHOLDER_FILE_SIZE && !sendGwUpload) {
                gwUploadService.upload(uploadFile, cardInfo.cardCompany().getCode(), Const.STOCKHOLDER_GW_FILE_CODE, licenseNo);
                sendGwUpload = true;
            } else {
                sendGwUpload = false;
            }

            uploadFile.delete();

            return CardIssuanceDto.StockholderFileRes.from(repoFile.save(StockholderFile.builder()
                    .cardIssuanceInfo(cardInfo)
                    .corp(cardInfo.corp())
                    .fname(fileName)
                    .type(type)
                    .s3Link(s3Link)
                    .s3Key(s3Key)
                    .size(file.getSize())
                    .isTransferToGw(sendGwUpload)
                    .orgfname(file.getOriginalFilename()).build()), cardInfo.idx());

        } catch (Exception e) {
            uploadFile.delete();
            s3Service.s3FileDelete(s3Key);
            gwUploadService.delete(fileName, cardInfo.cardCompany().getCode());

            log.error("[uploadStockholderFile] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw FileUploadException.builder().category(FileUploadException.Category.UPLOAD_STOCKHOLDER_FILE).build();
        }
    }

    private String makeStockholderFileName(MultipartFile file, boolean sendGwUpload, String licenseNo, String sequence) {
        if (file.getSize() > STOCKHOLDER_FILE_SIZE || sendGwUpload) {
            return licenseNo + Const.STOCKHOLDER_GW_FILE_CODE + sequence + "_back." + FilenameUtils.getExtension(file.getOriginalFilename());
        }
        return licenseNo + Const.STOCKHOLDER_GW_FILE_CODE + sequence + "." + FilenameUtils.getExtension(file.getOriginalFilename());
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
    public CardIssuanceDto.CardRes registerCard(Long idx_user, CardIssuanceDto.RegisterCard dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        Double cardLimitNow = repoRisk.findCardLimitNowFirst(idx_user, CommonUtil.getNowYYYYMMDD());
        Long calculatedLimitLong = 0L;
        if (!ObjectUtils.isEmpty(cardLimitNow)) {
            calculatedLimitLong = Long.parseLong(String.valueOf(Math.round(cardLimitNow)));
        }
        String calculatedLimit = String.valueOf(calculatedLimitLong);
        String grantLimit = calculatedLimitLong < Long.parseLong(dto.getAmount()) ? calculatedLimit : dto.getAmount();

        updateRiskConfigCard(user, grantLimit, calculatedLimit, dto.getAmount());
        updateD1000Card(user.corp().idx(), grantLimit, dto);
        updateD1400Card(user.corp().idx(), grantLimit, dto);
        updateD1100Card(user.corp().idx(), grantLimit, dto);

        cardInfo.card(Card.builder()
                .addressBasic(dto.getAddressBasic())
                .addressDetail(dto.getAddressDetail())
                .zipCode(dto.getZipCode())
                .addressKey(dto.getAddressKey())
                .hopeLimit(dto.getAmount())
                .calculatedLimit(calculatedLimit)
                .grantLimit(grantLimit)
                .receiveType(dto.getReceiveType())
                .requestCount(dto.getCount())
                .build());

        return CardIssuanceDto.CardRes.from(repoCardIssuance.save(cardInfo));
    }

    private RiskConfig updateRiskConfigCard(User user, String grantLimit, String calculatedLimit, String hopeLimit) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                    .calculatedLimit(calculatedLimit)
                    .hopeLimit(hopeLimit)
                    .grantLimit(grantLimit)
            );
        } else {
            return repoRiskConfig.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .calculatedLimit(calculatedLimit)
                    .hopeLimit(hopeLimit)
                    .grantLimit(grantLimit)
                    .enabled(true)
                    .build()
            );
        }
    }

    private D1000 updateD1000Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            return d1000;
        }
        return repoD1000.save(d1000
                .setD022(dto.getZipCode().substring(0, 3))      //직장우편앞번호
                .setD023(dto.getZipCode().substring(3))         //직장우편뒷번호
                .setD024(dto.getAddressBasic())                 //직장기본주소
                .setD025(dto.getAddressDetail())                //직장상세주소
                .setD050(grantLimit)                            //제휴약정한도금액
                .setD055(dto.getAddressKey())                   //도로명참조KEY값
        );
    }

    private D1400 updateD1400Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1400 d1400 = getD1400(idx_corp);
        if (ObjectUtils.isEmpty(d1400)) {
            return d1400;
        }
        return repoD1400.save(d1400
                .setD014(grantLimit)
                .setD044(dto.getZipCode().substring(0, 3))      // 직장우편앞번호
                .setD045(dto.getZipCode().substring(3))         // 직장우편뒷번호
                .setD046(dto.getAddressBasic())                 // 직장기본주소
                .setD047(dto.getAddressDetail())                // 직장상세주소
                .setD066(dto.getAddressKey())                   // 도로명참조KEY값
        );
    }

    private D1100 updateD1100Card(Long idx_corp, String grantLimit, CardIssuanceDto.RegisterCard dto) {
        D1100 d1100 = getD1100(idx_corp);
        if (ObjectUtils.isEmpty(d1100)) {
            return d1100;
        }
        return repoD1100.save(d1100
                .setD020(grantLimit)
                .setD029(dto.getReceiveType().getCode())
                .setD031(dto.getZipCode().substring(0, 3))
                .setD032(dto.getZipCode().substring(3))
                .setD033(dto.getAddressBasic())
                .setD034(dto.getAddressDetail())
                .setD039(dto.getCount() + "")
                .setD046(Const.CARD_RECEIVE_ADDRESS_CODE)
                .setD047(dto.getAddressKey())
        );
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
    public CardIssuanceDto.AccountRes registerAccount(Long idx_user, CardIssuanceDto.RegisterAccount dto, Long idx_CardInfo) {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        ResAccount account = findResAccount(dto.getAccountIdx());
        if (ObjectUtils.isEmpty(account.resAccountHolder())) {
            account.resAccountHolder(dto.getAccountHolder());
        }

        updateD1100Account(user.corp().idx(), account);

        cardInfo.bankAccount(BankAccount.builder()
                .bankAccount(account.resAccount())
                .bankCode(account.organization())
                .bankAccountHolder(account.resAccountHolder())
                .build());

        return CardIssuanceDto.AccountRes.from(repoCardIssuance.save(cardInfo), getBankName(account.organization()));
    }

    private D1100 updateD1100Account(Long idx_corp, ResAccount account) {
        D1100 d1100 = getD1100(idx_corp);
        if (ObjectUtils.isEmpty(d1100) || ObjectUtils.isEmpty(account)) {
            return d1100;
        }
        String bankCode = account.organization();
        if (bankCode != null && bankCode.length() > 3) {
            bankCode = bankCode.substring(bankCode.length() - 3);
        }
        return repoD1100.save(d1100
                .setD024(bankCode)
                .setD025(Seed128.encryptEcb(account.resAccount()))
                .setD026(account.resAccountHolder())
        );
    }

    /**
     * 대표자 정보
     *
     * @param idx_user 조회하는 User idx
     * @return 등록 정보
     */
    @Transactional(readOnly = true)
    public CardIssuanceDto.CeoTypeRes getCeoType(Long idx_user) {
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
        return CardIssuanceDto.CeoTypeRes.builder()
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
    public CardIssuanceDto.CeoRes registerCeo(Long idx_user, CardIssuanceDto.RegisterCeo dto, Long idx_CardInfo) {
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
        ceoNum = updateD1000Ceo(d1000, user.corp().idx(), dto, ceo, ceoNum);

        D1400 d1400 = getD1400(user.corp().idx());
        ceoNum = updateD1400Ceo(d1400, dto, ceo, ceoNum);

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

        return CardIssuanceDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
    }

    private Integer updateD1000Ceo(D1000 d1000, Long idx_corp, CardIssuanceDto.RegisterCeo dto, CeoInfo ceo, Integer ceoNum) {
        if (d1000 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1000.getD012()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
            repoD1000.save(d1000
                    .setD010(dto.getName())                     //대표자명1
                    .setD012(dto.getEngName())                  //대표자영문명1
                    .setD013(dto.getNation())                   //대표자국적코드1
                    .setD035(dto.getName())                     //신청관리자명
                    .setD040(dto.getPhoneNumber().substring(0, 3))      //신청관리자휴대전화식별번호
                    .setD041(dto.getPhoneNumber().substring(3, 7))      //신청관리자휴대전화국번호
                    .setD042(dto.getPhoneNumber().substring(7))         //신청관리자휴대전화고유번호
            );
            ceoNum = 1;

            updateD1100Ceo(idx_corp, dto);

        } else if (!StringUtils.hasText(d1000.getD016()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
            repoD1000.save(d1000
                    .setD014(dto.getName())         //대표자명2
                    .setD016(dto.getEngName())      //대표자영문명2
                    .setD017(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1000.getD020()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
            repoD1000.save(d1000
                    .setD018(dto.getName())         //대표자명3
                    .setD020(dto.getEngName())      //대표자영문명3
                    .setD021(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }


        return ceoNum;
    }

    private D1100 updateD1100Ceo(Long idx_corp, CardIssuanceDto.RegisterCeo dto) {
        D1100 d1100 = getD1100(idx_corp);
        if (d1100 != null) {
            d1100 = repoD1100.save(d1100
                    .setD035(dto.getPhoneNumber().substring(0, 3))
                    .setD036(dto.getPhoneNumber().substring(3, 7))
                    .setD037(dto.getPhoneNumber().substring(7))
            );
        }
        return d1100;
    }

    private Integer updateD1400Ceo(D1400 d1400, CardIssuanceDto.RegisterCeo dto, CeoInfo ceo, Integer ceoNum) {
        if (d1400 == null) {
            return ceoNum;
        }

        if (!StringUtils.hasText(d1400.getD035()) || (ceo != null && ceo.ceoNumber().equals(1))) { // 첫번째 대표자정보
            repoD1400.save(d1400
                    .setD032(dto.getName())                     //대표자명1
                    .setD034(dto.getEngName())                  //대표자영문명1
                    .setD035(dto.getNation())                   //대표자국적코드1
                    .setD057(dto.getName())                     //신청관리자명
                    .setD062(dto.getPhoneNumber().substring(0, 3))      //신청관리자휴대전화식별번호
                    .setD063(dto.getPhoneNumber().substring(3, 7))      //신청관리자휴대전화국번호
                    .setD064(dto.getPhoneNumber().substring(7))         //신청관리자휴대전화고유번호
            );
            ceoNum = 1;

        } else if (!StringUtils.hasText(d1400.getD039()) || (ceo != null && ceo.ceoNumber().equals(2))) { // 두번째 대표자정보
            repoD1400.save(d1400
                    .setD036(dto.getName())         //대표자명2
                    .setD038(dto.getEngName())      //대표자영문명2
                    .setD039(dto.getNation())       //대표자국적코드2
            );
            ceoNum = 2;

        } else if (!StringUtils.hasText(d1400.getD043()) || (ceo != null && ceo.ceoNumber().equals(3))) { // 세번째 대표자정보
            repoD1400.save(d1400
                    .setD040(dto.getName())         //대표자명3
                    .setD042(dto.getEngName())      //대표자영문명3
                    .setD043(dto.getNation())       //대표자국적코드3
            );
            ceoNum = 3;
        }

        return ceoNum;
    }

    /**
     * 카드발급정보 전체 조회
     *
     * @param idx_user 조회하는 User idx
     * @return 카드발급정보
     */
    @Transactional(readOnly = true)
    public CardIssuanceDto.CardIssuanceInfoRes getCardIssuanceInfoByUser(Long idx_user) {
        User user = findUser(idx_user);
		CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.getTopByUserAndDisabledFalseOrderByIdxDesc(user);

        List<CardIssuanceDto.ConsentRes> consentInfo = getConsentRes(idx_user);

        if (cardIssuanceInfo != null) {
            return CardIssuanceDto.CardIssuanceInfoRes.builder()
                    .consentRes(consentInfo)
                    .corporationRes(getCorporationRes(cardIssuanceInfo))
                    .ventureRes(CardIssuanceDto.VentureRes.from(cardIssuanceInfo))
                    .stockholderRes(CardIssuanceDto.StockholderRes.from(cardIssuanceInfo))
                    .cardRes(CardIssuanceDto.CardRes.from(cardIssuanceInfo))
                    .accountRes(CardIssuanceDto.AccountRes.from(cardIssuanceInfo, getBankName(!ObjectUtils.isEmpty(cardIssuanceInfo.bankAccount()) ? cardIssuanceInfo.bankAccount().getBankCode() : null)))
                    .ceoRes(cardIssuanceInfo.ceoInfos() != null ? cardIssuanceInfo.ceoInfos().stream().map(CardIssuanceDto.CeoRes::from).collect(Collectors.toList()) : null)
                    .stockholderFileRes(cardIssuanceInfo.stockholderFiles() != null ? cardIssuanceInfo.stockholderFiles().stream().map(file -> CardIssuanceDto.StockholderFileRes.from(file, cardIssuanceInfo.idx())).collect(Collectors.toList()) : null)
                    .build();

        } else {
            return CardIssuanceDto.CardIssuanceInfoRes.builder()
                    .consentRes(consentInfo)
                    .corporationRes(CardIssuanceDto.CorporationRes.from(user.corp(), null)).build();
        }
    }

    private List<CardIssuanceDto.ConsentRes> getConsentRes(Long idx_user) {
        List<CardIssuanceDto.ConsentRes> consentInfo = new ArrayList<>();

        List<BrandConsentDto> consents = repoConsent.findAllByEnabledOrderByConsentOrderAsc(true)
                .map(BrandConsentDto::from)
                .collect(Collectors.toList());

        consents.forEach(item -> {
            ConsentMapping consentMapping = repoConsentMapping.findTopByIdxUserAndIdxConsentOrderByIdxDesc(idx_user, item.getIdx());

            CardIssuanceDto.ConsentRes resTemp = CardIssuanceDto.ConsentRes.builder()
                    .consentIdx(item.getIdx())
                    .title(item.getTitle())
                    .boolConsent(consentMapping != null ? consentMapping.status() : false)
                    .consentType(item.getTypeCode())
                    .build();
            consentInfo.add(resTemp);
        });

        return consentInfo;
    }

    private CardIssuanceDto.CorporationRes getCorporationRes(CardIssuanceInfo cardIssuanceInfo) {
        if (ObjectUtils.isEmpty(cardIssuanceInfo.corp())) {
            return null;
        }
        CardIssuanceDto.CorporationRes corporationRes = CardIssuanceDto.CorporationRes.from(cardIssuanceInfo.corp(), cardIssuanceInfo.idx());
        if (!ObjectUtils.isEmpty(corporationRes.getBusinessCode())) {
            CommonCodeDetail codeDetailData = repoCodeDetail.getByCode1AndCode5(corporationRes.getBusinessCode().substring(0, 1), corporationRes.getBusinessCode().substring(1));
            corporationRes.setBusinessCodeValue(codeDetailData.value5());
        }
        return corporationRes;
    }

    private String getBankName(String bankCode) {
        CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.BANK_1, bankCode);
        if (commonCodeDetail != null) {
            return commonCodeDetail.value1();
        }
        return null;
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

    /**
     * 신한 운전면허 지역코드 조회
     *
     * @return 신한 운전면허 지역코드 목록
     */
    @Transactional(readOnly = true)
    public List<CardIssuanceDto.ShinhanDriverLocalCode> getShinhanDriverLocalCodes() {
        return repoCodeDetail.findAllByCode(CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).stream().map(CardIssuanceDto.ShinhanDriverLocalCode::from).collect(Collectors.toList());
    }

    /**
     * 대표자 타당성 확인
     *
     * @param idx_user 조회하는 User idx
     * @param dto      대표자 타당성 확인 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public void verifyValidCeo(Long idx_user, CardIssuanceDto.CeoValidReq dto) {
        User user = findUser(idx_user);
        if (ObjectUtils.isEmpty(user.corp())) {
            throw EntityNotFoundException.builder().entity("Corp").build();
        }

        CardIssuanceInfo cardIssuanceInfo = findCardIssuanceInfo(user.corp());
        cardIssuanceInfo.ceoInfos().forEach(ceoInfo -> {
            if (dto.getPhoneNumber().equals(ceoInfo.phoneNumber())) {
                throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "ALREADY_AUTH_CEO");
            }
        });

        // 스크래핑 데이터와 입력 데이터 일치여부 확인
        verifyCorrespondCeo(user.corp().idx(), dto);
    }

    public void verifyCorrespondCeo(Long idx_corp, CardIssuanceDto.CeoValidReq dto) {
        D1000 d1000 = getD1000(idx_corp);
        if (ObjectUtils.isEmpty(d1000)) {
            throw EntityNotFoundException.builder().entity("D1000").build();
        }

        boolean isValidCeoInfo = !checkCeo(d1000.getD010(), d1000.getD012(), d1000.getD011(), dto)
                && !checkCeo(d1000.getD014(), d1000.getD016(), d1000.getD015(), dto)
                && !checkCeo(d1000.getD018(), d1000.getD020(), d1000.getD019(), dto);

        if (isValidCeoInfo) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "MISMATCH_CEO");
        }
    }

    private boolean checkCeo(String korName, String engName, String idNum, CardIssuanceDto.CeoValidReq dto) {
        if (!StringUtils.hasText(idNum)) {
            return false;
        }

        idNum = Seed128.decryptEcb(idNum);

        if ((dto.getName().equals(korName) || dto.getName().equals(engName)) && dto.getIdentificationNumberFront().substring(0, 6).equals(idNum.substring(0, 6))) {
            return true;
        }
        return false;
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
