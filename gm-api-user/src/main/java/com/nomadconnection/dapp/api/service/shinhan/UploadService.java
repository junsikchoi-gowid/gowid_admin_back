package com.nomadconnection.dapp.api.service.shinhan;

import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.api.exception.FileUploadException;
import com.nomadconnection.dapp.api.service.AwsS3Service;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.service.GwUploadService;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.dto.kised.ConfirmationFileDto;
import com.nomadconnection.dapp.api.v2.dto.kised.ConfirmationFileResponse;
import com.nomadconnection.dapp.api.v2.service.kised.ConfirmationFileService;
import com.nomadconnection.dapp.api.v2.utils.StreamUtils;
import com.nomadconnection.dapp.core.domain.card.CardCompany;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardIssuanceInfo;
import com.nomadconnection.dapp.core.domain.cardIssuanceInfo.CardType;
import com.nomadconnection.dapp.core.domain.kised.ConfirmationFile;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

	private final AwsS3Service awsS3Service;

	private final GwUploadService gwUploadService;

	private final UserService userService;

	private final CardIssuanceInfoService cardIssuanceInfoService;

	private final KisedService kisedService;

	private final ConfirmationFileService confirmationFileService;

	@Value("${confirmation.file.size}")
	private Long CONFIRMATION_FILE_SIZE;

	@Value("${confirmation.file.type}")
	private List<String> CONFIRMATION_FILE_TYPE;

	private String CODE = "1701";

	@Transactional
	public ConfirmationFileResponse uploadSelectionConfirmation(Long idxUser, MultipartFile file, Long cardIssuanceInfoIdx) throws Exception {
		File uploadFile = null;
		String s3Key = "";
		String s3Link;
		String fileName = "";
		CardCompany cardCompany = CardCompany.SHINHAN;
		try {
			checkAllowedExtension(file);

			User user = userService.getUser(idxUser);
			CardIssuanceInfo cardIssuanceInfo = cardIssuanceInfoService.findById(cardIssuanceInfoIdx);
			Kised kised = cardIssuanceInfo.kised();

			String licenseNo = CommonUtil.replaceHyphen(user.corp().resCompanyIdentityNo());
			fileName = makeSelectionFileName(file, licenseNo);
			s3Key = "confirmation/" + kised.getIdx() + "/" + fileName;

			ConfirmationFileDto dto = ConfirmationFileDto.of(file, s3Key, fileName);

			uploadFile = makeFile(file, fileName);
			s3Link = uploadS3(uploadFile, s3Key);
			uploadGw(cardCompany, uploadFile, CODE, licenseNo);

			ConfirmationFile confirmationFile = saveConfirmationFile(dto, s3Link, kised);

			uploadFile.delete();

			return ConfirmationFileResponse.from(confirmationFile);
		} catch (IOException e){
			e.printStackTrace();
			deleteFile(uploadFile, s3Key, cardCompany, fileName);
			log.error("[uploadSelectionConfirmation] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
			throw FileUploadException.builder().category(FileUploadException.Category.UPLOAD_CONFIRMATION_FILE).build();
		}
	}

	private void checkAllowedExtension(MultipartFile file) throws BadRequestedException {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
		List<String> allowedExtensions = CONFIRMATION_FILE_TYPE.stream().map(type -> type.toLowerCase()).collect(Collectors.toList());
		boolean isAllowed = StreamUtils.in(allowedExtensions, extension);

		if(!isAllowed){
			throw new BadRequestedException(BadRequestedException.Category.NOT_ALLOWED_EXTENSION
				, "Allowed extensions are " + allowedExtensions.toString());
		}
	}

	private void deleteFile(File file, String s3Key, CardCompany cardCompany, String fileName) throws IOException {
		if(file.exists()){
			file.delete();
		}
		awsS3Service.s3FileDelete(s3Key);
		gwUploadService.delete(cardCompany, fileName);
	}

	private ConfirmationFile saveConfirmationFile(ConfirmationFileDto dto, String s3Key, Kised kised){
		dto.setS3Link(s3Key);
		dto.setIsTransferToGw(true);

		ConfirmationFile confirmationFile = saveConfirmationFile(dto);
		kised.updateConfirmationFile(confirmationFile);

		return confirmationFile;
	}

	private String makeSelectionFileName(MultipartFile file, String licenseNo) {
		return licenseNo + CODE + "0001" + "." + FilenameUtils.getExtension(file.getOriginalFilename());
	}

	private File makeFile(MultipartFile file, String fileName) throws IOException {
		File uploadFile = new File(fileName);
		uploadFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(uploadFile);
		fos.write(file.getBytes());
		fos.close();

		return uploadFile;
	}

	private String uploadS3(File uploadFile, String s3Key){
		String s3Link = awsS3Service.s3FileUpload(uploadFile, s3Key);

		return s3Link;
	}

	private ConfirmationFile saveConfirmationFile(ConfirmationFileDto dto){
		return confirmationFileService.save(ConfirmationFileDto.toEntity(dto));
	}

	private void uploadGw(CardCompany cardCompany, File file, String fileCode, String licenseNo) throws IOException {
		gwUploadService.upload(cardCompany, file, fileCode, licenseNo);
	}

}
