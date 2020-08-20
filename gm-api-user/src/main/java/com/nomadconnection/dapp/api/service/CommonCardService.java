package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.BrandConsentDto;
import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.exception.*;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFile;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.StockholderFileType;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.IssuanceProgress;
import com.nomadconnection.dapp.core.domain.consent.ConsentMapping;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.corp.VentureBusiness;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.CardIssuanceInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.cardIssuanceInfo.StockholderFileRepository;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.common.IssuanceProgressRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentMappingRepository;
import com.nomadconnection.dapp.core.domain.repository.consent.ConsentRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CeoInfoRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.VentureBusinessRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskConfigRepository;
import com.nomadconnection.dapp.core.domain.repository.risk.RiskRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.user.User;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCardService {

	private final UserRepository repoUser;
	private final CardIssuanceInfoRepository repoCardIssuance;
	private final CommonCodeDetailRepository repoCodeDetail;
	private final VentureBusinessRepository repoVenture;
	private final StockholderFileRepository repoFile;
	private final ConsentRepository repoConsent;
	private final ConsentMappingRepository repoConsentMapping;
	private final CeoInfoRepository repoCeoInfo;
	private final ConnectedMngRepository repoConnectedMng;
	private final IssuanceProgressRepository repoIssuanceProgress;
	private final CorpRepository repoCorp;
	private final RiskRepository repoRisk;
	private final RiskConfigRepository repoRiskConfig;

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
	public List<CardIssuanceDto.StockholderFileRes> registerStockholderFile(Long idx_user, MultipartFile[] file_1, MultipartFile[] file_2, String type, Long idx_CardInfo, String depthKey) throws IOException {
		User user = findUser(idx_user);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
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

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
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
				gwUploadService.upload(cardInfo.cardCompany(), uploadFile, Const.STOCKHOLDER_GW_FILE_CODE, licenseNo);
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
	public void deleteStockholderFile(Long idx_user, Long idx_file, Long idx_CardInfo, String depthKey) throws IOException {
		User user = findUser(idx_user);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		if (!cardInfo.idx().equals(idx_CardInfo)) {
			throw MismatchedException.builder().category(MismatchedException.Category.CARD_ISSUANCE_INFO).build();
		}

		StockholderFile file = findStockholderFile(idx_file);
		if (file.cardIssuanceInfo().idx() != idx_CardInfo) {
			throw MismatchedException.builder().category(MismatchedException.Category.STOCKHOLDER_FILE).build();
		}
		gwUploadService.delete(cardInfo.cardCompany(), file.fname());
		s3Service.s3FileDelete(file.s3Key());
		repoFile.delete(file);

		if (StringUtils.hasText(depthKey)) {
			repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
		}
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
					.issuanceDepth(cardIssuanceInfo.issuanceDepth())
					.cardCompany(!ObjectUtils.isEmpty(cardIssuanceInfo.cardCompany()) ? cardIssuanceInfo.cardCompany().name() : null)
					.consentRes(consentInfo)
					.corporationRes(getCorporationRes(cardIssuanceInfo))
					.corporationExtendRes(CardIssuanceDto.CorporationExtendRes.from(cardIssuanceInfo, getListedCompanyName(!ObjectUtils.isEmpty(cardIssuanceInfo.corpExtend()) ? cardIssuanceInfo.corpExtend().listedCompanyCode() : null)))
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
	 * 상장거래소 조회
	 *
	 * @return 상장거래소 목록
	 */
	@Transactional(readOnly = true)
	public List<CardIssuanceDto.ExchangeType> getListedExchangeType() {
		return repoCodeDetail.findAllByCode(CommonCodeType.LOTTE_LISTED_EXCHANGE).stream().map(CardIssuanceDto.ExchangeType::from).collect(Collectors.toList());
	}

	/**
	 * 발급가능한 카드사 조회
	 *
	 * @return 발급가능한 카드사 목록
	 */
	@Transactional(readOnly = true)
	public List<CardIssuanceDto.IssuanceCardType> getIssuanceCardType() {
		return Arrays.stream(CardCompany.values()).map(CardIssuanceDto.IssuanceCardType::from).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveIssuanceDepth(Long idx_user, String depthKey) {
		User user = findUser(idx_user);
		CardIssuanceInfo cardInfo = findCardIssuanceInfo(user);
		repoCardIssuance.save(cardInfo.issuanceDepth(depthKey));
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void deleteAllIssuanceInfo(User user) {
		Corp corp = user.corp();

		repoUser.saveAndFlush(user.corp(null).cardCompany(null));
		log.debug("Complete update gowid.User set idxCorp = null, cardCompany = null where idxCorp = @idxCorp");

		{
			List<Long> cardIssuanceInfoIdx = repoCardIssuance.findAllIdxByUserIdx(user.idx());
			repoCeoInfo.deleteAllByCardIssuanceInfoIdx(cardIssuanceInfoIdx);
			log.debug("Complete delete FROM gowid.CeoInfo where idxCardIssuanceInfo = @idxCardIssuanceInfo");

			repoFile.deleteAllByCardIssuanceInfoIdx(cardIssuanceInfoIdx);
			log.debug("Complete delete FROM gowid.StockholderFile where idxCardIssuanceInfo = @idxCardIssuanceInfo");

			repoCardIssuance.deleteAllByUserIdx(user.idx());
			log.debug("Complete delete from gowid.CardIssuanceInfo where idxUser = @idxUser");
		}

		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(user.idx());
		if (!ObjectUtils.isEmpty(connectedMng)) {
			repoConnectedMng.deleteInBatch(repoConnectedMng.findByIdxUser(user.idx()));
			repoConnectedMng.flush();
		}
		log.debug("Complete delete from gowid.ConnectedMng where idxUser = @idxUser");

		List<ConsentMapping> consentMappings = repoConsentMapping.findAllByIdxUser(user.idx());
		if (!ObjectUtils.isEmpty(consentMappings)) {
			repoConsentMapping.deleteInBatch(consentMappings);
			repoConsentMapping.flush();
		}
		log.debug("Complete delete from gowid.ConsentMapping where idxUser = @idxUser");

		IssuanceProgress issuanceProgress = getIssuanceProgress(user.idx());
		if (!ObjectUtils.isEmpty(issuanceProgress)) {
			repoIssuanceProgress.delete(issuanceProgress);
			repoIssuanceProgress.flush();
		}
		log.debug("Complete delete from gowid.IssuanceProgress WHERE userIdx = @idxUser");

		repoRisk.deleteByCorpIdx(corp.idx());
		log.debug("Complete delete from gowid.Risk where idxCorp = @idxCorp");

		repoRiskConfig.deleteByCorpIdx(corp.idx());
		log.debug("Complete delete from gowid.RiskConfig where idxCorp = @idxCorp");

		repoCorp.deleteCorpByIdx(corp.idx());
		log.debug("Complete delete from gowid.Corp where idx = @idxCorp");
	}

	private User findUser(Long idx_user) {
		return repoUser.findById(idx_user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("User")
						.idx(idx_user)
						.build()
		);
	}

	private CardIssuanceInfo findCardIssuanceInfo(User user) {
		return repoCardIssuance.findTopByUserAndDisabledFalseOrderByIdxDesc(user).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("CardIssuanceInfo")
						.build()
		);
	}

	private StockholderFile findStockholderFile(Long idx_file) {
		return repoFile.findById(idx_file).orElseThrow(
				() -> EntityNotFoundException.builder()
						.entity("StockholderFile")
						.idx(idx_file)
						.build()
		);
	}

	private IssuanceProgress getIssuanceProgress(Long idx_user) {
		return repoIssuanceProgress.findById(idx_user).orElse(null);
	}
}
