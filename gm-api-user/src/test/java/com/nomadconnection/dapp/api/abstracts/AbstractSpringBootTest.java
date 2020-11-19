package com.nomadconnection.dapp.api.abstracts;

import com.nomadconnection.dapp.UserApiApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = UserApiApplication.class)
@ExtendWith(SpringExtension.class)
public abstract class AbstractSpringBootTest {
}
