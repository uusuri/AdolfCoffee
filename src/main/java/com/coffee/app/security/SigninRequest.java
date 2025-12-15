package com.coffee.app.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SigninRequest {
    private  String username;
    private String password;
}
