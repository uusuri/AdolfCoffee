package com.coffee.app.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {
    private String username;
    private String email;
    private String password;
}

