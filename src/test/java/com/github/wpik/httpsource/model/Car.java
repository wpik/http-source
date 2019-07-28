package com.github.wpik.httpsource.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Car {
    @NotNull
    private String manufacturer;
}
