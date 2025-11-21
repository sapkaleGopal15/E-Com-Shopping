package com.GopaShopping.Form;

import lombok.Data;

@Data
public class UpdateUserPassword {

    private String currentPassword;

    private String newPassword;

    private String confirmPassword;
}
