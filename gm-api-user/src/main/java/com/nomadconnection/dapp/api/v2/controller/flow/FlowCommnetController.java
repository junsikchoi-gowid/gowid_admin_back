package com.nomadconnection.dapp.api.v2.controller.flow;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.nomadconnection.dapp.api.util.CommonUtil;
import com.nomadconnection.dapp.api.v2.dto.FlowDto;
import com.nomadconnection.dapp.api.v2.service.flow.FlowReportService;
import com.nomadconnection.dapp.core.annotation.ApiPageable;
import com.nomadconnection.dapp.core.annotation.CurrentUser;
import com.nomadconnection.dapp.core.exception.response.GowidResponse;
import com.nomadconnection.dapp.core.security.CustomUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.nomadconnection.dapp.core.exception.response.GowidResponse.ok;

@Slf4j
@CrossOrigin(allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@Api(tags = {"FlowCash"},  value = "FlowCash" )
@RequestMapping(FlowBaseController.URI.BASE)
@Validated
@SuppressWarnings("unchecked")
public class FlowCommnetController extends FlowBaseController {
    public static class URI {
        public static final String COMMENT       = "/comment";
        public static final String COMMENT_LAST  = "/comment/last";
        public static final String COMMENT_FILE  = "/comment/file";
    }
 
    private final FlowReportService flowReportService;

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "Comment 저장")
    @PostMapping(value = URI.COMMENT)
    public GowidResponse<Long> postReportComment(@ApiIgnore @CurrentUser CustomUser user,
                                                         @RequestPart(required = false) MultipartFile file,
                                                         @RequestParam(required = false) String message){

        log.info("[postReportComment] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        return ok(flowReportService.saveComment(user, message, file));
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "Comment ")
    @PutMapping(value = URI.COMMENT + "/{idx}")
    public GowidResponse<Long> putReportComment(@ApiIgnore @CurrentUser CustomUser user,
                                                        @ModelAttribute FlowDto.FlowCommentDto dto,
                                                        @PathVariable(required = false) Long idx,
                                                        @RequestParam(required = false) String message){

        log.info("[putReportComment] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        return ok(flowReportService.saveComment(user, message, null));
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "Comment ")
    @DeleteMapping(value = URI.COMMENT + "/{idx}")
    public GowidResponse<Long> delReportComment(@ApiIgnore @CurrentUser CustomUser user,
                                                @PathVariable(required = false) Long idx,
                                                @RequestParam(required = false) String message){

        log.info("[putReportComment] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        return ok(flowReportService.delComment(user, idx));
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "Comment")
    @GetMapping(value = URI.COMMENT)
    public GowidResponse<List<FlowDto.FlowCommentDto>> getReportComment(@ApiIgnore @CurrentUser CustomUser user){

        log.info("[getReportComment] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        List<FlowDto.FlowCommentDto> list = flowReportService.getReportComment(user);

        return ok(list);
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "last comment data")
    @GetMapping(value = URI.COMMENT_LAST)
    public GowidResponse<FlowDto.FlowCommentLastDto> getReportCommentLast(@ApiIgnore @CurrentUser CustomUser user){

        log.info("[getReportComment] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        FlowDto.FlowCommentLastDto data = flowReportService.getReportCommentLast(user.corp());

        return ok(data);
    }

    @PreAuthorize("hasAnyRole('MASTER','VIEWER')")
    @ApiOperation( value = "Comment File Download")
    @GetMapping(value = URI.COMMENT_FILE + "/{idx}")
    public ResponseEntity<ByteArrayResource> getCommentFile(HttpServletRequest request, @ApiIgnore @CurrentUser CustomUser user, @PathVariable(required = false) Long idx) throws UnsupportedEncodingException {

        log.info("[getCommentFile] user = {}, corp = {} " ,user.idx(), user.corp().idx());

        S3Object s3Object = flowReportService.getCommentFile(idx);
        String fileName = flowReportService.getComment(idx);

        byte[] content = null;

        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            s3Object.close();
        } catch(final IOException ex) {
            log.info("IO Error Message= " + ex.getMessage());
        }

        final byte[] data = content;
        final ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-Transfer-Encoding", "binary")
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode( fileName, "UTF-8") + "\"")
                .body(resource);
    }
}
