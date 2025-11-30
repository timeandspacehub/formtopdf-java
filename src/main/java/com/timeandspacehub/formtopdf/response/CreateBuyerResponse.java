package com.timeandspacehub.formtopdf.response;

import lombok.Getter;
import lombok.Setter;

import com.timeandspacehub.formtopdf.entity.Buyer;

@Getter
@Setter
public class CreateBuyerResponse {
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;


    public CreateBuyerResponse(Buyer buyer){
        this.firstName = buyer.getFirstName();
        this.middleName = buyer.getMiddleName();
        this.lastName = buyer.getLastName();
        this.fullName = buyer.getFirstName() + " " + buyer.getMiddleName() + " " +buyer.getLastName();
        this.email = buyer.getEmail();
        this.phone = buyer.getPhone();
    }
    
}