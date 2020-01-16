package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.core.security.CustomUser;
import com.nomadconnection.dapp.api.service.ResxService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(ResxController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "리소스", description = ResxController.URI.BASE)
@SuppressWarnings({"unused", "deprecation"})
public class ResxController {

	@SuppressWarnings("WeakerAccess")
	public static class URI {
		public static final String BASE = "/resx/v1";
		public static final String PROFILE = "/profile";
		public static final String STOCKHOLDERSLIST = "/stockholdersList";
	}

	private final ResxService service;

	//==================================================================================================================
	//
	//	프로필
	//
	//==================================================================================================================

	//==================================================================================================================
	//
	//	주주명부
	//
	//==================================================================================================================

	@ApiOperation(value = "주주명부", notes = "" +
			"\n ### Remarks" +
			"\n" +
			"\n - " +
			"\n")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "corp", value = "식별자(법인)", dataType = "Long")
	})
	@GetMapping(URI.STOCKHOLDERSLIST)
	public ResponseEntity<Resource> getStockholdersList(
			@ApiIgnore HttpServletRequest request,
			@ApiIgnore @CurrentUser CustomUser user,
			@RequestParam Long corp) {
		Resource resource = service.getResxStockholdersList(user.idx(), corp);
		String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		{
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException e) {
				if (log.isWarnEnabled()) {
					log.warn("([ getStockholdersList ]) COULD NOT DETERMINE FILE MANAGER TYPE, $resource='{}'", resource.getFilename());
				}
			}
		}
		//
		//	fixme: 원본파일명으로 다운로드
		//
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
