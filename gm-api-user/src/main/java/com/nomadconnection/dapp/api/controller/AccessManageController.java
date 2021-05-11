package com.nomadconnection.dapp.api.controller;

import com.nomadconnection.dapp.api.dto.AccessManageDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.service.AccessManageService;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(AccessManageController.URI.BASE)
@RequiredArgsConstructor
@Api(tags = "[02] 연동관리", description = AccessManageController.URI.BASE)
@Validated
public class AccessManageController {

    public static class URI {
        public static final String BASE = "/accessManagement/v1";
        public static final String COMMON_CORP = "/common-corp"; // 기관리스트
        public static final String CONNECTEDID_ALL = "/connected-id/all";
        public static final String CONNECTEDID = "/connected-id"; // connectedId
        public static final String CONNECTEDID_PUT = "/connected-id/{idxConnectedMng}"; // connectedId - 사용중지
        public static final String CONNECTEDID_IDXCORP = "/connected-id/{idxConnectedMng}/corp"; // connectedId, corpType
        public static final String CONNECTEDID_STOP = "/connected-id/stop"; // 기관 삭제
    }

    private final AccessManageService accessManageService;

    @ApiOperation(value = "기관 목록", notes = "" +
            "기관종류: 은행(BANK),증권(STOCK),카드(CARD),공공(NT)\n" +
            "\n - " +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "corpType", value="기관종류")
    })
    @GetMapping(URI.COMMON_CORP)
    public ResponseEntity<?> commonCorp(@ApiIgnore @CurrentUser CustomUser user,@RequestParam CommonCodeType corpType) {

        List<AccessManageDto.AccessCodeType> commonCodeDetail = accessManageService.commonCorp(user, corpType);
        if(commonCodeDetail == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(commonCodeDetail,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED ID 목록", notes = "" +
            "CONNECTED ID 목록\n" +
            "\n - " +
            "\n")
    @GetMapping(URI.CONNECTEDID)
    public ResponseEntity<?> getConnectedId(@ApiIgnore @CurrentUser CustomUser user) {

        List<ConnectedMngDto> connectedMngs = accessManageService.getConnectedIdNormal(user);

        if(connectedMngs == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(connectedMngs,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED ID 등록", notes = "" +
            "- 연동관리 CONNECTED ID 등록\n" +
            "\n")
    @PostMapping(URI.CONNECTEDID)
    public ResponseEntity<?> postConnectedId(@ApiIgnore @CurrentUser CustomUser user
            ,@RequestBody AccessManageDto dto) {

        Long data = accessManageService.postConnectedId(user, dto);

        if(data == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED ID 사용중지", notes = "" +
        "connected id 사용 중지 \n" +
        "\n - " +
        "\n")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "idxConnectedMng", value="CONNECTED ID")
    })
    @PutMapping(URI.CONNECTEDID_PUT)
    public ResponseEntity<?> updateConnectedId(@ApiIgnore @CurrentUser CustomUser user
        ,@PathVariable Long idxConnectedMng) {

        ConnectedMngDto connectedMng = accessManageService.updateConnectedId(user, idxConnectedMng);

        if(connectedMng == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(connectedMng,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED, CORP 목록", notes = "" +
            "\n" +
            "\n - " +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idxConnectedMng", value="CONNECTED ID"),
    })
    @GetMapping(URI.CONNECTEDID_IDXCORP)
    public ResponseEntity<?> getConnectedIdCorp(@ApiIgnore @CurrentUser CustomUser user
            ,@PathVariable Long idxConnectedMng) {

        List<AccessManageDto.ResConCorpListDto> resConCorpListDto = accessManageService.getConnectedIdCorp(user,idxConnectedMng);

        if(resConCorpListDto == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(resConCorpListDto,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED, CORP 등록", notes = "" +
            "\n" +
            "\n - " +
            "\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idxConnectedMng", value="CONNECTED ID")
    })
    @PostMapping(URI.CONNECTEDID_IDXCORP)
    public ResponseEntity<?> postConnectedIdCorp(@ApiIgnore @CurrentUser CustomUser user
            ,@RequestBody AccessManageDto dto
            ,@PathVariable Long idxConnectedMng) {

        Long data = accessManageService.postConnectedIdCorp(user, dto, idxConnectedMng);

        if(data == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED ID 목록", notes = "" +
            "CONNECTED ID 목록\n" +
            "\n - " +
            "\n")
    @GetMapping(URI.CONNECTEDID_ALL)
    public ResponseEntity<?> getConnectedIdAll(@ApiIgnore @CurrentUser CustomUser user) {

        AccessManageDto.AccessInfoAll data = accessManageService.getConnectedIdAll(user);

        if(data == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    @ApiOperation(value = "CONNECTED ID 목록", notes = "" +
            "CONNECTED ID 목록\n" +
            "\n - " +
            "\n")
    @PostMapping(URI.CONNECTEDID_STOP)
    public ResponseEntity<?> procConnectedIdStop(@ApiIgnore @CurrentUser CustomUser user,
            @RequestBody AccessManageDto.ResConCorpStatusDto dto) {

        Boolean data = accessManageService.procConnectedIdStop(user, dto);

        if(data == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(data,HttpStatus.OK);
    }


}
