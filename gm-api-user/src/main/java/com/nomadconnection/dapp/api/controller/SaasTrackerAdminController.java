package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.SaasTrackerAdminDto;
import com.nomadconnection.dapp.api.service.SaasTrackerAdminService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(SaasTrackerAdminController.URI.BASE)
@RequiredArgsConstructor
@Validated
@Api(tags = "[05] SaaS Tracker", description = SaasTrackerAdminController.URI.BASE)
public class SaasTrackerAdminController {

    public static class URI {

        // Base
        public static final String BASE = "/admin/v1/saas/tracker";

        // SaaS tracker User 관리
        public static final String SAAS_TRACKER_USER = "/users";
        public static final String SAAS_TRACKER_FIND_USER = "/find-users";
        public static final String SAAS_TRACKER_USER_DETAIL = "/users/{idxUser}";

        // SaaS 지출 내역(Rule #1) 관리
        public static final String SAAS_PAYMENT_HISTORY = "/payment/histories";
        public static final String SAAS_PAYMENT_HISTORY_DETAIL = "/payment/histories/{idx}";

        // SaaS 관리 내역(Rule #2) 관리
        public static final String SAAS_PAYMENT_INFO = "/payment/infos";
        public static final String SAAS_PAYMENT_INFO_DETAIL = "/payment/infos/{idx}";

        // SaaS 구독/구독만료 정보 조회
        public static final String SAAS_SUBSCRIPTIONS = "/subscriptions/{idx}";

    }

    private final SaasTrackerAdminService service;

    @ApiOperation("SaaS tracker User 조회")
    @GetMapping(URI.SAAS_TRACKER_USER)
    public ResponseEntity getSaasTrackerUser(@ApiIgnore @CurrentUser CustomUser user) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasTrackerUser ])");
        }
        return service.getSaasTrackerUser(user.idx());
    }

    @ApiOperation("SaaS Tracker 사용을 위한 사용자 조회")
    @GetMapping(URI.SAAS_TRACKER_FIND_USER)
    public ResponseEntity findUserAtSaasTracker(@ApiIgnore @CurrentUser CustomUser user,
                                                @RequestParam(required = true) String email) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.findUserAtSaasTracker ]) $email='{}'", email);
        }
        return service.findUserAtSaasTracker(email);
    }

    @ApiOperation("SaaS Tracker 사용자 등록")
    @PostMapping(URI.SAAS_TRACKER_USER)
    public ResponseEntity saveSaasTrackerUser(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestBody @Valid SaasTrackerAdminDto.SaasTrackerUserReq req) {

        if (log.isDebugEnabled()) {
            log.info("([saveSaasTrackerUser]) $req='{}'", req.toString());
        }
        return service.saveSaasTrackerUser(req);
    }

    @ApiOperation("SaaS Tracker 사용자 권한 수정")
    @PutMapping(URI.SAAS_TRACKER_USER_DETAIL)
    public ResponseEntity updateSaasTrackerUser(@ApiIgnore @CurrentUser CustomUser user,
                                                @PathVariable Long idxUser,
                                                @RequestBody @Valid SaasTrackerAdminDto.SaasTrackerUserUpdateReq req) {

        if (log.isDebugEnabled()) {
            log.info("([updateSaasTrackerUser]) $idxUser='{}' $req='{}'", idxUser, req.toString());
        }
        return service.updateSaasTrackerUser(idxUser, req);
    }


    @ApiOperation("SaaS 지출 내역 조회")
    @GetMapping(URI.SAAS_PAYMENT_HISTORY)
    public ResponseEntity getSaasPaymentHistories(@ApiIgnore @CurrentUser CustomUser user,
                                                  @RequestParam(required = true) Long idxUser,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to,
                                                  @PageableDefault Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasPaymentHistories ]) $idxUser='{}', fromPaymentDate='{}', toPaymentDate='{}'"
                    , idxUser, from, to);
        }
        return service.getSaasPaymentHistories(idxUser, from, to, pageable);
    }

    @ApiOperation("SaaS 지출 내역 상세 조회")
    @GetMapping(URI.SAAS_PAYMENT_HISTORY_DETAIL)
    public ResponseEntity getSaasPaymentHistoryDetail(@ApiIgnore @CurrentUser CustomUser user,
                                                      @PathVariable Long idx) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasPaymentHistoryDetail ]) $idx='{}'", idx);
        }
        return service.getSaasPaymentHistoryDetail(idx);
    }

    @ApiOperation("SaaS 지출 내역 저장")
    @PostMapping(URI.SAAS_PAYMENT_HISTORY)
    public ResponseEntity saveSaasPaymentHistory(@ApiIgnore @CurrentUser CustomUser user,
                                                 @RequestBody @Valid SaasTrackerAdminDto.SaasPaymentHistoryReq req) {

        if (log.isDebugEnabled()) {
            log.info("([saveSaasPaymentHistory]) $user='{}', $req='{}'", user, req.toString());
        }
        return service.saveSaasPaymentHistory(req);
    }

    @ApiOperation("SaaS 지출 내역 삭제")
    @DeleteMapping(URI.SAAS_PAYMENT_HISTORY_DETAIL)
    public ResponseEntity deleteSaasPaymentHistory(@ApiIgnore @CurrentUser CustomUser user,
                                                   @PathVariable Long idx) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.deleteSaasPaymentHistory ]) $idx='{}'", idx);
        }
        return service.deleteSaasPaymentHistory(idx);
    }

    @ApiOperation("SaaS 사용현황 내역 조회")
    @GetMapping(URI.SAAS_PAYMENT_INFO)
    public ResponseEntity getSaasPaymentInfos(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestParam(required = true) Long idxUser,
                                              @PageableDefault Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasPaymentInfos ]) $idxUser='{}'", idxUser);
        }
        return service.getSaasPaymentInfos(idxUser, pageable);
    }

    @ApiOperation("SaaS 사용현황 내역 상세 조회")
    @GetMapping(URI.SAAS_PAYMENT_INFO_DETAIL)
    public ResponseEntity getSaasPaymentInfoDetail(@ApiIgnore @CurrentUser CustomUser user,
                                                   @PathVariable Long idx) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasPaymentInfoDetail ]) $idx='{}'", idx);
        }
        return service.getSaasPaymentInfoDetail(idx);
    }

    @ApiOperation("SaaS 사용현황 저장")
    @PostMapping(URI.SAAS_PAYMENT_INFO)
    public ResponseEntity saveSaasPaymentInfo(@ApiIgnore @CurrentUser CustomUser user,
                                              @RequestBody @Valid SaasTrackerAdminDto.SaasPaymentInfoReq req) {

        if (log.isDebugEnabled()) {
            log.info("([saveSaasPaymentInfo]) $user='{}', $req='{}'", user, req.toString());
        }
        return service.saveSaasPaymentInfo(req);
    }

    @ApiOperation("SaaS 사용현황 삭제")
    @DeleteMapping(URI.SAAS_PAYMENT_INFO_DETAIL)
    public ResponseEntity deleteSaasPaymentInfo(@ApiIgnore @CurrentUser CustomUser user,
                                                @PathVariable Long idx) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.deleteSaasPaymentInfo ]) $idx='{}'", idx);
        }
        return service.deleteSaasPaymentInfo(idx);
    }

    @ApiOperation("SaaS 사용현황 수정")
    @PutMapping(URI.SAAS_PAYMENT_INFO_DETAIL)
    public ResponseEntity updateSaasPaymentInfo(@ApiIgnore @CurrentUser CustomUser user,
                                                @PathVariable Long idx,
                                                @RequestBody @Valid SaasTrackerAdminDto.SaasPaymentInfoReq req) {

        if (log.isInfoEnabled()) {
            log.info("([ updateSaasPaymentInfo ]) $idx='{}' $req='{}'", idx, req);
        }
        return service.updateSaasInfo(idx, req);
    }

    @ApiOperation("SaaS 구독/구독만료 조회")
    @GetMapping(URI.SAAS_SUBSCRIPTIONS)
    public ResponseEntity getSaasSubscriptions(@ApiIgnore @CurrentUser CustomUser user,
                                               @PathVariable Long idx) {
        if (log.isInfoEnabled()) {
            log.info("([ admin.getSaasSubscriptions ]) $idxUser='{}'", idx);
        }
        return service.getSaasSubscriptions(idx);
    }

}