package com.GopaShopping.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.GopaShopping.Entities.User;
import com.GopaShopping.Form.LoginUserForm;
import com.GopaShopping.Form.UserForm;
import com.GopaShopping.Services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;





@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/demo")
    public String demoPage() {
        return "demo";
    }

    @GetMapping("/gopal")
    public String gopalPage() {
        return "gopal";
    }
    

    

    @RequestMapping("/index")
    public String requestMethodName() {
        return "index";
    }


    @GetMapping("/login")
    public String userLogin(Model model) {
        LoginUserForm loginUserForm = new LoginUserForm();
        model.addAttribute("loginUserForm", loginUserForm);
        return "loginUser";
    }


    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginUserForm") LoginUserForm loginUserForm, BindingResult result, HttpSession session ,Model model) {
        
        if (result.hasErrors()) {
            return "loginUser";
        }

        String email = loginUserForm.getEmail();
        String password = loginUserForm.getPassword();

        User exitUser = userService.getUserByEmail(email);

        if (exitUser==null) {
            model.addAttribute("error", "Invalid email");
            return "loginUser";
        }

        if (exitUser.getEmail().equals(email) && exitUser.getPassword().equals(password)) {
            model.addAttribute("message", "Login successful...");
            // redirectAttributes.getFlashAttributes("success","Login successful...");
            return "redirect:/home";
        }else {
            model.addAttribute("error", "Wrong Password");
            return "loginUser";
        }

    }
    
    


    @GetMapping("/add")
    public String addUser(Model model){
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "addUser";
    }

    @PostMapping("/add")
    public String saveUser(@Valid @ModelAttribute UserForm userForm, BindingResult result, HttpSession session, Model model){

        if (result.hasErrors()) {
            return "addUser";
        }
     
        User user = User.builder()
            .fName(userForm.getFName())
            .lName(userForm.getLName())
            .address(userForm.getAddress())
            .email(userForm.getEmail())
            .contact(userForm.getContact())
            .password(userForm.getPassword())
            .build();

        User savedUser = userService.saveUser(user);

        System.out.println("saved User "+ savedUser);

        model.addAttribute("message", "Registration successful...");

        //session.setAttribute("message", "Registration successful...");

        return "addUser";
    }






// =========================== User Controller ============================== 

    @GetMapping("/cart")
    public String userCart() {
        return "user/cart";
    }
    
    @GetMapping("/orders")
    public String userOrders() {
        return "user/my_orders";
    }

    @GetMapping("/order")
    public String userOrder() {
        return "user/order";
    }

    @GetMapping("/profile")
    public String profile() {
        return "user/profile";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "user/success";
    }

}
