package com.nomadconnection.dapp.api.v2.service.flow;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.Download;
import com.nomadconnection.dapp.api.config.AwsS3Config;
import com.nomadconnection.dapp.api.exception.BadRequestedException;
import com.nomadconnection.dapp.api.exception.CorpNotRegisteredException;
import com.nomadconnection.dapp.api.exception.EntityNotFoundException;
import com.nomadconnection.dapp.api.exception.FileUploadException;
import com.nomadconnection.dapp.api.helper.GowidUtils;
import com.nomadconnection.dapp.api.service.AwsS3Service;
import com.nomadconnection.dapp.api.v2.dto.FlowDto;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.flow.*;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.flow.*;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountHistoryRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResAccountRepository;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import com.nomadconnection.dapp.core.domain.res.ResAccount;
import com.nomadconnection.dapp.core.domain.res.ResAccountHistory;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.exception.BaseException;
import com.nomadconnection.dapp.core.exception.DuplicatedException;
import com.nomadconnection.dapp.core.exception.NotFoundException;
import com.nomadconnection.dapp.core.exception.result.ResultType;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowReportService {

    private final FlowReportMonthRepository repoFlowReportMonth;
    private final FlowTagMonthRepository repoFlowTagMonth;
    private final FlowTagConfigRepository repoFlowTagConfig;
    private final FlowCommentRepository repoFlowComment;
    private final CorpRepository repoCorp;
    private final ResAccountRepository repoResAccount;
    private final ConnectedMngRepository repoConnectedMng;
    private final ResAccountHistoryRepository repoResAccountHistory;
    private final FlowCommentStatusRepository repoFlowCommentStatus;
    private final AwsS3Service awsS3Service;

    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String FLOW_PATH = "/flow/";

    @Transactional(readOnly = true)
    public List<FlowDto.FlowReportByPeriodDto> getReportStatusMonth(Long idxCorp, String toDate) {

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());

        LocalDate now = LocalDate.parse(toDate, DATEFORMATTER);
        toDate = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String fromDate = now.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMM"));

        return repoFlowReportMonth.findByCorpAndFlowDateBetween(corp, fromDate, toDate).
                stream().map(FlowDto.FlowReportByPeriodDto::from).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<FlowDto.FlowAccountDto> getFlowAccount(Long idxCorp, FlowDto.SearchFlowAccount searchFlowAccount) {

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());

        return repoResAccount.FlowAccountList(
                corp.idx(),
                searchFlowAccount.getFavorite()).stream().map(FlowDto.FlowAccountDto::from).collect(Collectors.toList());
    }

    /**
     * 4개월간의 월별 리포트
     */
    @Transactional(readOnly = true)
    public FlowDto.FlowCashInfo getReportTableMonth(Long idxCorp, String toDate) {

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());

        List<FlowDto.FlowCashFluctuationDto> flowCashFluctuationLists = getFlowCashFluctuationLists(corp, toDate);
        List<FlowDto.FlowTagMonthDto> flowTagList = getFlowCashLists(corp, toDate);

        return FlowDto.FlowCashInfo.builder()
                .flowCashFluctuationList(flowCashFluctuationLists)
                .flowTagList(flowTagList)
                .build();
    }

    /**
     * 월별 잔고
     */
    private List<FlowDto.FlowCashFluctuationDto> getFlowCashFluctuationLists(Corp corp, String toDate) {

        LocalDate now = LocalDate.parse(toDate, DATEFORMATTER);
        String fromDate = now.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMM"));

        return repoFlowReportMonth.findByCorpAndFlowDateBetween(corp, fromDate, toDate)
                .stream().map(FlowDto.FlowCashFluctuationDto::from).collect(Collectors.toList());
    }

    /**
     * 계정 분류 별 합
     */
    @Transactional(readOnly = true)
    List<FlowDto.FlowTagMonthDto> getFlowCashLists(Corp corp, String toDate) {

        LocalDate now = LocalDate.parse(toDate, DATEFORMATTER);
        toDate = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String fromDate = now.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMM"));

        return repoFlowTagMonth.findByCorpAndFlowDateBetween(corp.idx(), fromDate, toDate)
                .stream().map(FlowDto.FlowTagMonthDto::excel).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlowDto.FlowTagConfigDto> getTagConfigList(Long idxCorp) {

        Corp corp = repoCorp.findById(idxCorp).orElseThrow(() -> CorpNotRegisteredException.builder().build());

        return repoFlowTagConfig.findByCorpAndDeleteYnIsFalse(corp)
                .stream().map(FlowDto.FlowTagConfigDto::from).collect(Collectors.toList());
    }

    @Transactional
    public FlowDto.FlowTagConfigDto addFlowTagConfig(FlowDto.FlowTagConfigDto dto, Corp corp) {

        if (repoFlowTagConfig.findByCorpAndCodeLv3AndCodeLv4(corp, dto.getCodeLv3().trim(), dto.getCodeLv4().trim()).isPresent() || !ObjectUtils.isEmpty(dto.code4)) {
            throw new DuplicatedException("duplicated codeLv4 = " + dto.codeLv4);
        }

        FlowTagConfig flowTagConfig = repoFlowTagConfig.save(FlowTagConfig.of(FlowDto.FlowTagConfigDto.of(dto), corp));

        return FlowDto.FlowTagConfigDto.builder().idx(flowTagConfig.idx()).build();
    }

    @Transactional
    public FlowDto.FlowTagConfigDto updateFlowTagConfig(Corp corp, Long idx, FlowDto.FlowTagConfigDto dto) {

        if (repoFlowTagConfig.findByCorpAndCodeLv1AndCodeLv2AndCodeLv3AndCodeLv4AndIdxNot(
                corp, dto.getCodeLv1().trim(), dto.getCodeLv2().trim(), dto.getCodeLv3().trim(), dto.getCodeLv4().trim(), idx).isPresent()) {
            throw new DuplicatedException("duplicated codeLv4 = " + dto.codeLv4);
        }

        FlowTagConfig data = repoFlowTagConfig.findById(idx).orElseThrow(
                () -> EntityNotFoundException.builder().build());

        if (ObjectUtils.isEmpty(data.code4())) {
            data.enabled(dto.getEnabled())
                    .code1(dto.getCode1())
                    .code2(dto.getCode2())
                    .code3(dto.getCode3())
                    .code4(dto.getCode4())
                    .codeLv1(dto.getCodeLv1())
                    .codeLv2(dto.getCodeLv2())
                    .codeLv3(dto.getCodeLv3())
                    .codeLv4(dto.getCodeLv4())
                    .enabled(dto.getEnabled())
                    .tagOrder(dto.getTagOrder())
                    .codeDesc(dto.getCodeDesc());
        } else {
            data.enabled(dto.getEnabled())
                    .tagOrder(dto.getTagOrder());
        }


        return FlowDto.FlowTagConfigDto.from(data);
    }

    /**
     * 사용중인 flowTag 는 삭제가 안됨
     */
    @Transactional(rollbackFor = Exception.class)
    public FlowDto.FlowTagConfigDto deleteFlowTagConfig(Long idxFlowTagConfig, Corp corp) {

        FlowTagConfig data = repoFlowTagConfig.findById(idxFlowTagConfig).orElseThrow(
                () -> new NotFoundException("idx not found")
        );

        FlowTagConfig updateData = FlowTagConfig.delete(data, corp);
        repoFlowTagConfig.save(updateData);

        return FlowDto.FlowTagConfigDto.from(updateData);
    }


    @Transactional(rollbackFor = Exception.class)
    public FlowDto.FlowAccountDto saveAccount(FlowDto.AccountFavoriteDto dto, Long idx) {

        ResAccount resAccount = repoResAccount.findById(idx).orElseThrow(
                () -> new BaseException(ResultType.NOT_FOUND)
        );

        resAccount.favorite(dto.getFavorite());
        resAccount.enabled(dto.getEnabled());

        return FlowDto.FlowAccountDto.builder()
                .idxAccount(resAccount.idx())
                .resAccount(resAccount.resAccount())
                .currency(resAccount.resAccountCurrency())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<FlowDto.FlowAccountHistoryDto> getFlowAccountHistory(FlowDto.SearchFlowAccountHistory searchDto, Pageable pageable) {

        Page<FlowDto.FlowAccountHistoryDto> flowAccountHistoryList;

        if (searchDto.getInOutType().toLowerCase().equals("in")) {
            flowAccountHistoryList = repoResAccountHistory
                    .searchInResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getFrom(), searchDto.getTo(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from);
        } else if (searchDto.getInOutType().toLowerCase().equals("out")) {
            flowAccountHistoryList = repoResAccountHistory
                    .searchOutResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getFrom(), searchDto.getTo(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from);
        } else {
            flowAccountHistoryList = repoResAccountHistory
                    .searchAllResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getFrom(), searchDto.getTo(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from);
        }

        flowAccountHistoryList.forEach(
                flowAccountHistoryDto -> Hibernate.initialize(flowAccountHistoryDto.flowTagConfig)
        );

        return flowAccountHistoryList;
    }

    @Transactional(readOnly = true)
    public FlowDto.FlowExcelPath getExcelFlowAccountHistory(Long idxCorp, FlowDto.SearchFlowAccountHistory searchDto, Pageable pageable) throws IOException {

        List<FlowDto.FlowAccountHistoryDto> flowAccountHistoryList;

        if (!ObjectUtils.isEmpty(searchDto.getInOutType()) && searchDto.getInOutType().toLowerCase().equals("in")) {
            flowAccountHistoryList = repoResAccountHistory
                    .searchInResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getTo(), searchDto.getFrom(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from).getContent();
        } else if (!ObjectUtils.isEmpty(searchDto.getInOutType()) && searchDto.getInOutType().toLowerCase().equals("out")) {
            flowAccountHistoryList = repoResAccountHistory
                    .searchOutResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getTo(), searchDto.getFrom(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from).getContent();
        } else {
            flowAccountHistoryList = repoResAccountHistory
                    .searchAllResAccountHistoryLikeList(searchDto.getArrayResAccount(), searchDto.getTo(), searchDto.getFrom(), searchDto.getSearchWord(), pageable)
                    .map(FlowDto.FlowAccountHistoryDto::from).getContent();
        }


        String fileName= createListToExcel(idxCorp, flowAccountHistoryList, searchDto);
        final byte[] data = getExcelFlowFile(fileName);

        return FlowDto.FlowExcelPath.builder()
                .fileName(fileName)
                .file(data)
                .build();
    }

    private byte[] getExcelFlowFile(String fileName) throws IOException {

        File file = new File(fileName);

        return Files.readAllBytes(file.toPath());
    }


    public String createListToExcel(Long idxCorp, List<FlowDto.FlowAccountHistoryDto> dataList, FlowDto.SearchFlowAccountHistory searchDto) {
        FileOutputStream fos = null;
        SXSSFWorkbook workbook = null;

        SXSSFRow row;
        SXSSFCell cell;
        CellStyle styleMoneyFormat;
        String fileDownLoadPath = null;
        int index = 0;

        int rowIndex = 1;
        String[] cellHeader = {"", "거래일시", "계좌번호", "적요1", "적요2", "적요3", "적요4", "입금", "출금", "거래후잔액", "성격", "계정", "메모"};
        try {
            workbook = new SXSSFWorkbook();
            workbook.setCompressTempFiles(true);
            SXSSFSheet sheet = workbook.createSheet(searchDto.getTo() + "~" + searchDto.getFrom());
            sheet.setRandomAccessWindowSize(100);
            // 메모리 행 100개로 제한, 초과 시 Disk로 flush //셀 칼럼 크기 설정
            sheet.setColumnWidth(1, 256 * 15);
            sheet.setColumnWidth(2, 256 * 15);
            sheet.setColumnWidth(3, 256 * 15);
            sheet.setColumnWidth(4, 256 * 15);
            sheet.setColumnWidth(5, 256 * 15);
            sheet.setColumnWidth(6, 256 * 15);
            sheet.setColumnWidth(7, 256 * 15);
            sheet.setColumnWidth(8, 256 * 15);
            sheet.setColumnWidth(9, 256 * 15);
            sheet.setColumnWidth(10, 256 * 15);
            sheet.setColumnWidth(11, 256 * 15);
            sheet.setColumnWidth(12, 256 * 15);

            row = sheet.createRow(0);
            styleMoneyFormat = workbook.createCellStyle();
            CreationHelper ch = workbook.getCreationHelper();
            styleMoneyFormat.setDataFormat(ch.createDataFormat().getFormat("#,##0"));

            // 헤더 적용
            for (String head : cellHeader) {
                cell = row.createCell(index++);
                cell.setCellValue(head);
            }
            for (FlowDto.FlowAccountHistoryDto dataDto : dataList) {

                row = sheet.createRow(rowIndex++);
                int indexCol = 1;
                cell = row.createCell(indexCol);
                cell.setCellValue("");
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccountTrDateTime());
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccount());
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccountDesc1());
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccountDesc2());
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccountDesc3());
                cell = row.createCell(indexCol++);
                cell.setCellValue(dataDto.getResAccountDesc4());
                cell = row.createCell(indexCol++);
                cell.setCellStyle(styleMoneyFormat);
                cell.setCellValue(GowidUtils.doubleTypeGet(dataDto.getResAccountIn()));
                cell = row.createCell(indexCol++);
                cell.setCellStyle(styleMoneyFormat);
                cell.setCellValue(GowidUtils.doubleTypeGet(dataDto.getResAccountOut()));
                cell = row.createCell(indexCol++);
                cell.setCellStyle(styleMoneyFormat);
                cell.setCellValue(GowidUtils.doubleTypeGet(dataDto.getResAfterTranBalance()));

                if (!ObjectUtils.isEmpty(dataDto.getFlowTagConfig())) {
                    cell = row.createCell(indexCol++);
                    cell.setCellValue(GowidUtils.getEmptyStringToString(dataDto.getFlowTagConfig().codeLv3()));
                    cell = row.createCell(indexCol++);
                    cell.setCellValue(GowidUtils.getEmptyStringToString(dataDto.getFlowTagConfig().codeLv4()));
                } else {
                    cell = row.createCell(indexCol++);
                    cell.setCellValue("");
                    cell = row.createCell(indexCol++);
                    cell.setCellValue("");
                }

                cell = row.createCell(indexCol);
                cell.setCellValue(dataDto.getMemo());

            }
            String orgFileName = searchDto.getTo() + "_" + searchDto.getFrom() + "_" + idxCorp + ".xlsx";
            fileDownLoadPath = FLOW_PATH + orgFileName;
            fos = new FileOutputStream(fileDownLoadPath + orgFileName);
            workbook.write(fos);

        } catch (Exception e) {
            e.printStackTrace();
            if (fos != null) try {
                fos.close();
            } catch (Exception ignore) {
            }
        } finally {
            try {
                workbook.close();
                workbook.dispose();
                if (fos != null) try {
                    fos.close();
                } catch (Exception ignore) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileDownLoadPath;
    }

    @Transactional(rollbackFor = Exception.class)
    public FlowDto.FlowAccountHistoryDto saveAccountHistory(FlowDto.FlowAccountHistoryUpdateDto dto) {

        ResAccountHistory resAccountHistory = repoResAccountHistory.findById(dto.getIdx()).orElseThrow(
                () -> EntityNotFoundException.builder().build()
        );

        if (!ObjectUtils.isEmpty(dto.getMemo())) {
            resAccountHistory.memo(dto.getMemo());
        }

        FlowTagConfig flowTagConfig;

        if (!ObjectUtils.isEmpty(dto.getIdxFlowTagConfig())) {
            flowTagConfig = repoFlowTagConfig.findById(dto.idxFlowTagConfig).orElseThrow(
                    () -> BadRequestedException.builder().build()
            );
            resAccountHistory.flowTagConfig(flowTagConfig);
        }

        if (!ObjectUtils.isEmpty(dto.getTagValue())) {
            resAccountHistory.tagValue(dto.getTagValue());
        }


        return repoResAccountHistory.findById(dto.getIdx()).map(FlowDto.FlowAccountHistoryDto::from).get();
    }

    @Transactional
    public List<FlowDto.FlowCommentDto> getReportComment(CustomUser user) {

        List<FlowComment> FlowCommentlist = repoFlowComment.findByCorpAndEnabledOrderByCreatedAtDesc(user.corp(), true);

        List<FlowDto.FlowCommentDto> FlowCommentDtoList
                = FlowCommentlist.stream().map(FlowDto.FlowCommentDto::from).collect(Collectors.toList());

        FlowComment flowComment = repoFlowComment.findTopByCorpAndEnabledOrderByCreatedAtDesc(user.corp(), true).orElse(null);

        FlowCommentStatus flowCommentStatus = repoFlowCommentStatus.findTopByUser(user.user()).orElseGet(
                () -> FlowCommentStatus.builder().user(user.user()).build());

        flowCommentStatus.flowComment(flowComment);

        repoFlowCommentStatus.save(flowCommentStatus);

        return FlowCommentDtoList;
    }


    @Transactional(readOnly = true)
    public FlowDto.FlowCommentLastDto getReportCommentLast(CustomUser user){

        FlowComment dto = repoFlowComment.findTopByCorpAndEnabledOrderByCreatedAtDesc(user.corp(), true).orElseThrow(
                () -> EntityNotFoundException.builder().build()
        );

        Optional<FlowCommentStatus> flowCommentStatus = repoFlowCommentStatus.findTopByUser(user.user());

        boolean readYn = false;

        if(flowCommentStatus.isPresent()){
            if(dto.idx().equals(flowCommentStatus.get().flowComment().idx())){
                readYn = true;
            }
        }


        return FlowDto.FlowCommentLastDto.builder()
                .comment(StringUtils.isEmpty(dto.comment()) ? dto.orgFileName() : dto.comment())
                .readYn(readYn)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public Long saveComment(CustomUser user, String message, MultipartFile file) {
        String s3Link = null, fileName = null, fileOriginal = null, s3Key = null;
        long fileSize = 0L;
        if (file != null) {
            fileName = FilenameUtils.getExtension(file.getOriginalFilename());
            s3Key = "comments/" + user.corp().idx() + "/" + user.idx() + "/" + file.getOriginalFilename();

            File uploadFile = new File(file.getOriginalFilename());
            uploadFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(uploadFile);
            fos.write(file.getBytes());
            fos.close();

            fileOriginal = file.getOriginalFilename();
            fileSize = file.getSize();

            try {
                s3Link = awsS3Service.s3FileUpload(uploadFile, s3Key);
                uploadFile.delete();
            } catch (Exception e) {
                uploadFile.delete();
                awsS3Service.s3FileDelete(s3Key);

                log.error("[saveComment] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                throw FileUploadException.builder().build();
            }
        }

        Corp corp = repoCorp.findById(user.corp().idx()).orElseThrow(() -> CorpNotRegisteredException.builder().build());


        return repoFlowComment.save(FlowComment.of(FlowComment.builder()
                .comment(message)
                .corp(corp)
                .s3Link(s3Link)
                .fileName(fileName)
                .orgFileName(fileOriginal)
                .fileSize(fileSize)
                .s3Key(s3Key)
                .enabled(true)
                .build(), corp.user())).idx();
    }

    /**
     * accountHistory and ( FlowReportMonth, FlowTagMonth ) deffer by 3 minutes
     */
    @Transactional
    public FlowDto.FlowReportDto createReport(Corp corp) {

        FlowReportMonth flowReportMonth = repoFlowReportMonth.findTopByCorpOrderByUpdatedAtDesc(corp);

        ResAccount resAccount = repoConnectedMng.accountUpdateTime(corp);
        ResAccountHistory resAccountHistory = repoConnectedMng.accountHistoryUpdateTime(corp);

        if( flowReportMonth == null){
            procCreateReport(corp);
        }else if (flowReportMonth != null && resAccount != null && resAccountHistory != null) {
            if (flowReportMonth.getUpdatedAt().isBefore(resAccount.getUpdatedAt())
                    || flowReportMonth.getUpdatedAt().isBefore(resAccountHistory.getUpdatedAt())) {
                procCreateReport(corp);
            }
        }

        return FlowDto.FlowReportDto.builder().createdAt(resAccountHistory.getUpdatedAt()).build();
    }

    @Transactional
    void procCreateReport(Corp corp) {
        List<ResAccount> arrayResAccount = repoConnectedMng.accountList(corp);
        List<String> arrayAccount = arrayResAccount.stream().map(ResAccount::resAccount).collect(Collectors.toList());

        String localDateTime = LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        repoResAccountHistory.monthStatics(arrayAccount, localDateTime).forEach(
                dto -> {
                    Optional<FlowTagConfig> flowTagConfig;

                    if (ObjectUtils.isEmpty(dto.getIdxFlowTagConfig())) {

                        String flowCode;
                        flowCode = "B000";
                        if (dto.getFlowIn().equals(0L)) {
                            flowCode = "A000";
                        }

                        flowTagConfig = Optional.ofNullable(repoFlowTagConfig.findByCorpAndFlowCodeAndCode4(corp, "FLOW", flowCode).orElseThrow(
                                () -> EntityNotFoundException.builder().build()
                        ));
                    } else {
                        log.debug("dto.getIdxFlowTagConfig() = {} ", dto.getIdxFlowTagConfig());
                        flowTagConfig = Optional.ofNullable(repoFlowTagConfig.findById(dto.getIdxFlowTagConfig()).orElseThrow(
                                () -> EntityNotFoundException.builder().build()
                        ));
                    }

                    FlowTagMonth flowTagMonth = repoFlowTagMonth.findTopByCorpAndFlowDate(corp, dto.getMonth()).orElseGet(
                            () -> FlowTagMonth.builder().build()
                    );

                    long total = dto.getFlowIn() + dto.getFlowOut();

                    flowTagMonth.corp(corp)
                            .flowTagConfig(flowTagConfig.get())
                            .flowDate(dto.getMonth())
                            .flowTotal(GowidUtils.doubleTypeGet(String.valueOf(total)));

                    repoFlowTagMonth.save(flowTagMonth);
                });

        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        for(Long i = 1L ; startDate.isBefore(endDate) ; startDate = startDate.plusMonths(i)){

            log.debug("startdate = {}" , startDate );

            String flowDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
            String lastDaty = YearMonth.from(startDate).atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            FlowReportMonth flowReportMonth = repoFlowReportMonth.findByCorpAndFlowDate(corp, flowDate).orElseGet(
                    () -> FlowReportMonth.builder()
                            .corp(corp)
                            .flowDate(flowDate)
                            .flowIn(0.0)
                            .flowOut(0.0)
                            .flowTotal(0.0)
                            .build()
            );

            ResAccountHistoryRepository.CMonthInOutStaticsDto dto =
                    repoResAccountHistory.monthInOutStatics(arrayAccount, flowDate.concat("00"), flowDate.concat("32"));


            flowReportMonth.corp(corp)
                    .idx(flowReportMonth.idx())
                    .flowDate(flowDate)
                    .flowIn(ObjectUtils.isEmpty(dto)?0.0:GowidUtils.doubleTypeGet(dto.getFlowIn().toString()))
                    .flowOut(ObjectUtils.isEmpty(dto)?0.0:GowidUtils.doubleTypeGet(dto.getFlowOut().toString()))
                    .flowTotal(repoResAccount.recentBalanceCorp(corp.idx(), lastDaty));

            repoFlowReportMonth.save(flowReportMonth);
        }
    }

    @Transactional
    public Long delComment(CustomUser user, Long idx) {
        FlowComment comments = repoFlowComment.findById(idx).orElseThrow(
                () -> EntityNotFoundException.builder().build()
        );

        comments.enabled(false);

        return comments.idx();

    }

    private final AwsS3Config config;

    @Transactional(readOnly = true)
    public S3Object getCommentFile(Long idx) {
        FlowComment comments = repoFlowComment.findById(idx).orElseThrow(
                () -> EntityNotFoundException.builder().build()
        );

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.accessKey(), config.secretKey())))

                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(config.bucketName(), comments.s3Key()));

        return s3Object;
    }

    @Transactional(readOnly = true)
    public String getComment(Long idx) {
        return repoFlowComment.findById(idx).get().orgFileName();
    }

    public FlowDto.FlowExcelPath getReportTableMonthFile(Long idxCorp, String searchDate) throws IOException {

        FlowDto.FlowCashInfo flowCashInfo = getReportTableMonth(idxCorp, searchDate);

        String fileName = createReportTableMonthToExcel(flowCashInfo);

        final byte[] data = getExcelFlowFile(fileName);

        return FlowDto.FlowExcelPath.builder()
                .fileName(fileName)
                .file(data)
                .build();
    }

    private String createReportTableMonthToExcel(FlowDto.FlowCashInfo flowCashInfo) {

        List<FlowDto.FlowCashFluctuationDto> flowCashFluctuationList = flowCashInfo.getFlowCashFluctuationList();
        List<FlowDto.FlowTagMonthDto> flowTagList = flowCashInfo.getFlowTagList();

        FileOutputStream fos = null;
        SXSSFWorkbook workbook = null;

        SXSSFRow row;
        SXSSFCell cell;
        CellStyle styleMoneyFormat;
        String fileDownLoadPath = null;
        int index = 0;

        int rowIndex = 1;
        String[] cell1 = { flowCashFluctuationList.get(3).getFlowDate()
                , flowCashFluctuationList.get(2).getFlowDate()
                , flowCashFluctuationList.get(1).getFlowDate()
                , flowCashFluctuationList.get(0).getFlowDate() , "기간총계"};

        Double[] cell2 = { flowCashFluctuationList.get(4).getFlowTotal()
                , flowCashFluctuationList.get(3).getFlowTotal()
                , flowCashFluctuationList.get(2).getFlowTotal()
                , flowCashFluctuationList.get(1).getFlowTotal()
                , flowCashFluctuationList.get(4).getFlowTotal() +
                flowCashFluctuationList.get(3).getFlowTotal() +
                flowCashFluctuationList.get(2).getFlowTotal() +
                flowCashFluctuationList.get(1).getFlowTotal()
        };

        try {
            workbook = new SXSSFWorkbook();
            workbook.setCompressTempFiles(true);
            SXSSFSheet sheet = workbook.createSheet("sheet");
            sheet.setRandomAccessWindowSize(100);
            sheet.setColumnWidth(1, 256 * 15);
            sheet.setColumnWidth(2, 256 * 15);
            sheet.setColumnWidth(3, 256 * 15);
            sheet.setColumnWidth(4, 256 * 15);
            sheet.setColumnWidth(5, 256 * 15);
            sheet.setColumnWidth(6, 256 * 15);
            sheet.setColumnWidth(7, 256 * 15);
            sheet.setColumnWidth(8, 256 * 15);
            sheet.setColumnWidth(9, 256 * 15);
            sheet.setColumnWidth(10, 256 * 15);
            sheet.setColumnWidth(11, 256 * 15);
            sheet.setColumnWidth(12, 256 * 15);

            row = sheet.createRow(0);
            styleMoneyFormat = workbook.createCellStyle();
            CreationHelper ch = workbook.getCreationHelper();
            styleMoneyFormat.setDataFormat(ch.createDataFormat().getFormat("#,##0"));

            // row 1
            cell = row.createCell(index++);
            cell.setCellValue("계정");
            cell = row.createCell(index++);
            cell.setCellValue("계정");
            cell = row.createCell(index++);
            cell.setCellValue("계정");
            cell = row.createCell(index++);
            cell.setCellValue("계정");

            for (String obj : cell1) {
                cell = row.createCell(index++);
                cell.setCellValue(obj);
            }

            // row 2
            row = sheet.createRow(1);

            cell = row.createCell(index++);
            cell.setCellValue("입금");
            cell = row.createCell(index++);
            cell.setCellValue("입금");
            cell = row.createCell(index++);
            cell.setCellValue("입금");
            cell = row.createCell(index++);
            cell.setCellValue("입금");

            for (Double obj : cell2) {
                cell = row.createCell(index++);
                cell.setCellValue(obj);
            }

            // row 3

            for (FlowDto.FlowTagMonthDto dto : flowTagList) {

                row = sheet.createRow(rowIndex++);
                int indexCol = 1;
                cell = row.createCell(indexCol);
                cell.setCellValue(dto.getCodeLv1());

                cell = row.createCell(indexCol++);
                cell.setCellValue(dto.getCodeLv2());

                cell = row.createCell(indexCol++);
                cell.setCellValue(dto.getCodeLv3());

                cell = row.createCell(indexCol++);
                cell.setCellValue(dto.getCodeLv4());

                if(dto.getFlowDate().equals(flowCashFluctuationList.get(3).getFlowDate())){
                    cell = row.createCell(indexCol++);
                    cell.setCellValue(dto.getCodeLv4());
                }
            }
            String orgFileName = "dd" + ".xlsx";
            fileDownLoadPath = FLOW_PATH + orgFileName;
            fos = new FileOutputStream(fileDownLoadPath + orgFileName);
            workbook.write(fos);

        } catch (Exception e) {
            e.printStackTrace();
            if (fos != null) try {
                fos.close();
            } catch (Exception ignore) {
            }
        } finally {
            try {
                workbook.close();
                workbook.dispose();
                if (fos != null) try {
                    fos.close();
                } catch (Exception ignore) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileDownLoadPath;
    }
}
