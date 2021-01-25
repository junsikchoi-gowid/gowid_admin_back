package com.nomadconnection.dapp.api.service;

import com.nomadconnection.dapp.api.abstracts.AbstractSpringBootTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("stage")
class ScrapingServiceTest extends AbstractSpringBootTest {

    @Autowired
    private ScrapingService scrapingService;

    @Test
    @DisplayName("매일배치")
    void scraping3Years() throws Exception {
        // scrapingService.scraping3Years(454L);
        // scrapingService.scrapingBatchTaxInvoice(1L, 1L, 4527L);
        // scrapingService.scrapingBatchHistory_v2(1L, 1L, 1L);
    }
}
