package com.GopaShopping.Form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "First name is required")
    private String fName;

    @NotBlank(message = "last name is required")
    private String lName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "[789][0-9]{9}", message = "please enter a valid number")
    private String contact;

    @NotBlank(message = "email is required")
    @Email
    private String email;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "password is required")
    private String password;
}
