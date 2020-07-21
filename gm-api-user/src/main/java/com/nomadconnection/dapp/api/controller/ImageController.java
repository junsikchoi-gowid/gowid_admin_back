package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.exception.api.BadRequestException;
import com.nomadconnection.dapp.api.exception.api.SystemException;
import com.nomadconnection.dapp.core.dto.ImageConvertDto;
import com.nomadconnection.dapp.core.utils.ImageConverter;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(ImageController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "이미지생성")
public class ImageController {

	public static class URI {
		public static final String BASE = "/image/create";
	}

	private final ImageConverter imageConverter;

	@PostMapping
	public ResponseEntity<?> create(@RequestBody ImageConvertDto params) {
		try {
			imageConverter.convertJsonToImage(params);
		} catch (IllegalArgumentException e){
			throw new BadRequestException();
		} catch (Exception e){
			throw new SystemException();
		}
		return ResponseEntity.ok().build();
	}

}
