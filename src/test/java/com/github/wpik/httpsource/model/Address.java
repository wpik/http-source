package com.github.wpik.httpsource.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Address {
    @NotEmpty
    private String city;
}
