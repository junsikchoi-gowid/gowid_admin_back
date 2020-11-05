package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.CronConfig;
import com.nomadconnection.dapp.core.domain.repository.res.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(cron="${spring.cron.time}")
    private void schedule() {
        log.debug("schedule start");
        if( cronConfig.getEnabled().equals("true")){
            repoUser.findByAuthentication_Enabled(true).forEach( user -> {
                List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(user.idx());
                boolean boolSchedule = true;
                if(returnData.size()>0 && Integer.parseInt(returnData.get(0).getMin()) < 3){
                    return;
                }
                try {
                    Thread.sleep(1000);
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    if(( (Integer.parseInt(ip.substring(ip.length()-1)) + Integer.parseInt(user.idx().toString())) % 2) < 1){
                        boolSchedule = false ;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(boolSchedule) {
                    service.scraping10Years(user.idx());
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
}
