package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.CronConfig;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.corp.CorpRepository;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerService {

    private final ScrapingService service;
    private final ResBatchRepository repoResBatch;
    private final UserRepository repoUser;
    private final CronConfig cronConfig;

    private final UserService userService;
    private final EmailService emailService;
    private final CorpRepository repoCorp;
    private final EnvUtil envUtil;

    private final String LIV1_HOSTNAME = "CARD-LIV-WAS-LB01";
    private final int NOTICE_DAYS = 78;
    private final int EXPIRATION_DAYS = 85;

    @Value("${spring.cron.risk-enabled}")
    boolean riskEnabled;

    @Scheduled(cron="${spring.cron.time}")
    private void schedule() {
        log.debug("schedule start");
        if( cronConfig.getEnabled().equals("true")){
            repoCorp.findAll().forEach( corp -> {
                List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefreshCorp(corp.idx());
                boolean boolSchedule = true;
                if(returnData.size()>0 && Integer.parseInt(returnData.get(0).getMin()) < 3){
                    return;
                }
                try {
                    Thread.sleep(1000);
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    if(( (Integer.parseInt(ip.substring(ip.length()-1)) + Integer.parseInt(corp.idx().toString())) % 2) < 1){
                        boolSchedule = false ;
                    }

                } catch (Exception e) {
                    log.error("[schedule] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
                }

                if(boolSchedule) {
                    service.runExecutor(corp.idx());
                }
            });
        }
    }

    @Scheduled(cron="${spring.cron.endtime}")
    private void schedule_end() {
        log.debug("schedule end");
        if( cronConfig.getEnabled().equals("true")){
            repoResBatch.endBatch();
        }
    }


    @Scheduled(cron="${spring.cron.risk-time}")
    private void schedule_risk() {
        log.debug("schedule_risk start");
        if( riskEnabled ){
            repoCorp.findAll().forEach( corp -> {
                if(boolSchedule(corp)) {
                     service.runExecutorRisk(corp.idx());
                }
            });
        }
    }

    private boolean boolSchedule(Corp corp) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            if(( (Integer.parseInt(ip.substring(ip.length()-1)) + Integer.parseInt(corp.idx().toString())) % 2) < 1){
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

//    @Scheduled(cron = "${spring.cron.reset-corp-time}")
//    public void resetExpiredCorp() {
//        if (envUtil.isProd()) {
//            log.info("[ resetExpiredCorp ] scheduler start!");
//            boolean boolSchedule = true;
//            try {
//                String hostName = InetAddress.getLocalHost().getHostName();
//                if(!hostName.equals(LIV1_HOSTNAME)){
//                    boolSchedule = false ;
//                }
//            } catch (Exception e) {
//                log.error("[resetExpiredCorp] $ERROR({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
//            }
//            if (boolSchedule) {
//                List<String> statusList = new ArrayList<>();
//                statusList.add(IssuanceStatus.UNISSUED.toString());
//                statusList.add(IssuanceStatus.INPROGRESS.toString());
//                List<Corp> corps = repoCorp.findCorpByIssuanceStatus(statusList);
//                LocalDateTime now = LocalDateTime.now();
//                for (Corp corp : corps) {
//                    if (Duration.between(corp.getCreatedAt(), now).toDays() == NOTICE_DAYS) {
//                        emailService.sendResetEmail(CommonUtil.replaceHyphen(corp.resCompanyIdentityNo()));
//                    }
//                    if (Duration.between(corp.getCreatedAt(), now).toDays() >= EXPIRATION_DAYS) {
//                        log.info("[resetExpiredCorp] {}", corp.resCompanyNm());
//                        userService.initUserInfo(repoCorp.searchIdxUser(corp.idx()));
//                    }
//                }
//            }
//            log.info("[ resetExpiredCorp ] scheduler end!");
//        }
//    }

    @Scheduled(cron = "${spring.cron.koreaexim}")
    public void getKoreaexim(){
        log.info("[getKoreaexim] scheduler start");
        try {
            if (envUtil.isProd() && InetAddress.getLocalHost().getHostName().equals(LIV1_HOSTNAME)) {
                service.scrapExchange();
                log.info("[getKoreaexim] scheduler end");
            }
        }catch (Exception e){
            log.error("[getKoreaexim] scheduler error {}", e);
        }
    }
}
