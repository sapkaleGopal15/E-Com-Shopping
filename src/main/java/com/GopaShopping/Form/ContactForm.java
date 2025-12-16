package com.GopaShopping.Form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactForm {

    @NotBlank(message = "Enter your name")
    private String name;

    @NotBlank(message = "Enter your mail")
    @Email
    private String email;

    @NotBlank(message = "Enter subject")
    private String subject;

    @NotBlank(message = "Please enter your message")
    private String message;
    
}
