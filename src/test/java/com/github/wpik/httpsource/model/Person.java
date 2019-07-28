package com.github.wpik.httpsource.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Person {
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @Min(18)
    private int age;
    @NotNull
    @Valid
    private Address address;
}
