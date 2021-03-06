package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.dto.AccessManageDto;
import com.nomadconnection.dapp.api.dto.ConnectedMngDto;
import com.nomadconnection.dapp.api.exception.CodefApiException;
import com.nomadconnection.dapp.api.exception.UserNotFoundException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.codef.io.helper.*;
import com.nomadconnection.dapp.core.domain.common.CommonCodeDetail;
import com.nomadconnection.dapp.core.domain.common.CommonCodeType;
import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.repository.common.CommonCodeDetailRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResConCorpListRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import com.nomadconnection.dapp.core.domain.res.ResConCorpListStatus;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessManageService {

    private final CorpRepository repoCorp;
    private final CommonCodeDetailRepository repoCodeDetail;
    private final ConnectedMngRepository repoConnectedMng;
    private final ResConCorpListRepository repoResConCorpList;
    private final UserRepository repoUser;

    private final ScrapingService scrapingService;

    private final String urlPath = CommonConstant.getRequestDomain();
    private final List<ResConCorpListStatus> resConCorpListStatuses
            = Arrays.asList(ResConCorpListStatus.NORMAL, ResConCorpListStatus.ERROR, ResConCorpListStatus.STOP);

    private final List<ConnectedMngStatus> connectedMngStatuses
            = Arrays.asList(ConnectedMngStatus.NORMAL, ConnectedMngStatus.ERROR, ConnectedMngStatus.STOP);

    @Transactional
    public List<AccessManageDto.AccessCodeType> commonCorp(CustomUser user, CommonCodeType corpType) {

        return repoCodeDetail.findAllByCode(corpType).stream().map(AccessManageDto.AccessCodeType::from).collect(Collectors.toList());
    }

    @Transactional
    public List<ConnectedMngDto> getConnectedIdNormal(CustomUser user) {

        return repoConnectedMng.findByIdxUserAndStatusInOrderByCreatedAtDesc(user.idx(), connectedMngStatuses)
                .stream().map(ConnectedMngDto::from).collect(Collectors.toList());
    }


    @SneakyThrows
    public Long postConnectedId(CustomUser customUser, AccessManageDto dto){
        log.debug("[postConnectedId] AccessManageDto.Account = ${}", dto);

        User user = repoUser.findById(customUser.idx()).orElseThrow(() -> UserNotFoundException.builder().build());
        Long idxCorp = repoCorp.searchIdxCorp(customUser.idx());

        List<CommonCodeDetail> commonCodeDetail = new ArrayList<>();

        if(StringUtils.isEmpty(dto.getType())){
            commonCodeDetail.addAll(repoCodeDetail.findByCode(CommonCodeType.BANK_1));
            commonCodeDetail.addAll(repoCodeDetail.findByCode(CommonCodeType.STOCK));
            commonCodeDetail.addAll(repoCodeDetail.findByCode(CommonCodeType.NT));
        }else{
            commonCodeDetail.add(CommonCodeDetail.builder()
                    .code(CommonCodeType.getCommonCodeType(dto.getType()))
                    .code1(dto.getOrganization())
                    .build());
        }

        HashMap<String, Object> bodyMap = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> accountMap1;

        for(CommonCodeDetail code: commonCodeDetail){
            switch (code.code()){
                case BANK_1:
                    accountMap1 = new HashMap<>();
                    accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                    accountMap1.put("businessType",	"BK");  // ??????????????????
                    accountMap1.put("clientType",  	"B");   // ????????????(P: ??????, B: ??????)
                    accountMap1.put("organization",	code.code1());// ????????????
                    accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                    accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                    accountMap1.put("certType",     CommonConstant.CERTTYPE);
                    accountMap1.put("certFile",     dto.getCertFile());
                    list.add(accountMap1);
                    break;
                case STOCK:
                    accountMap1 = new HashMap<>();
                    accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                    accountMap1.put("businessType",	"ST");  // ??????????????????
                    accountMap1.put("clientType",  	"A");   // ????????????(P: ??????, B: ??????)
                    accountMap1.put("organization",	code.code1());// ????????????
                    accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                    accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                    accountMap1.put("certType",     CommonConstant.CERTTYPE);
                    accountMap1.put("certFile",     dto.getCertFile());
                    list.add(accountMap1);
                    break;
                case NT:
                    accountMap1 = new HashMap<>();
                    accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                    accountMap1.put("businessType",	"NT");  // ??????????????????
                    accountMap1.put("clientType",  	"A");   // ????????????(P: ??????, B: ??????, A: ?????????)
                    accountMap1.put("organization",	code.code1());// ????????????
                    accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                    accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                    accountMap1.put("certType",     CommonConstant.CERTTYPE);
                    accountMap1.put("certFile",     dto.getCertFile());
                    list.add(accountMap1);
                    break;
            }
        }

        Long idxConnectedId = saveConnectedId(list,bodyMap,user,dto);

        scrapingService.scraping3Years(null, user.idx(), idxCorp);

        return idxConnectedId;
    }

    @SneakyThrows
    private Long saveConnectedId(List<HashMap<String, Object>> list, HashMap<String, Object> bodyMap, User user, AccessManageDto dto) {
        String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;
        bodyMap.put("accountList", list);
        String strObject = ApiRequest.request(createUrlPath, bodyMap);

        JSONParser jsonParse = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParse.parse(strObject);

        String strResultCode = jsonObject.get("result").toString();
        String strResultData = jsonObject.get("data").toString();

        String code = (((JSONObject)jsonParse.parse(strResultCode)).get("code")).toString();
        ConnectedMng connectedMng;

        if(code.equals(ResponseCode.CF00000.getCode()) || code.equals(ResponseCode.CF04012.getCode())) {
            JSONObject JSONObjectData = (JSONObject) jsonObject.get("data"); //  (JSONObject)(jsonObject.get("data"));
            JSONArray JSONObjectSuccessData = (JSONArray) JSONObjectData.get("successList");
            String connectedId = (((JSONObject) jsonParse.parse(strResultData)).get("connectedId")).toString();

            connectedMng = repoConnectedMng.save(ConnectedMng.builder()
                    .connectedId(connectedId)
                    .idxUser(user.idx())
                    .name(dto.getName())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .desc1(dto.getDesc1())
                    .desc2(dto.getDesc2())
                    .issuer(dto.getIssuer())
                    .serialNumber(dto.getSerial())
                    .corp(user.corp())
                    .status(ConnectedMngStatus.NORMAL)
                    .build()
            );

            /* ConnectedId ???????????? ???????????? */
            for( Object object : JSONObjectSuccessData){
                JSONObject obj = (JSONObject) object;
                repoResConCorpList.save(
                        ResConCorpList.builder()
                                .organization(GowidUtils.getEmptyStringToString(obj, "organization"))
                                .businessType(GowidUtils.getEmptyStringToString(obj, "businessType"))
                                .clientType(GowidUtils.getEmptyStringToString(obj, "clientType"))
                                .code(GowidUtils.getEmptyStringToString(obj, "code"))
                                .countryCode(GowidUtils.getEmptyStringToString(obj, "countryCode"))
                                .extraMessage(GowidUtils.getEmptyStringToString(obj, "extraMessage"))
                                .loginType(GowidUtils.getEmptyStringToString(obj, "loginType"))
                                .message(GowidUtils.getEmptyStringToString(obj, "message"))
                                .status(ResConCorpListStatus.NORMAL)
                                .connectedId(connectedId)
                                .build()
                );
            }

        }else{
            throw new CodefApiException(ResponseCode.findByCode(code), ScrapingMessageGroup.GP00004);
        }
        return connectedMng.idx();
    }

    @Transactional
    public ConnectedMngDto updateConnectedId(CustomUser user, Long idx) {
        ConnectedMng connectedMng = new ConnectedMng();
        Optional<ConnectedMng> objConnectedMng = repoConnectedMng.findById(idx);

        if(objConnectedMng.isPresent()){
            if( objConnectedMng.get().idxUser().equals(user.idx())){
                connectedMng = objConnectedMng.get();
                connectedMng.status(ConnectedMngStatus.DELETE);
                connectedMng = repoConnectedMng.save(connectedMng);
            }
        }

        return ConnectedMngDto.builder()
            .idx(connectedMng.idx())
            .status(connectedMng.status())
            .build();
    }

    @Transactional
    public List<AccessManageDto.ResConCorpListDto> getConnectedIdCorp(CustomUser customUser, Long idxConnectedMng) {

        User user = repoUser.findById(customUser.idx()).orElseThrow(() ->UserNotFoundException.builder().build());
        List<ConnectedMng> connectedMngList = new ArrayList<>();
        List<String> connectedIdList = new ArrayList<>();
        List<Long> idxConnectedIdList = new ArrayList<>();

        if( idxConnectedMng != null){
            List<ConnectedMng> finalConnectedMngList = connectedMngList;
            repoConnectedMng.findById(idxConnectedMng).ifPresent(
                    finalConnectedMngList::add
            );
            connectedMngList = finalConnectedMngList;
        }else{
            connectedMngList = repoConnectedMng.findByIdxUser(user.idx());
        }

        for(ConnectedMng connectedMng : connectedMngList){
            connectedIdList.add(connectedMng.connectedId());
            idxConnectedIdList.add(connectedMng.idx());
        }

        List<ConnectedMngStatus> connectedMngStatuses = Arrays.asList(ConnectedMngStatus.NORMAL, ConnectedMngStatus.ERROR, ConnectedMngStatus.STOP);
        List<ResConCorpListStatus> resConCorpListStatus = Arrays.asList(ResConCorpListStatus.NORMAL, ResConCorpListStatus.ERROR, ResConCorpListStatus.STOP);
        List<AccessManageDto.ResConCorpListDto> resConCorpListDtoList = new ArrayList<>();
        List<ResConCorpListRepository.distinctData> distinctData = repoResConCorpList.findDistinctByConnectedIdInAndStatusIn(connectedIdList, resConCorpListStatus);

        for( ResConCorpListRepository.distinctData obj : distinctData ){
            List<ResConCorpList> resConCorpListList = repoResConCorpList.findByConnectedIdInAndStatusInAndBusinessTypeAndOrganization(
                    connectedIdList, resConCorpListStatus, obj.getBusinessType(), obj.getOrganization());

            ResConCorpListStatus status = ResConCorpListStatus.ERROR;

            for(ResConCorpList objList : resConCorpListList){
                if( objList.status().equals(ResConCorpListStatus.NORMAL)){
                    status = ResConCorpListStatus.NORMAL;
                }
            }

            resConCorpListDtoList.add(AccessManageDto.ResConCorpListDto.builder()
                    .businessType(obj.getBusinessType())
                    .organization(obj.getOrganization())
                    .idxConnectedIdList(idxConnectedIdList)
                    .status(status)
                    .build());
        }

        return resConCorpListDtoList;
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Long postConnectedIdCorp(CustomUser customUser, AccessManageDto dto, Long idxConnectedMng) {
        User user = repoUser.findById(customUser.idx()).orElseThrow(() ->UserNotFoundException.builder().build());
        ResConCorpList resConCorpList = new ResConCorpList();
        HashMap<String, Object> bodyMap = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> accountMap1;
        String createUrlPath = urlPath + CommonConstant.CREATE_ACCOUNT;

        switch (dto.getBusinessType()){
            case "BK":
                accountMap1 = new HashMap<>();
                accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                accountMap1.put("businessType",	"BK");  // ??????????????????
                accountMap1.put("clientType",  	"B");   // ????????????(P: ??????, B: ??????)
                accountMap1.put("organization",	dto.getOrganization());// ????????????
                accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                accountMap1.put("certType",     CommonConstant.CERTTYPE);
                accountMap1.put("certFile",     dto.getCertFile());
                list.add(accountMap1);
                break;
            case "ST":
                accountMap1 = new HashMap<>();
                accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                accountMap1.put("businessType",	"ST");  // ??????????????????
                accountMap1.put("clientType",  	"A");   // ????????????(P: ??????, B: ??????)
                accountMap1.put("organization",	dto.getOrganization());// ????????????
                accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                accountMap1.put("certType",     CommonConstant.CERTTYPE);
                accountMap1.put("certFile",     dto.getCertFile());
                list.add(accountMap1);
                break;
            case "NT":
                accountMap1 = new HashMap<>();
                accountMap1.put("countryCode",	CommonConstant.COUNTRYCODE);  // ????????????
                accountMap1.put("businessType",	"NT");  // ??????????????????
                accountMap1.put("clientType",  	"A");   // ????????????(P: ??????, B: ??????, A: ?????????)
                accountMap1.put("organization",	dto.getOrganization());// ????????????
                accountMap1.put("loginType",  	"0");   // ??????????????? (0: ?????????, 1: ID/PW)
                accountMap1.put("password",  	RSAUtil.encryptRSA(dto.getPassword1(), CommonConstant.PUBLIC_KEY));
                accountMap1.put("certType",     CommonConstant.CERTTYPE);
                accountMap1.put("certFile",     dto.getCertFile());
                list.add(accountMap1);
                break;
        }

        return saveConnectedId(list,bodyMap,user,dto);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConnectedIdCorp(CustomUser customUser,Long idxConnectedMng, String organization) {
        User user = repoUser.findById(customUser.idx()).orElseThrow(() ->UserNotFoundException.builder().build());
        Optional<ConnectedMng> connectedMng = repoConnectedMng.findByIdxAndIdxUser(idxConnectedMng, customUser.idx());

        if (connectedMng.isPresent()) {
            Optional<ResConCorpList> optResConCorpList = repoResConCorpList.findByConnectedIdAndCode(connectedMng.get().connectedId(), organization);

            if (optResConCorpList.isPresent()) {
                ResConCorpList resConCorpList = optResConCorpList.get();
                resConCorpList.status(ResConCorpListStatus.DELETE);
                repoResConCorpList.save(resConCorpList);
            }
        }
        return true;
    }

    @Transactional
    public AccessManageDto.AccessInfoAll getConnectedIdAll(CustomUser customUser) {
        AccessManageDto.AccessInfoAll accessInfoAll = new AccessManageDto.AccessInfoAll();

        List<AccessManageDto.ResConCorpListDto> conCorpList = getConnectedIdCorp( customUser,null);

        List<ConnectedMngDto> connectedList = getConnectedIdNormal(customUser);
        accessInfoAll.setConnectedMngDto(connectedList);
        accessInfoAll.setResConCorpListDtoList(conCorpList);

        return accessInfoAll;
    }

    @Transactional
    public Boolean procConnectedIdStop(CustomUser customUser, AccessManageDto.ResConCorpStatusDto dto) {
        boolean value = false;
        for( Long idxConnectedMng : dto.getIdxConnectedMngs()){
            value = updateResConCorpList(customUser, idxConnectedMng, dto.getOrganization());
        }
        return value;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateResConCorpList(CustomUser customUser,Long idxConnectedMng, String organization) {
        User user = repoUser.findById(customUser.idx()).orElseThrow(() ->UserNotFoundException.builder().build());

        repoConnectedMng.findByIdxAndIdxUser(idxConnectedMng, customUser.idx()).flatMap(connectedMng -> repoResConCorpList.findByConnectedIdAndOrganizationAndStatusIn(
                connectedMng.connectedId()
                , organization
                , resConCorpListStatuses
        )).ifPresent(resConCorpList -> resConCorpList.status(ResConCorpListStatus.DELETE));

        Optional<ConnectedMng> connectedMng = repoConnectedMng.findById(idxConnectedMng);
        if(repoResConCorpList.findByConnectedIdAndStatusIn(connectedMng.get().connectedId(), resConCorpListStatuses).size() < 1){
            connectedMng.ifPresent(
                    connectedMng1 -> {
                        connectedMng1.status(ConnectedMngStatus.DELETE);
                    }
            );
        }

        return true;
    }
}