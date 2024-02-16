package com.myproject.luharcom.models;

import lombok.Data;

import java.time.Instant;

@Data
public class LoginDto {

    private String token;
    private Instant ttl;

}
