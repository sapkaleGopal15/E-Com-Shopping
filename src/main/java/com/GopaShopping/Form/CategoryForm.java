package com.GopaShopping.Form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryForm {

    @NotBlank(message = "caregory name is required")
    private String name;

    @NotBlank(message = "category image is required")
    private String imageName;

    private Boolean isActive;
}
