package com.coffee.app;

import com.coffee.app.security.SigninRequest;
import com.coffee.app.security.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
class AdolfCoffeeApplicationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

	@Test
	void shouldSignupUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();

        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@mail.com");
        signupRequest.setPassword("password123");

        mockMvc.perform(post("/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
	}

    @Test
    void shouldSigninUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("signinuser");
        signupRequest.setEmail("signin@mail.com");
        signupRequest.setPassword("password123");

        mockMvc.perform(post("/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setUsername("signinuser");
        signinRequest.setPassword("password123");

        mockMvc.perform(post("/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk());
    }
}
