package com.GopaShopping.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.ContactForm;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Form.UserForm;
import com.GopaShopping.Repositories.UserRepository;
import com.GopaShopping.Services.ServiceImpl.AdminServiceImpl;
import com.GopaShopping.Services.ServiceImpl.UserServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;




@Controller
public class PageController {

    public record TeamMember(String name, String role, String photoUrl, String linkedin, String twitter, String github) {}
    public record Review(String userName, String comment) {}

    public record ContactInfo(String address, String phone, String email, String mapEmbed) {}

    @Autowired
    private AdminServiceImpl adminServiceImpl;


    @Autowired
    private UserServiceImpl userServiceImpl;
    
    @GetMapping("/")
    public String homepage(Model model){
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", adminServiceImpl.getAllActiveProducts());
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String index(Model model){
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", adminServiceImpl.getAllActiveProducts());
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
             // Dynamic statistics
        model.addAttribute("stats", Map.of(
            "users", "1M+",
            "orders", "5M+",
            "products", "10K+",
            "reviews", "50K+"
        ));

        // Dynamic team members
        model.addAttribute("team", List.of(
            new TeamMember("John Doe", "CEO", "/images/team1.jpg", "https://linkedin.com", "#", "#"),
            new TeamMember("Sarah Lee", "CTO", "/images/team2.jpg", "https://linkedin.com", "#", "#"),
            new TeamMember("Ravi Kumar", "Lead Developer", "/images/team3.jpg", "https://linkedin.com", "#", "#"),
            new TeamMember("Aisha Patel", "Marketing Head", "/images/team4.jpg", "https://linkedin.com", "#", "#")
        ));

        // Customer reviews
        model.addAttribute("reviews", List.of(
            new Review("Priya Sharma", "Excellent service and great quality!"),
            new Review("Amit Singh", "Fast delivery and responsive support!"),
            new Review("Neha Verma", "Loved the products. Highly recommend ShopEase!")
        ));
        return "about";
    }
    
    @GetMapping("/product")
    public String product(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", adminServiceImpl.getAllActiveProducts());
        return "product";
    }

    @GetMapping("/product/view/{id}")
    public String productView(@PathVariable Long id, Model model) {
        Products temp = adminServiceImpl.productFindById(id);
        model.addAttribute("products", temp);
        return "view_product";
    }

    @GetMapping("/product/view/category/{category}")
    public String productViewByCategory(@PathVariable String category, Model model){
        // List<Products> products = adminServiceImpl.productsFindByCategory(category);
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", adminServiceImpl.getAllActiveProductsAndCategory(category));
        model.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contactInfo", new ContactInfo(
                "123 Market Street, Mumbai, India",
                "+91 9876543210",
                "support@shopease.com",
                "https://www.google.com/maps/embed?pb=!1m18..."
        ));
        model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }

    @GetMapping("slide")
    public String slides() {
        return "slides";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }



    // ===================== Processing ====================

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{

        Boolean existUser = userServiceImpl.getUserByEmail(userForm.getEmail());
        
        if (existUser) {
            session.setAttribute("errorMsg", "Email already register!");
            return "redirect:/register";
        }

                
        if (result.hasErrors()) {
            return "register";
        }

        if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
            session.setAttribute("errorMsg", "Confirm password is not match!");
            return "register";
        }

        if (userForm.getContact().length() != 10) {
            session.setAttribute("errorMsg", "Invalid Mobile number");
            return "register";
        }

        if (userForm.getPincode().length() != 6) {
            session.setAttribute("errorMsg", "Invalid Postal code");
            return "register";
        }

        String tempImage = "default_profile_image.png";
        String image = file == null || file.isEmpty() ? tempImage : file.getOriginalFilename();
        String userRole = "Guest";

        User user = User.builder()
            .name(userForm.getName())
            .contact(userForm.getContact())
            .email(userForm.getEmail())
            .address(userForm.getAddress())
            .city(userForm.getCity())
            .state(userForm.getState())
            .pincode(userForm.getPincode())
            .password(userForm.getPassword())
            .role(userRole)
            .profileImage(image)
            .status("Active")
            .build();

        User user2 = userServiceImpl.savedUser(user);

        if (!ObjectUtils.isEmpty(user2)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "You have account register Successfully...Please login and continue");
            return "redirect:/login";
        }else{
            session.setAttribute("errorMsg", "Somathing went wrong..!");
            return "redirect:/register";
        }
    }

    @PostMapping("/dologin")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        
        if (email.isEmpty() && password.isEmpty()) {
            session.setAttribute("errorMsg", "Enter email or password..!");
            return "redirect:/login";
        }

        if (!userServiceImpl.getUserByEmail(email)) {
            session.setAttribute("errorMsg", "Invalid Email..!");
            return "login";
        }

        User user2 = userServiceImpl.findUserByEmail(email);

        if (!user2.getPassword().equals(password)) {
            session.setAttribute("errorMsg", "Invalid Password..!");
            return "login";
        }

        if (user2.getRole().equals("Admin")) {
            session.setAttribute("errorMsg", "Invalid User..!");
            return "login";
        }

        if (!user2.getStatus().equals("Active")) {
            session.setAttribute("errorMsg", "Your Account is inActive..!");
            return "login";
        }

        session.setAttribute("userId", user2.getId());
        session.setAttribute("logedUser", user2);
        return "redirect:/user/dashboard";
    }
    
}
