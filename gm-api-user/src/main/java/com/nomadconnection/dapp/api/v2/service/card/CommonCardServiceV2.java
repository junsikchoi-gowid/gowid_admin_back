package com.nomadconnection.dapp.api.v2.service.card;


import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.dto.FinancialConsumersRequestDto;
import com.nomadconnection.dapp.api.dto.FinancialConsumersResponseDto;
import com.nomadconnection.dapp.api.dto.shinhan.DataPart1700;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.AwsS3Service;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.service.FinancialConsumersService;
import com.nomadconnection.dapp.api.service.GwUploadService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.dto.cardissuanceinfo.IssuanceDepthResponseDto;
import com.nomadconnection.dapp.api.v2.enums.CeoVerifyCode;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
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
import com.nomadconnection.dapp.core.domain.repository.corp.ManagerRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.VentureBusinessRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.risk.RiskConfig;
import com.nomadconnection.dapp.core.domain.shinhan.D1000;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.dto.response.ErrorCode;
import com.nomadconnection.dapp.core.encryption.shinhan.Seed128;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import com.nomadconnection.dapp.core.utils.NumberUtils;
import com.nomadconnection.dapp.secukeypad.EncryptParam;
import com.nomadconnection.dapp.secukeypad.SecuKeypad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nomadconnection.dapp.api.v2.utils.CardCommonUtils.isRealOwnerConvertCeo;
import static com.nomadconnection.dapp.api.v2.utils.CardCommonUtils.isStockholderUpdateCeo;
import static com.nomadconnection.dapp.core.domain.card.CardCompany.getStockRate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCardServiceV2 {

    private final UserRepository repoUser;
    private final CardIssuanceInfoRepository repoCardIssuance;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final VentureBusinessRepository repoVenture;
    private final StockholderFileRepository repoFile;
    private final ConsentRepository repoConsent;
    private final ConsentMappingRepository repoConsentMapping;
    private final ResAccountRepository repoResAccount;
    private final CorpRepository repoCorp;
    private final RiskRepository repoRisk;
    private final RiskConfigRepository repoRiskConfig;
    private final CeoInfoRepository repoCeo;
    private final ManagerRepository repoManager;

    private final AwsS3Service s3Service;
    private final GwUploadService gwUploadService;
    private final IssuanceService issuanceService;

    private final CardIssuanceInfoService cardIssuanceInfoService;

    // v2
    private final ShinhanCardServiceV2 shinhanCardService;
    private final LotteCardServiceV2 lotteCardService;
    private final FinancialConsumersService financialConsumersService;

    private final EnvUtil envUtil;

    @Value("${stockholder.file.size}")
    private Long STOCKHOLDER_FILE_SIZE;
    @Value("${stockholder.file.type}")
    private String[] STOCKHOLDER_FILE_TYPE;

    /**
     * ???????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CorporationRes updateCorporation(Long idxUser, CardIssuanceDto.RegisterCorporation dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        if (CardCompany.isShinhan(user.cardCompany())) {
            shinhanCardService.updateD1000Corp(cardInfo, dto);
            shinhanCardService.updateD1400Corp(cardInfo, dto);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateD1100Corp(user.corp().idx(), dto);
        }

        Corp corp = repoCorp.save(user.corp()
            .resCompanyEngNm(dto.getEngCorName())
            .resCompanyNumber(dto.getCorNumber())
            .resCompanyZipCode(dto.getCorZipCode())
            .resCompanyAddr(dto.getCorAddr())
            .resCompanyAddrDt(dto.getCorAddrDt())
            .resCompanyBuildingCode(dto.getCorBuildingCode())
            .resBusinessCode(dto.getBusinessCode())
        );

        return CardIssuanceDto.CorporationRes.from(corp, cardInfo.idx());
    }

    /**
     * ???????????? ???????????? ??????
     *
     * @return ??????????????? ??????
     */
    @Transactional(readOnly = true)
    public List<CardIssuanceDto.BusinessType> getBusinessType() {
        return repoCodeDetail.findAllByCode(CommonCodeType.BUSINESS_1).stream().map(CardIssuanceDto.BusinessType::from).collect(Collectors.toList());
    }

    /**
     * ???????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CorporationExtendRes updateCorporationExtend(Long idxUser, CardIssuanceDto.RegisterCorporationExtend dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);

        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo = repoCardIssuance.save(cardInfo.corpExtend(CorpExtend.builder()
            .isVirtualCurrency(dto.getIsVirtualCurrency())
            .isListedCompany(dto.getIsListedCompany())
            .listedCompanyCode(dto.getListedCompanyCode())
            .build())
        );

        if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateD1100CorpExtend(user.corp().idx(), dto);
        }

        return CardIssuanceDto.CorporationExtendRes.from(cardInfo, getListedCompanyName(dto.getListedCompanyCode()));
    }

    /**
     * ???????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.VentureRes registerStockholder(Long idxUser, CardIssuanceDto.RegisterStockholder dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        cardInfo = cardInfo.stockholder(Stockholder.builder()
            .isStockHold25(dto.getIsHold25())
            .isStockholderList(dto.getIsStockholderList())
            .isStockholderPersonal(dto.getIsPersonal())
            .stockholderName(dto.getName())
            .stockholderEngName(dto.getEngName())
            .stockholderBirth(dto.getBirth())
            .stockholderNation(dto.getNation())
            .stockRate(dto.getRate())
            .build());

        List<CeoInfo> ceoInfos = repoCeo.getByCardIssuanceInfo(cardInfo);
        for (CeoInfo ceoInfo : ceoInfos) {
            if (isRealOwnerConvertCeo(cardInfo, ceoInfo)) {
                cardInfo = setStockholderByCeoInfo(cardInfo, ceoInfo, getStockRate(user.cardCompany()));
                break;
            }
        }

        updateRiskConfigStockholder(user, dto);
        if (CardCompany.isShinhan(user.cardCompany())) {
            shinhanCardService.updateD1000Stockholder(user.corp().idx(), cardInfo, ceoInfos, dto);
            shinhanCardService.updateD1400Stockholder(user.corp().idx(), cardInfo, ceoInfos, dto);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateD1100Stockholder(user.corp().idx(), cardInfo, ceoInfos, dto);
        }

        return CardIssuanceDto.VentureRes.from(repoCardIssuance.save(cardInfo));
    }

    private CardIssuanceInfo setStockholderByCeoInfo(CardIssuanceInfo cardIssuanceInfo, CeoInfo ceoInfo, String stockRate) {
        return cardIssuanceInfo.stockholder(cardIssuanceInfo.stockholder()
            .stockholderName(ceoInfo.name())
            .stockholderEngName(ceoInfo.engName())
            .stockholderBirth(ceoInfo.birth())
            .stockholderNation(ceoInfo.nationality())
            .stockRate(stockRate));
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

    /**
     * ???????????? ?????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param file_1       ??????1
     * @param file_2       ??????2
     * @param type         file type
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(noRollbackFor = FileUploadException.class)
    public List<CardIssuanceDto.StockholderFileRes> registerStockholderFile(Long idxUser, MultipartFile[] file_1, MultipartFile[] file_2, String type, Long idxCardInfo, CardType cardType) throws IOException {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        if (ObjectUtils.isEmpty(file_1) && ObjectUtils.isEmpty(file_2)) {
            throw EmptyResxException.builder().build();
        }

        StockholderFileType fileType = StockholderFileType.valueOf(type);

        List<StockholderFile> fileList = repoFile.findAllByCardIssuanceInfoAndType(cardInfo, fileType);
        if (!ObjectUtils.isEmpty(fileList)) {
            for (StockholderFile file : fileList) {
                repoFile.delete(file);
                s3Service.s3FileDelete(file.s3Key());
                gwUploadService.delete(cardInfo.cardCompany(), file.fname());
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
        String licenseNo = CommonUtil.replaceHyphen(cardInfo.corp().resCompanyIdentityNo());
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
        String gwFileCode = getGwFileCode(cardInfo.cardCompany());
        String fileName = makeStockholderFileName(file, sendGwUpload, licenseNo, sequence, gwFileCode);
        String s3Key = "stockholder/" + cardInfo.idx() + "/" + fileName;

        File uploadFile = new File(fileName);
        uploadFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(uploadFile);
        fos.write(file.getBytes());
        fos.close();

        try {
            String s3Link = s3Service.s3FileUpload(uploadFile, s3Key);

            if (file.getSize() <= STOCKHOLDER_FILE_SIZE && Arrays.asList(STOCKHOLDER_FILE_TYPE)
                .contains(FilenameUtils.getExtension(fileName).toLowerCase())) {
                gwUploadService.upload(cardInfo.cardCompany(), uploadFile, gwFileCode, licenseNo);
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
            gwUploadService.delete(cardInfo.cardCompany(), fileName);

            log.error("[uploadStockholderFile] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw FileUploadException.builder().category(FileUploadException.Category.UPLOAD_STOCKHOLDER_FILE).build();
        }
    }

    private String makeStockholderFileName(MultipartFile file, boolean sendGwUpload, String licenseNo, String sequence, String gwFileCode) {
        if (file.getSize() > STOCKHOLDER_FILE_SIZE || sendGwUpload) {
            return licenseNo + gwFileCode + sequence + "_back." + FilenameUtils.getExtension(file.getOriginalFilename());
        }
        return licenseNo + gwFileCode + sequence + "." + FilenameUtils.getExtension(file.getOriginalFilename());
    }

    private String getGwFileCode(CardCompany cardCompany) {
        switch (cardCompany) {
            case SHINHAN:
                return Const.SHINHAN_STOCKHOLDER_GW_FILE_CODE;
            case LOTTE:
                return Const.LOTTE_STOCKHOLDER_GW_FILE_CODE;
            default:
                return "";
        }
    }

    @Transactional(readOnly = true)
    public List<CardIssuanceDto.CardIssuanceInfoRes> getCards(Long idxUser) {
        User user = findUser(idxUser);
        Corp corp = user.corp();
        Optional<List<CardIssuanceInfo>> cardIssuanceInfos =
                corp != null ? repoCardIssuance.findAllByCorp(corp) : repoCardIssuance.findAllByUser(user);
        List<CardIssuanceDto.ConsentRes> consents = getConsentRes(idxUser);

        List<CardIssuanceDto.CardIssuanceInfoRes> response =
            Stream.of(CardIssuanceDto.CardIssuanceInfoRes.toDefault(consents)).collect(Collectors.toList());

        if (cardIssuanceInfos.isPresent()) {
            response = cardIssuanceInfos.get()
                .stream()
                .map(cardIssuanceInfo ->
                    CardIssuanceDto.CardIssuanceInfoRes.toDto(
                        cardIssuanceInfo,
                        consents,
                        getCorporationRes(cardIssuanceInfo),
                        CardIssuanceDto.CorporationExtendRes.from(cardIssuanceInfo, getListedCompanyName(!ObjectUtils.isEmpty(cardIssuanceInfo.corpExtend()) ? cardIssuanceInfo.corpExtend().listedCompanyCode() : null)),
                        CardIssuanceDto.AccountRes.from(cardIssuanceInfo, getBankName(!ObjectUtils.isEmpty(cardIssuanceInfo.bankAccount()) ? cardIssuanceInfo.bankAccount().getBankCode() : null))
                        )
                )
                .collect(Collectors.toList());
        }

        return response;
    }

    /**
     * ?????????????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CardRes registerCard(Long idxUser, CardIssuanceDto.RegisterCard dto, Long idxCardInfo, CardType cardType) {

        try {
            User user = findUser(idxUser);
            CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
            if (!cardInfo.idx().equals(idxCardInfo)) {
                throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
            }

            // front cross check ????????????
            if (CardCompany.isLotte(user.cardCompany())) {
                if (ObjectUtils.isEmpty(dto.getGreenCount()) && ObjectUtils.isEmpty(dto.getBlackCount()) &&
                    ObjectUtils.isEmpty(dto.getGreenTrafficCount()) && ObjectUtils.isEmpty(dto.getBlackTrafficCount())) {
                    throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, "Green, Black, GreenTraffic or BlackTraffic, One of them must exist");
                }
            }

            // ????????????
            Double calculatedLimit = Optional.ofNullable(repoRisk.findCardLimitNowFirst(idxUser, CommonUtil.getNowYYYYMMDD())).orElse(0D);

            String strCalculatedLimit = NumberUtils.doubleToString(calculatedLimit);

            // ????????????
            String hopeLimit = "0";
            if (!ObjectUtils.isEmpty(cardInfo.card()) && !ObjectUtils.isEmpty(cardInfo.card().hopeLimit())) {
                hopeLimit = cardInfo.card().hopeLimit();
            }

            // ????????????
            String grantLimit = calculatedLimit < Double.valueOf(hopeLimit) ? strCalculatedLimit : hopeLimit;

            updateRiskConfigCard(user, grantLimit, strCalculatedLimit, hopeLimit);
            if (CardCompany.isShinhan(user.cardCompany())) {
                grantLimit = calculateShinhanGrantLimit(grantLimit);
                shinhanCardService.updateShinhanFulltextCard(cardInfo, grantLimit, dto);
                shinhanCardService.setCardInfoCard(cardInfo, dto, strCalculatedLimit, grantLimit);
            } else if (CardCompany.isLotte(user.cardCompany())) {
                lotteCardService.updateD1100Card(user, grantLimit, strCalculatedLimit, hopeLimit, dto);
                lotteCardService.setCardInfoCard(cardInfo, dto, strCalculatedLimit, grantLimit);
            }

            return CardIssuanceDto.CardRes.from(repoCardIssuance.save(cardInfo));

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
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

    private String getListedCompanyName(String ListedCompanyCode) {
        CommonCodeDetail commonCodeDetail = repoCodeDetail.getByCodeAndCode1(CommonCodeType.LOTTE_LISTED_EXCHANGE, ListedCompanyCode);
        if (ObjectUtils.isEmpty(commonCodeDetail)) {
            return null;
        }
        return commonCodeDetail.value1();
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
                .boolConsent(consentMapping != null && consentMapping.status())
                .consentType(item.getTypeCode())
                .essential(item.getEssential())
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
     * ??????????????? ??????
     *
     * @return ??????????????? ??????
     */
    @Transactional(readOnly = true)
    public List<String> getVentureBusiness() {
        return repoVenture.findAllByOrderByNameAsc().stream().map(VentureBusiness::name).collect(Collectors.toList());
    }

    /**
     * ?????????????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.VentureRes registerVenture(Long idxUser, CardIssuanceDto.RegisterVenture dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        String investorName = repoVenture.findEqualsName(dto.getInvestorName());
        cardInfo.venture(Venture.builder()
            .investAmount(dto.getAmount())
            .isVC(dto.getIsVC())
            .isVerifiedVenture(dto.getIsVerifiedVenture())
            .investor(investorName != null ? investorName : dto.getInvestorName())
            .isExist(investorName != null)
            .build()
        );

        updateRiskConfigVenture(user, dto);
        // ???????????? ?????? ?????? ?????? ??????
        if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateD1100Venture(user.corp().idx(), dto);
        }

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

    // CardIssuanceInfoService.findTopByUser ??? ??????
    private CardIssuanceInfo findCardIssuanceInfo(User user, CardType cardType) {
        return repoCardIssuance.findByUserAndCardType(user, cardType).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CardIssuanceInfo")
                .build()
        );
    }

    /**
     * ?????? ???????????? ??????
     *
     * @param idxUser ???????????? User idx
     * @param dto      ????????????
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CardRes saveHopeLimit(Long idxUser, CardIssuanceDto.HopeLimitReq dto) {
        User user = findUser(idxUser);

        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardTypeOrDefaultEntity(user, CardType.GOWID,
            CardIssuanceInfo.builder()
                .corp(user.corp())
                .user(user)
                .cardType(CardType.GOWID)
                .issuanceStatus(IssuanceStatus.INPROGRESS)
                .build()
        );

        Card card = Optional.ofNullable(cardIssuanceInfo.card()).orElseGet(
            () -> Card.builder().build());

        String hopeLimit = dto.getHopeLimit();
        card.hopeLimit(hopeLimit);
        cardIssuanceInfo.card(card);
        String grantLimit = card.grantLimit();

        if (StringUtils.hasText(grantLimit)) {
            calculateGrantLimit(user, cardIssuanceInfo, card);
        }

        repoCardIssuance.save(cardIssuanceInfo);
        return CardIssuanceDto.CardRes.from(cardIssuanceInfo);
    }

    private void calculateGrantLimit(User user, CardIssuanceInfo cardIssuanceInfo, Card card) {
        String calculatedLimit = card.calculatedLimit();
        Long calculatedLimitLong = Long.parseLong(calculatedLimit);

        String hopeLimit = card.hopeLimit();
        Long hopeLimitLong = Long.parseLong(hopeLimit);

        String grantLimit = calculatedLimitLong > hopeLimitLong ? hopeLimit : calculatedLimit;
        CardCompany cardCompany = user.cardCompany();

        if (CardCompany.isShinhan(cardCompany)) {
            grantLimit = calculateShinhanGrantLimit(grantLimit);
            shinhanCardService.updateShinhanFulltextLimit(cardIssuanceInfo, grantLimit);
        } else if (CardCompany.isLotte(cardCompany)) {
            lotteCardService.updateD1100Limit(user, grantLimit, hopeLimit);
        }
        card.grantLimit(grantLimit);
        updateRiskConfigLimit(user, grantLimit, hopeLimit);
    }

    private String calculateShinhanGrantLimit(String grantLimit) {
        double maxGrantLimit = Double.valueOf(
            repoCodeDetail.getByCodeAndCode1(CommonCodeType.CARD_LIMIT, CardCompany.SHINHAN.getName()).value1());
        double parsedGrantLimit = Double.valueOf(grantLimit);

        return NumberUtils.doubleToString(Math.min(parsedGrantLimit, maxGrantLimit));
    }

    private RiskConfig updateRiskConfigLimit(User user, String grantLimit, String hopeLimit) {
        Optional<RiskConfig> riskConfig = repoRiskConfig.findByCorpAndEnabled(user.corp(), true);
        if (riskConfig.isPresent()) {
            return repoRiskConfig.save(riskConfig.get()
                .hopeLimit(hopeLimit)
                .grantLimit(grantLimit)
            );
        }

        return null;
    }

    /**
     * ?????? ???????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.AccountRes registerAccount(Long idxUser, CardIssuanceDto.RegisterAccount dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        ResAccount account = findResAccount(dto.getAccountIdx());
        if (ObjectUtils.isEmpty(account.resAccountHolder())) {
            account.resAccountHolder(dto.getAccountHolder());
        }

        if (CardCompany.isShinhan(user.cardCompany())) {
            shinhanCardService.updateD1100Account(cardInfo, account);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateD1100Account(user.corp().idx(), account);
        }

        cardInfo.bankAccount(BankAccount.builder()
            .bankAccount(account.resAccount())
            .bankCode(GowidUtils.get3digitsBankCode(account.organization()))
            .bankAccountHolder(account.resAccountHolder())
            .build());

        return CardIssuanceDto.AccountRes.from(repoCardIssuance.save(cardInfo), getBankName(account.organization()));
    }

    private ResAccount findResAccount(Long idxResAccount) {
        return repoResAccount.findById(idxResAccount).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("ResAccount")
                .idx(idxResAccount)
                .build()
        );
    }

    /**
     * ????????? ??????
     *
     * @param idxUser ???????????? User idx
     * @return ?????? ??????
     */
    @Transactional(readOnly = true)
    public CardIssuanceDto.CeoTypeRes getCeoType(Long idxUser) {
        User user = findUser(idxUser);
        if (user.corp() == null) {
            throw EntityNotFoundException.builder().entity("Corp").build();
        }
        D1000 d1000 = shinhanCardService.getD1000(user.corp().idx());
        Integer count = user.corp().ceoCount();
        CeoType ceoType = CeoType.SINGLE;
        if (d1000 != null) {
            // ?????????????????? ????????? ?????? ????????????
            if (CardCompany.isShinhan(user.cardCompany())) {
                ceoType = CeoType.fromShinhan(d1000.getD009());
            } else if (CardCompany.isLotte(user.cardCompany())) {
                ceoType = CeoType.fromLotte(CeoType.convertShinhanToLotte(d1000.getD009()));
            }
        }
        return CardIssuanceDto.CeoTypeRes.builder()
            .type(ceoType)
            // ceoType??? ??????????????? ?????? count??? 1??? ??????
            .count(ceoType.equals(CeoType.EACH)?1:count)
            .build();
    }

    /**
     * ????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.CeoRes registerCeo(Long idxUser, CardIssuanceDto.RegisterCeo dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        List<CeoInfo> ceoInfos = repoCeo.getByCardIssuanceInfo(cardInfo);
        if (!ceoInfos.isEmpty()) {
            for (CeoInfo ceoInfo : ceoInfos) {
                // ceo???????????? ????????????
                if ((ceoInfo.name().equals(dto.getName()) || ceoInfo.engName().equals(dto.getEngName()))
                    && ceoInfo.phoneNumber() != null) {
                    throw AlreadyExistException.builder().category("ceo")
                        .resource(dto.getName())
                        .build();
                }
            }
        }

        CeoInfo ceo = null;
        Integer ceoNum = 0;
        if (!ObjectUtils.isEmpty(dto.getCeoIdx())) {
            ceo = findCeoInfo(dto.getCeoIdx());
            if (!cardInfo.ceoInfos().contains(ceo)) {
                // ceo??????????????? ?????? ceo????????? ????????? cardInfo??? ???????????? ?????? ??????
                throw MismatchedException.builder().category(MismatchedException.Category.CEO).build();
            }
            if (ceo.phoneNumber() != null) {
                // ceo???????????? ????????????
                // ??????????????? ???????????????????????? ???????????? ?????????
                // ????????? ?????????????????? null??? ?????? ?????? ?????????????????? ??????
                throw AlreadyExistException.builder().category("ceo")
                    .resource(dto.getName())
                    .build();
            }
            if (ceo.ceoNumber() > 0) {
                ceoNum = ceo.ceoNumber();
            }
        }

        if (CardCompany.isShinhan(user.cardCompany())) {
            ceo = shinhanCardService.updateCeo(ceo, user.corp().idx(), cardInfo, dto, ceoNum);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            ceo= lotteCardService.updateCeo(ceo, user.corp().idx(), cardInfo, dto, ceoNum);
        }

        if (isStockholderUpdateCeo(cardInfo)) {
            setStockholderByCeoInfo(cardInfo, ceo, getStockRate(user.cardCompany()));
        }

        return CardIssuanceDto.CeoRes.from(repoCeo.save(ceo)).setDeviceId("");
    }

    private CeoInfo findCeoInfo(Long idxCeo) {
        return repoCeo.findById(idxCeo).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CeoInfo")
                .idx(idxCeo)
                .build()
        );
    }

    /**
     * ??????????????? ??????
     *
     * @param idxUser     ???????????? User idx
     * @param dto          ????????????
     * @param idxCardInfo CardIssuanceInfo idx
     * @return ?????? ??????
     */
    @Transactional(rollbackFor = Exception.class)
    public CardIssuanceDto.ManagerRes registerManager(Long idxUser, CardIssuanceDto.RegisterManager dto, Long idxCardInfo, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardInfo = findCardIssuanceInfo(user, cardType);
        if (!cardInfo.idx().equals(idxCardInfo)) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        if (!ObjectUtils.isEmpty(repoManager.getByCardIssuanceInfo(cardInfo))) {
            throw new BadRequestException(ErrorCode.Api.ALREADY_EXIST, "ALREADY_EXIST_MANAGER");
        }


        if (CardCompany.isShinhan(user.cardCompany())) {
            shinhanCardService.updateManager(user, cardInfo,  dto);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateManager(user, dto);
        }

        ManagerInfo manager = ManagerInfo.builder()
            .cardIssuanceInfo(cardInfo)
            .engName(dto.getEngName())
            .name(dto.getName())
            .nation(dto.getNation())
            .phoneNumber(dto.getPhoneNumber())
            .genderCode(dto.getGenderCode())
            .birth(dto.getBirth())
            .build();

        return CardIssuanceDto.ManagerRes.from(repoManager.save(manager));
    }

    /**
     * ?????? ??????
     * <p>
     * 1700 ????????? ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    public void verifyCeoIdentification(HttpServletRequest request, Long idxUser, CardType cardType, CardIssuanceDto.IdentificationReq dto) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardIssuanceInfo = findCardIssuanceInfo(user, cardType);
        if (!cardIssuanceInfo.idx().equals(dto.getCardIssuanceInfoIdx())) {
            throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
        }

        Map<String, String> decryptData;
        if (dto.getIdentityType().equals(CertificationType.DRIVER)) {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER, EncryptParam.DRIVER_NUMBER});
            dto.setDriverLocal(findShinhanDriverLocalCode(dto.getDriverLocal()));
        } else {
            decryptData = SecuKeypad.decrypt(request, "encryptData", new String[]{EncryptParam.IDENTIFICATION_NUMBER});
        }

        verifyCeo(idxUser, dto, decryptData);
        if(envUtil.isProd() && !"0".equals(dto.getCeoSeqNo())) {
            // ?????? ?????? ceo??? ????????? ???????????????(d1000)??? ????????????
            verifyCorrespondCeo(cardIssuanceInfo.corp().idx(), CardIssuanceDto.CeoValidReq.builder()
                .identificationNumberFront(dto.getIdentificationNumberFront())
                .name(dto.getKorName())
                .nation(dto.getNation()).build());
        }

        // Todo: ???????????? ????????? ?????? ??????
        if (CardCompany.isShinhan(user.cardCompany())) {
            shinhanCardService.updateIdentification(cardIssuanceInfo, dto, decryptData);
        } else if (CardCompany.isLotte(user.cardCompany())) {
            lotteCardService.updateIdentification(cardIssuanceInfo, dto, decryptData);
        }
    }

    private void verifyCeo(Long idxUser, CardIssuanceDto.IdentificationReq dto, Map<String, String> decryptData){
        // 1700(???????????????)
        DataPart1700 resultOfD1700 = issuanceService.proc1700(idxUser, dto, decryptData);
        String code = resultOfD1700.getD008();
        String message = resultOfD1700.getD009();

        if (!Const.API_SHINHAN_RESULT_SUCCESS.equals(code)) {
            code = changeOldDriverLicenseErrorCode(code, message);
            throw BadRequestedException.builder().category(BadRequestedException.Category.INVALID_CEO_IDENTIFICATION).desc(code).build();
        }
    }

    private String changeOldDriverLicenseErrorCode(String code, String message){
        String OLD_DRIVER_LICENSE_MSG = "?????? ??????";
        if(message.contains(OLD_DRIVER_LICENSE_MSG)){
            code = CeoVerifyCode.OLD_DRIVER_LICENSE_CODE.getCode();
        }
        return code;
    }

    private void verifyCorrespondCeo(Long idxCorp, CardIssuanceDto.CeoValidReq dto) {
        D1000 d1000 = shinhanCardService.getD1000(idxCorp);
        if (ObjectUtils.isEmpty(d1000)) {
            throw EntityNotFoundException.builder().entity("D1000").build();
        }

        boolean isValidCeoInfo = !checkCeo(d1000.getD010(), d1000.getD011(), dto)
            && !checkCeo(d1000.getD014(), d1000.getD015(), dto)
            && !checkCeo(d1000.getD018(), d1000.getD019(), dto);

        if (isValidCeoInfo) {
            throw new BadRequestException(ErrorCode.Api.VALIDATION_FAILED, CeoVerifyCode.MISMATCHED_CEO_WITH_CORP_REGISTRATION.getCode());
        }
    }

    private boolean checkCeo(String korName, String idNum, CardIssuanceDto.CeoValidReq dto) {
        if (!StringUtils.hasText(idNum)) {
            return false;
        }

        if (dto.getName().equals(korName)) {
            if (!"KR".equals(dto.getNation())) {
                return true;
            } else {
                idNum = Seed128.decryptEcb(idNum);
                return dto.getIdentificationNumberFront().substring(0, 6).equals(idNum.substring(0, 6));
            }
        }

        return false;
    }

    public String findShinhanDriverLocalCode(String code) {
        return repoCodeDetail.findFirstByValue1OrValue2AndCode(code, code, CommonCodeType.SHINHAN_DRIVER_LOCAL_CODE).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("CommonCodeDetail")
                .build()
        ).code1();
    }

    /**
     * ???????????? ??????
     *
     * @param depthKey ???????????? ???
     */
    @Transactional(rollbackFor = Exception.class)
    public IssuanceDepthResponseDto saveIssuanceDepth(Long idxUser, IssuanceDepth depthKey, CardType cardType) {
        User user = findUser(idxUser);
        CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findByUserAndCardType(user, cardType);
        cardIssuanceInfo.updateIssuanceDepth(depthKey);

        return IssuanceDepthResponseDto.from(cardIssuanceInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public FinancialConsumersResponseDto updateFinancialConsumersInfo(Long idxUser, CardType cardType, FinancialConsumersRequestDto dto){
        User user = findUser(idxUser);
        return financialConsumersService.updateOverFiveEmployees(user, cardType, dto.getOverFiveEmployees());
    }

    private User findUser(Long idxUser) {
        return repoUser.findById(idxUser).orElseThrow(
            () -> EntityNotFoundException.builder()
                .entity("User")
                .idx(idxUser)
                .build()
        );
    }
}
