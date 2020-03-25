package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.config.CronConfig;
import com.nomadconnection.dapp.core.domain.repository.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        log.error("schedule start");
        if( cronConfig.getEnabled().equals("true")){
            repoUser.findByAuthentication_Enabled(true).forEach( user -> {
                List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(user.idx());
                if(returnData.size()>0 && Integer.parseInt(returnData.get(0).getMin()) < 3){
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                service.scraping10Years(user.idx());
            });
        }
    }

}
