//package com.nomadconnection.dapp.resx.dto;
//
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.Accessors;
//
//import java.nio.file.Path;
//
//@Data
//@Accessors(fluent = true)
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ResxDto {
//
//	@SuppressWarnings("unused")
//	public enum Status {
//		OK,
//		ERROR_INVALID_ID_PARAMETER,
//		ERROR_INVALID_RESOURCE_PARAMETER,
//		ERROR_INVALID_FILENAME_PARAMETER,
//		ERROR_INVALID_PATH_EXCEPTION,
//		ERROR_IO_EXCEPTION,
//		ERROR
//	}
//
//	@ApiModelProperty("식별자(상위디렉토리명)")
//	private Long id;
//
//	@ApiModelProperty("파일사이즈")
//	private Long size;
//
//	@ApiModelProperty("파일명")
//	private String filename;
//
//	@ApiModelProperty("원본파일명")
//	private String originalFilename;
//
//	@ApiModelProperty("저장경로")
//	private Path path;
//
//	@ApiModelProperty("상태")
//	private Status status;
//}
//package com.nomadconnection.dapp.resx.dto;
//
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.Accessors;
//
//import java.nio.file.Path;
//
//@Data
//@Accessors(fluent = true)
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ResxDto {
//
//	@SuppressWarnings("unused")
//	public enum Status {
//		OK,
//		ERROR_INVALID_ID_PARAMETER,
//		ERROR_INVALID_RESOURCE_PARAMETER,
//		ERROR_INVALID_FILENAME_PARAMETER,
//		ERROR_INVALID_PATH_EXCEPTION,
//		ERROR_IO_EXCEPTION,
//		ERROR
//	}
//
//	@ApiModelProperty("식별자(상위디렉토리명)")
//	private Long id;
//
//	@ApiModelProperty("파일사이즈")
//	private Long size;
//
//	@ApiModelProperty("파일명")
//	private String filename;
//
//	@ApiModelProperty("원본파일명")
//	private String originalFilename;
//
//	@ApiModelProperty("저장경로")
//	private Path path;
//
//	@ApiModelProperty("상태")
//	private Status status;
//}
