package com.coffee.app;

import com.coffee.app.security.SigninRequest;
import com.coffee.app.security.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToAddDrink() throws Exception {
        String coffeeJson = "{\"name\": \"Brazil\", \"price\": 1.00, \"description\": \"Strong coffee\"}";
        mockMvc.perform(post("/admin/add/coffee")
                        .contentType("application/json")
                        .content(coffeeJson)
                        .with(csrf()))
                        .andExpect(status().isOk());

        String milkJson = "{\"name\": \"Whole Milk\", \"price\": 0.50, \"description\": \"Creamy milk\"}";

        mockMvc.perform(post("/admin/add/milk")
                        .contentType("application/json")
                        .content(milkJson)
                        .with(csrf()))
                        .andExpect(status().isOk());

        String syrupJson = "{\"name\": \"Vanilla\", \"price\": 0.75, \"description\": \"Sweet vanilla syrup\"}";

        mockMvc.perform(post("/admin/add/syrup")
                .contentType("application/json")
                .content(syrupJson)
                .with(csrf()))
                .andExpect(status().isOk());

        String drinkJson = "{\"name\": \"Espresso\", \"basePrice\": 2.50, " +
                "\"defaultCoffee\": {\"name\": \"Brazil\"}, \"defaultMilk\": {\"name\": \"Whole Milk\"}, " +
                "\"defaultSyrups\": [{\"name\": \"Vanilla\"}]}";

        mockMvc.perform(post("/admin/add/drink")
                        .contentType("application/json")
                        .content(drinkJson)
                        .with(csrf()))
                        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldNotAllowUserToAddDrink() throws Exception {
        String coffeeJson = "{\"name\": \"Brazil\", \"price\": 1.00, \"description\": \"Strong coffee\"}";

        mockMvc.perform(post("/admin/add/coffee")
                        .contentType("application/json")
                        .content(coffeeJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        String milkJson = "{\"name\": \"Whole Milk\", \"price\": 0.50, \"description\": \"Creamy milk\"}";

        mockMvc.perform(post("/admin/add/milk")
                        .contentType("application/json")
                        .content(milkJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        String syrupJson = "{\"name\": \"Vanilla\", \"price\": 0.75, \"description\": \"Sweet vanilla syrup\"}";

        mockMvc.perform(post("/admin/add/syrup")
                        .contentType("application/json")
                        .content(syrupJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        String drinkJson = "{\"name\": \"Espresso\", \"basePrice\": 2.50, " +
                "\"defaultCoffee\": {\"name\": \"Brazil\"}, \"defaultMilk\": {\"name\": \"Whole Milk\"}, " +
                "\"defaultSyrups\": [{\"name\": \"Vanilla\"}]}";

        mockMvc.perform(post("/admin/add/drink")
                        .contentType("application/json")
                        .content(drinkJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
