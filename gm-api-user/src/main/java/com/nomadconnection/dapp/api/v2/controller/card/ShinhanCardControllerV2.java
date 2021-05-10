package com.nomadconnection.dapp.api.v2.controller.card;

import com.nomadconnection.dapp.api.dto.CardIssuanceDto;
import com.nomadconnection.dapp.api.service.CardIssuanceInfoService;
import com.nomadconnection.dapp.api.service.shinhan.IssuanceService;
import com.nomadconnection.dapp.api.service.shinhan.KisedService;
import com.nomadconnection.dapp.api.service.shinhan.ResumeService;
import com.nomadconnection.dapp.api.service.shinhan.UploadService;
import com.nomadconnection.dapp.api.v2.dto.kised.ConfirmationFileResponse;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedRequestDto;
import com.nomadconnection.dapp.api.v2.dto.kised.KisedResponseDto;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.common.SignatureHistory;
import com.nomadconnection.dapp.core.domain.kised.Kised;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static java.net.URI.create;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping(ShinhanCardControllerV2.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "[04] 신한 법인카드 발급", description = ShinhanCardControllerV2.URI.BASE)
public class ShinhanCardControllerV2 {

    public static class URI {
        public static final String BASE = "/card/v2/shinhan";
        public static final String APPLY = "/apply";
        public static final String PROJECT_ID = "/project-id";
        public static final String CONFIRMATION = "/confirmation";
        public static final String RESUME = "/resume";
    }

    private final IssuanceService issuanceService;
    private final KisedService kisedService;
    private final UploadService uploadService;
    private final CardIssuanceInfoService cardIssuanceInfoService;
    private final ResumeService resumeService;

    @ApiOperation(value = "법인카드 발급 신청")
    @PostMapping(URI.APPLY)
    public ResponseEntity<CardIssuanceDto.IssuanceRes> application(
        @ApiIgnore @CurrentUser CustomUser user,
        @ModelAttribute @Valid CardIssuanceDto.IssuanceReq request) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("([ application ]) $user='{}' $dto='{}'", user, request);
        }

        SignatureHistory signatureHistory = issuanceService.verifySignedBinaryAndSave(user.idx(), request.getSignedBinaryString());
        issuanceService.issuance(user.idx(), request, signatureHistory.getIdx());

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "창진원 과제번호 확인")
    @ApiResponses(value={
        @ApiResponse(code = 200, message = "정상"),
        @ApiResponse(code = 400, message = "파라미터 오류"),
        @ApiResponse(code = 401, message = "권한없음"),
        @ApiResponse(code = 500, message = "Internal Error")
    })
    @GetMapping(URI.PROJECT_ID)
    public ResponseEntity<KisedResponseDto> verifyProjectId(
        @ApiIgnore @CurrentUser CustomUser user,
        @Valid @ModelAttribute KisedRequestDto dto) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("([ verifyProjectId ]) $user='{}' $dto='{}'", user, dto);
        }

        KisedResponseDto response = kisedService.verify(user.idx(), dto);
        Kised kised = kisedService.save(response);
        cardIssuanceInfoService.updateKised(dto.getCardIssuanceInfoIdx(), kised);
        cardIssuanceInfoService.updateAccount(dto.getCardIssuanceInfoIdx(), response);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(
        value = "최종선정 확인서 파일 등록",
        notes = "허용 확장자 : pdf"
    )
    @PostMapping(URI.CONFIRMATION)
    @ApiResponses(value={
        @ApiResponse(code = 201, message = "정상 등록"),
        @ApiResponse(code = 400, message = "파라미터 오류"),
        @ApiResponse(code = 401, message = "권한없음"),
        @ApiResponse(code = 500, message = "Internal Error")
    })
    public ResponseEntity<ConfirmationFileResponse> uploadConfirmation(
        @ApiIgnore @CurrentUser CustomUser user,
        @RequestPart MultipartFile file,
        @RequestParam Long cardIssuanceInfoIdx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("([ uploadSelectionConfirmation ]) $user='{}' $idx_cardInfo='{}'", user);
        }

        ConfirmationFileResponse response = uploadService.uploadSelectionConfirmation(user.idx(), file, cardIssuanceInfoIdx);

        return ResponseEntity.created(create(response.getS3Link())).build();
    }

    @ApiOperation(value = "법인카드 발급 재개")
    @PostMapping(URI.RESUME)
    public ResponseEntity<CardIssuanceDto.ResumeRes> resumeApplication(
        @RequestBody CardIssuanceDto.ResumeReq request) {
        if (log.isInfoEnabled()) {
            log.info("([ resumeApplication ]) $dto='{}'", request);
        }

        if (log.isDebugEnabled()) {
            log.debug("## Received 1600");
            if (request != null) {
                log.debug("## request 1600 => " + request.toString());
            } else {
                log.warn("## request data of 1600 is empty!");
            }
        }

        return ResponseEntity.ok().body(
            resumeService.resumeApplication(request)
        );
    }

}
