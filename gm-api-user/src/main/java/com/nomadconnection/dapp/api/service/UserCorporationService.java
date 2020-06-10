package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.UserCorporationDto;
import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.api.exception.EmptyResxException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.MismatchedException;
import com.nomadconnection.dapp.core.domain.*;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.Card;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.*;
import com.nomadconnection.dapp.core.domain.embed.BankAccount;
import com.nomadconnection.dapp.core.domain.repository.*;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1000Repository;
import com.nomadconnection.dapp.core.domain.repository.shinhan.D1100Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCorporationService {

    private final UserRepository repoUser;
    private final CorpRepository repoCorp;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final D1000Repository repoD1000;
    private final RiskConfigRepository repoRisk;
    private final D1100Repository repoD1100;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final CeoInfoRepository repoCeo;
    private final VentureBusinessRepository repoVenture;
    private final StockholderFileRepository repoFile;

    private final AwsS3Service s3Service;
    private final GwUploadService gwUploadService;

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
    public UserCorporationDto.CorporationRes registerCorporation(Long idx_user, UserCorporationDto.RegisterCorporation dto, Long idx_CardInfo) {
        User user = findUser(idx_user);

        D1000 d1000 = getD1000(user.corp().idx());
        Corp corp = repoCorp.save(user.corp()
                .resCompanyEngNm(dto.getEngCorName())
                .resCompanyNumber(dto.getCorNumber())
                .resBusinessCode(dto.getBusinessCode())
                .resUserType(d1000 != null ? d1000.d009() : null)
        );

        CardIssuanceInfo cardInfo;
        try {
            cardInfo = findCardIssuanceInfo(user.corp());
            if (!cardInfo.idx().equals(idx_CardInfo)) {
                throw MismatchedException.builder().build();
            }

        } catch (EntityNotFoundException e) {
            cardInfo = repoCardIssuance.save(CardIssuanceInfo.builder().corp(corp).build());
            if (d1000 != null) {
                String[] corNumber = dto.getCorNumber().split("-");
                repoD1000.save(d1000
                        .d006(!StringUtils.hasText(d1000.d006()) ? dto.getEngCorName() : d1000.d006())
                        .d008(!StringUtils.hasText(d1000.d008()) ? dto.getBusinessCode() : d1000.d008())
                        .d026(!StringUtils.hasText(d1000.d026()) ? corNumber[0] : d1000.d026())
                        .d027(!StringUtils.hasText(d1000.d027()) ? corNumber[1] : d1000.d027())
                        .d028(!StringUtils.hasText(d1000.d028()) ? corNumber[2] : d1000.d028())
                );
            }
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
        Optional<RiskConfig> riskConfig = repoRisk.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRisk.save(riskConfig.get()
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .vcInvestment(dto.getIsVC())
            );
        } else {
            repoRisk.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .ventureCertification(dto.getIsVerifiedVenture())
                    .vcInvestment(dto.getIsVC())
                    .build()
            );
        }

        // TODO: 전문저장
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

        Optional<RiskConfig> riskConfig = repoRisk.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRisk.save(riskConfig.get()
                    .isStockHold25(dto.getIsHold25())
                    .isStockholderList(dto.getIsStockholderList())
                    .isStockholderPersonal(dto.getIsPersonal())
            );
        } else {
            repoRisk.save(RiskConfig.builder()
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
            repoD1000.save(d1000.d059(dto.getName())
                    .d060(dto.getEngName())
                    .d061(dto.getBirth())
                    .d062(dto.getNation())
                    .d065(dto.getRate())
                    .d066("KR".equalsIgnoreCase(dto.getNation()) ? "N" : "Y")
            );
        }

        return UserCorporationDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    /**
     * 주주명부 파일 등록
     *
     * @param idx_user     등록하는 User idx
     * @param files        파일
     * @param type         file type
     * @param idx_CardInfo CardIssuanceInfo idx
     * @return 등록 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public List<UserCorporationDto.StockholderFileRes> uploadStockholderFile(Long idx_user, MultipartFile[] files, String type, Long idx_CardInfo) throws IOException {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }
        List<StockholderFile> fileList = repoFile.findAllByCorp(user.corp());
        if (fileList != null && fileList.size() > 2) {
            throw BadRequestedException.builder().category(BadRequestedException.Category.EXCESS_UPLOAD_FILE_COUNT).build();
        }

        if (files == null || ObjectUtils.isEmpty(files)) {
            throw EmptyResxException.builder().build();
        }

        if (!ObjectUtils.isEmpty(fileList)) {
            for (StockholderFile file : fileList) {
                repoFile.delete(file);
                gwUploadService.delete(file.fname(), cardInfo.cardCode());
                s3Service.s3FileDelete(file.s3Key());
            }
        }
        List<UserCorporationDto.StockholderFileRes> resultList = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            File uploadFile = new File(fileName);
            uploadFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(uploadFile);
            fos.write(file.getBytes());
            fos.close();
            try {
                gwUploadService.upload(uploadFile, cardInfo.cardCode());

                String s3Key = "stockholder/" + idx_CardInfo + "/" + fileName;
                String s3Link = s3Service.s3FileUpload(uploadFile, s3Key);

                uploadFile.delete();

                resultList.add(UserCorporationDto.StockholderFileRes.from(repoFile.save(StockholderFile.builder()
                        .cardIssuanceInfo(cardInfo)
                        .corp(user.corp())
                        .fname(fileName)
                        .type(StockholderFileType.valueOf(type))
                        .s3Link(s3Link)
                        .s3Key(s3Key)
                        .size(file.getSize())
                        .orgfname(file.getOriginalFilename()).build()), cardInfo.idx()));

            } catch (Exception e) {
                uploadFile.delete();
                log.error("[uploadStockholderFile] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                throw e;
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
        gwUploadService.delete(file.fname(), cardInfo.cardCode());
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

        Optional<RiskConfig> riskConfig = repoRisk.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            repoRisk.save(riskConfig.get()
                    .calculatedLimit(dto.getCalAmount())
                    .hopeLimit(dto.getAmount())
                    .grantLimit(dto.getGrantAmount())
            );
        } else {
            repoRisk.save(RiskConfig.builder()
                    .user(user)
                    .corp(user.corp())
                    .calculatedLimit(dto.getCalAmount())
                    .hopeLimit(dto.getAmount())
                    .grantLimit(dto.getGrantAmount())
                    .build()
            );
        }

        D1000 d1000 = getD1000(user.corp().idx());
        if (d1000 != null) {
            repoD1000.save(d1000
                    .d022(dto.getZipCode().substring(0, 3))
                    .d023(dto.getZipCode().substring(3))
                    .d024(dto.getAddressBasic())
                    .d025(dto.getAddressDetail())
                    .d055(dto.getAddressKey())
            );
        }

        D1100 d1100 = getD1100(user.corp().idx());
        if (d1100 != null) {
            repoD1100.save(d1100
                    .d029(dto.getReceiveType().getCode())
                    .d031(dto.getZipCode().substring(0, 3))
                    .d032(dto.getZipCode().substring(3))
                    .d033(dto.getAddressBasic())
                    .d034(dto.getAddressDetail())
                    .d020(dto.getGrantAmount())
                    .d039(dto.getCount() + "")
                    .d046(Const.CARD_RECEIVE_ADDRESS_CODE)
                    .d047(dto.getAddressKey())
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

        String bankCode = dto.getBank();
        if (bankCode.length() > 3) {
            bankCode = bankCode.substring(bankCode.length() - 3);
        }

        cardInfo.bankAccount(BankAccount.builder()
                .bankAccount(dto.getAccountNumber())
                .bankCode(bankCode)
                .bankAccountHolder(dto.getAccountHolder())
                .build());

        D1100 d1100 = getD1100(user.corp().idx());
        if (d1100 != null) {
            repoD1100.save(d1100
                    .d024(bankCode)
                    .d025(dto.getAccountNumber())
                    .d026(dto.getAccountHolder())
            );
        }
        return UserCorporationDto.AccountRes.from(repoCardIssuance.save(cardInfo));
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
        D1000 d1000 = getD1000(user.corp().idx());
        Integer count = 1;
        String ceoType = "1";
        if (d1000 != null) {
            ceoType = d1000.d009();
            if (StringUtils.hasText(d1000.d010()) && StringUtils.hasText(d1000.d014()) && StringUtils.hasText(d1000.d018())) {
                count = 3;
            } else if (StringUtils.hasText(d1000.d010()) && StringUtils.hasText(d1000.d014()) && !StringUtils.hasText(d1000.d018())) {
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
    public UserCorporationDto.CeoRes registerCeo(Long idx_user, UserCorporationDto.RegisterCeo dto, Long idx_CardInfo) throws IOException {
        User user = findUser(idx_user);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user.corp());
        if (!cardInfo.idx().equals(idx_CardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        CeoInfo ceoInfo = CeoInfo.builder()
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
                .build();

        D1000 d1000 = getD1000(user.corp().idx());
        if (d1000 != null) {
            if (!StringUtils.hasText(d1000.d012())) {
                repoD1000.save(d1000
                        .d010(dto.getName())
                        .d012(dto.getEngName())
                        .d013(dto.getNation())
                        .d035(dto.getName())
                        .d036(dto.getPhoneNumber().substring(0, 3))
                        .d037(dto.getPhoneNumber().substring(3, 6))
                        .d038(dto.getPhoneNumber().substring(6))
                        .d040(dto.getPhoneNumber().substring(0, 3))
                        .d041(dto.getPhoneNumber().substring(3, 6))
                        .d042(dto.getPhoneNumber().substring(6))
                );
            }
            if (!StringUtils.hasText(d1000.d016())) {
                repoD1000.save(d1000
                        .d014(dto.getName())
                        .d016(dto.getEngName())
                        .d017(dto.getNation())
                );
            }
            if (!StringUtils.hasText(d1000.d020())) {
                repoD1000.save(d1000
                        .d018(dto.getName())
                        .d020(dto.getEngName())
                        .d021(dto.getNation())
                );
            }
        }

        D1100 d1100 = getD1100(user.corp().idx());
        if (d1100 != null && !StringUtils.hasText(d1100.d035())) {
            repoD1100.save(d1100
                    .d035(dto.getPhoneNumber().substring(0, 3))
                    .d036(dto.getPhoneNumber().substring(3, 6))
                    .d037(dto.getPhoneNumber().substring(6))
            );
        }

        return UserCorporationDto.CeoRes.from(repoCeo.save(ceoInfo)).setDeviceId("");
    }

    /**
     * 카드발급정보 전체 조회
     *
     * @param idx_cardIssuanceInfo 조회하는 CardIssuanceInfo idx
     * @return 카드발급정보
     */
    @Transactional(readOnly = true)
    public UserCorporationDto.CardIssuanceInfoRes getCardIssuanceInfo(Long idx_cardIssuanceInfo) {
        CardIssuanceInfo cardIssuanceInfo = repoCardIssuance.findById(idx_cardIssuanceInfo).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .idx(idx_cardIssuanceInfo)
                        .entity("CardIssuanceInfo")
                        .build()
        );
        return UserCorporationDto.CardIssuanceInfoRes.builder()
                .corporationRes(UserCorporationDto.CorporationRes.from(cardIssuanceInfo.corp(), idx_cardIssuanceInfo))
                .ventureRes(UserCorporationDto.VentureRes.from(cardIssuanceInfo))
                .stockholderRes(UserCorporationDto.StockholderRes.from(cardIssuanceInfo))
                .cardRes(UserCorporationDto.CardRes.from(cardIssuanceInfo))
                .accountRes(UserCorporationDto.AccountRes.from(cardIssuanceInfo))
                .ceoRes(cardIssuanceInfo.ceoInfos().stream().map(UserCorporationDto.CeoRes::from).collect(Collectors.toList()))
                .stockholderFileRes(cardIssuanceInfo.stockholderFiles().stream().map(file -> UserCorporationDto.StockholderFileRes.from(file, idx_cardIssuanceInfo)).collect(Collectors.toList()))
                .build();
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
        if (cardIssuanceInfo != null) {
            return UserCorporationDto.CardIssuanceInfoRes.builder()
                    .corporationRes(UserCorporationDto.CorporationRes.from(cardIssuanceInfo.corp(), cardIssuanceInfo.idx()))
                    .ventureRes(UserCorporationDto.VentureRes.from(cardIssuanceInfo))
                    .stockholderRes(UserCorporationDto.StockholderRes.from(cardIssuanceInfo))
                    .cardRes(UserCorporationDto.CardRes.from(cardIssuanceInfo))
                    .accountRes(UserCorporationDto.AccountRes.from(cardIssuanceInfo))
                    .ceoRes(cardIssuanceInfo.ceoInfos() != null ? cardIssuanceInfo.ceoInfos().stream().map(UserCorporationDto.CeoRes::from).collect(Collectors.toList()) : null)
                    .stockholderFileRes(cardIssuanceInfo.stockholderFiles() != null ? cardIssuanceInfo.stockholderFiles().stream().map(file -> UserCorporationDto.StockholderFileRes.from(file, cardIssuanceInfo.idx())).collect(Collectors.toList()) : null)
                    .build();
        } else {
            return UserCorporationDto.CardIssuanceInfoRes.builder()
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
        return repoVenture.findAllByOrderByNameAsc().stream().map(ventureBusiness -> ventureBusiness.name()).collect(Collectors.toList());
    }


    private User findUser(Long idx_user) {
        return repoUser.findById(idx_user).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("User")
                        .idx(idx_user)
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

    private StockholderFile findStockholderFile(Long idx_file) {
        return repoFile.findById(idx_file).orElseThrow(
                () -> EntityNotFoundException.builder()
                        .entity("StockholderFile")
                        .idx(idx_file)
                        .build()
        );
    }

}
