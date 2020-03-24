package com.nomadconnection.dapp.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomadconnection.dapp.api.config.CronConfig;
import com.nomadconnection.dapp.api.config.EmailConfig;
import com.nomadconnection.dapp.core.domain.ResBatch;
import com.nomadconnection.dapp.core.domain.repository.ResBatchRepository;
import com.nomadconnection.dapp.core.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchedulerService {
    private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @Autowired
    private TaskScheduler taskScheduler;
    private final ScrapingService service;

    private final ResBatchRepository repoResBatch;
    private final UserRepository repoUser;

    private TaskScheduler scheduler;
    private ScheduledFuture<?> future;
    private final CronConfig cronConfig;

    @Scheduled(cron="${spring.cron.time}")
    private void schedule() {
        log.error("schedule start");
        if( cronConfig.getEnabled().equals("true")){
            repoUser.findByAuthentication_Enabled(true).forEach( user -> {
                List<ResBatchRepository.CResBatchDto> returnData = repoResBatch.findRefresh(user.idx());
                if(returnData.size()>0 && Integer.valueOf(returnData.get(0).getMin()) < 3){
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
