package com.GopaShopping.Controllers;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileUploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", 
            "❌ File size too large! Please upload a file under the 20MB.");

        return "redirect:" + "/error-upload";
    }
}
