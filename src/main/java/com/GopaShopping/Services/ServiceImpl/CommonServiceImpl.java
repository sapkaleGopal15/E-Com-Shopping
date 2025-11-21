package com.GopaShopping.Services.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.GopaShopping.Services.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Value;

@Service
public class CommonServiceImpl implements CommonService{

    // @Value("${rupee.sign}")
	// public String rupeeSign;

    @Override
    public void removeMessage() {
        
        HttpServletRequest request = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute("successMsg");
        session.removeAttribute("errorMsg");
    }

    // @Override
    // public String rupeeSign() {
    //     return rupeeSign;
    // }

}       
