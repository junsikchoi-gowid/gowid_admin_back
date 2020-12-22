package com.nomadconnection.dapp.api.abstracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.nomadconnection.dapp.UserApiApplication;
import com.nomadconnection.dapp.api.dto.AccountDto;
import com.nomadconnection.dapp.api.service.UserService;
import com.nomadconnection.dapp.core.domain.user.User;
import com.nomadconnection.dapp.core.security.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserApiApplication.class)
public abstract class AbstractWebMvcTest {

	protected MockMvc mockMvc;
	private User user;
	protected CustomUser customUser;
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserService userService;

	@BeforeEach
	public void setup() {
		mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
		user = userService.getUser(67L);
		customUser = new CustomUser(user);
	}

	protected String json(Object o) throws IOException {
		return mapper.writeValueAsString(o);
	}

	protected ResultActions login(AccountDto dto) throws Exception {
		return mockMvc.perform(
			post("/auth/v1/token/issue")
				.content(json(dto))
				.contentType(MediaType.APPLICATION_JSON));
	}

	protected String extractToken(MvcResult result) throws UnsupportedEncodingException {
		return JsonPath.read(result.getResponse().getContentAsString(), "$.jwtAccess");
	}

	protected String getToken(String email, String password) throws Exception {
		AccountDto account = AccountDto.builder().email(email).password(password).build();
		return extractToken(login(account).andReturn());
	}

}
