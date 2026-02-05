package com.libra.admin.vo;

import lombok.Data;

@Data
public class UserProfileVO {
    private String name;
    private String studentNo;
    private String employeeNo;
    private String department;
    private String email;
    private String phone;
    private Boolean isAdmin;
}
