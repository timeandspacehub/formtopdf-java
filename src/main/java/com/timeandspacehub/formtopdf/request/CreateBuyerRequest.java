package com.timeandspacehub.formtopdf.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateBuyerRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String ssn;

}