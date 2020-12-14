package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.api.common.Const;
import com.nomadconnection.dapp.api.dto.GwUploadDto;
import com.nomadconnection.dapp.api.dto.ImageFileDto;
import com.nomadconnection.dapp.api.dto.shinhan.enums.ImageFileType;
import com.nomadconnection.dapp.api.service.GwUploadService;
import com.nomadconnection.dapp.api.v2.utils.FullTextJsonParser;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import com.nomadconnection.dapp.core.dto.ImageConvertRespDto;
import com.nomadconnection.dapp.core.exception.ImageConvertException;
import com.nomadconnection.dapp.core.exception.error.ImageConvertErrorMessage;
import com.nomadconnection.dapp.core.utils.ImageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.nomadconnection.dapp.api.util.CommonUtil.replaceHyphen;

/**
 * 이미지 생성 및 GW전송
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageConverter converter;
	private final GwUploadService gwUploadService;

	public ImageConvertDto buildImageConvertDto(ImageFileDto imageFileDto){
		return
			ImageConvertDto.builder()
				.cardCompany(imageFileDto.getCardCompany())
				.mrdType(imageFileDto.getImageFileType().getFileType())
				.data(imageFileDto.getImageJson())
				.fileName(replaceHyphen(imageFileDto.getLicenseNo()).concat(imageFileDto.getFileName()))
				.build();
	}

	public ImageConvertRespDto createImageByScrapingJson(ImageConvertDto param) throws Exception {
		ImageConvertRespDto imageConvertResponse = converter.convertJsonToImage(param);
		if(!imageConvertResponse.isSuccess()) {
			throw new ImageConvertException(ImageConvertErrorMessage.INTERNAL_ERROR);
		}
		return imageConvertResponse;
	}

	public GwUploadDto.Response sendImageToGw(ImageConvertDto imageParam, int totalPageCount, String licenseNo) throws IOException, InterruptedException {
		GwUploadDto.Response response = null;
		File file;
		for( int iPage = 1 ; iPage <= totalPageCount; iPage++ ) {
			StringBuffer filePath = new StringBuffer(Const.REPORTING_SERVER);
			if(CardCompany.isShinhan(imageParam.getCardCompany()) || totalPageCount < 2 ){
				file = new File(filePath.append(imageParam.getFileName()).append(".").append(imageParam.getExportType()).toString());
			} else {
				file = new File(filePath.append(imageParam.getFileName()).append("_").append(iPage).append(".").append(imageParam.getExportType()).toString());
			}
			response = sendImage(imageParam.getCardCompany(), file, imageParam.getMrdType().toString(), replaceHyphen(licenseNo));
		}

		return response;
	}

	private GwUploadDto.Response sendImage(CardCompany cardCompany,File file, String fileCode, String licenseNo) throws InterruptedException, IOException {
		GwUploadDto.Response response = null;
		fileCode = changeCode(cardCompany, fileCode);
		for (int i = 0; i < 3; i++) {
			Thread.sleep(500);
			response = gwUploadService.upload(cardCompany, file, fileCode, replaceHyphen(licenseNo));
			if ("200".equals(response.getResult().getCode())){
				break;
			}
		}
		return response;
	}

	public void sendCorpLicenseImage(CardCompany cardCompany, String response, String licenseNo) throws Exception {
		ImageConvertDto corpImageParam = buildImageConvertDto(
			ImageFileDto
				.builder()
				.cardCompany(cardCompany)
				.imageFileType(ImageFileType.CORP_LICENSE)
				.fileName("15100001")
				.imageJson(response)
				.licenseNo(licenseNo)
				.build());

		createAndSendImage(corpImageParam, licenseNo);
	}

	public void sendCorpRegistrationImage(CardCompany cardCompany, String response, String licenseNo) throws Exception {
		response = FullTextJsonParser.setNoContents(response);
		response = FullTextJsonParser.mergeResTypeStockContentItemList(response);

		ImageConvertDto corpRegistrationImageParam = buildImageConvertDto(
			ImageFileDto
				.builder()
				.cardCompany(cardCompany)
				.imageFileType(ImageFileType.CORP_REGISTRATION)
				.fileName("15300001")
				.imageJson(response)
				.licenseNo(licenseNo)
				.build());

		createAndSendImage(corpRegistrationImageParam, licenseNo);
	}

	public void sendFinancialStatementsImage(CardCompany cardCompany, String yyyyMm, String standardFinancialResult, String licenseNo) throws Exception {
		ImageConvertDto financialStatementsImageParam = buildImageConvertDto(
			ImageFileDto
				.builder()
				.cardCompany(cardCompany)
				.imageFileType(ImageFileType.FINANCIAL_STATEMENTS)
				.fileName(1520 + yyyyMm.substring(0, 4))
				.imageJson(standardFinancialResult)
				.licenseNo(licenseNo)
				.build());

		createAndSendImage(financialStatementsImageParam, licenseNo);
	}

	public void sendGuaranteeImage(Corp corp, CardCompany cardCompany, String licenseNo) throws Exception {
		//TODO: parse json
		String guaranteeImageJson = makeGuaranteeImageJson(corp);
		ImageConvertDto guaranteeImageParam = buildImageConvertDto(
			ImageFileDto
				.builder()
				.cardCompany(cardCompany)
				.imageFileType(ImageFileType.GUARANTEE)
				.fileName("99910001")
				.imageJson(guaranteeImageJson)
				.licenseNo(licenseNo)
				.build());

		createAndSendImage(guaranteeImageParam, licenseNo);
	}

	private void createAndSendImage(ImageConvertDto imageParam, String licenseNo) throws Exception {
		ImageConvertRespDto imageConvertResponse = createImageByScrapingJson(imageParam);
		sendImageToGw(imageParam, imageConvertResponse.getTotalPageCount(), licenseNo);
	}

	private String makeGuaranteeImageJson(Corp corp){
		return
			"{\n" +
				"\t\"data\" : {\n" +
				"\t\t\"resCompanyIdentityNo\" : \"" + corp.resCompanyIdentityNo() + "\" ,\n" +
				"\t\t\"resCompanyNm\" : \"" + corp.resCompanyNm() + "\"\n" +
				"\t}\n" +
				"}";
	}

	// 0306 신한 0311 롯데
	private String changeCode(CardCompany cardCompany, String fileCode) {
		HashMap<String, String> fileCodeMap = new HashMap<>();
		fileCodeMap.put("1510","8502");
		fileCodeMap.put("1520","8503");
		fileCodeMap.put("1530","8505");
		fileCodeMap.put("9991","8506");

		if(CardCompany.isLotte(cardCompany)){
			log.debug("0311 cardCompany = {}" , cardCompany.getName());
			fileCode = fileCodeMap.getOrDefault(fileCode, fileCode);
		} else if(CardCompany.isShinhan(cardCompany)){
			log.debug("0306 cardCompany = {}" , cardCompany.getName());
		}

		return fileCode;
	}



}
