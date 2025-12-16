package com.GopaShopping.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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

import com.GopaShopping.Entities.ContactMessage;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Form.ContactForm;
import com.GopaShopping.Form.UserForm;
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
        List<Products> products = userServiceImpl.findLatestProducts();
        List<Long> latest = new ArrayList<>();
        List<Products> latestProducts = new ArrayList<>();
        int temp = 0;
        for (Products products2 : products) {
            latest.add(products2.getId());
        }
        Collections.reverse(latest);
        for (Long id : latest) {
            latestProducts.add(userServiceImpl.findProductById(id));
            temp = temp + 1;
            if (temp == 8) {
                break;
            }
        }

        model.addAttribute("products", latestProducts);
        model.addAttribute("paramValue", "home");
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String index(Model model){

        List<Products> products = userServiceImpl.findLatestProducts();
        
        List<Long> latest = new ArrayList<>();
        List<Products> latestProducts = new ArrayList<>();
        int temp = 0;
        for (Products products2 : products) {
            latest.add(products2.getId());
        }
        
        for (Long id : latest) {
            latestProducts.add(userServiceImpl.findProductById(id));
            temp = temp + 1;
            if (temp == 8) {
                break;
            }
        }

        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", latestProducts);
        model.addAttribute("paramValue", "home");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
             
        model.addAttribute("stats", Map.of(
            "users", "1M+",
            "orders", "5M+",
            "products", "10K+",
            "reviews", "50K+"
        ));

        
        model.addAttribute("team", List.of(
            new TeamMember("Gopal Sapkale", "CEO", "https://avatars.githubusercontent.com/u/190483931?v=4", "https://www.linkedin.com/in/gopal-sapkale-8bb13b354/", "https://www.instagram.com/ip_sapkale_12/", "https://github.com/sapkaleGopal15"),
            new TeamMember("Sudarshan Sapkale", "CTO", "/images/profile_img/sudarshan Image.jpg", "https://www.linkedin.com/in/gopal-sapkale-8bb13b354/", "https://www.instagram.com/ip_sapkale_12/", "https://github.com/sapkaleGopal15"),
            new TeamMember("Virat Kohli", "Lead Developer", "https://documents.iplt20.com/ipl/IPLHeadshot2025/2.png", "https://www.linkedin.com/in/gopal-sapkale-8bb13b354/", "https://www.instagram.com/ip_sapkale_12/", "https://github.com/sapkaleGopal15"),
            new TeamMember("Rohit Sharma", "Marketing Head", "https://i.pinimg.com/736x/7f/e9/bb/7fe9bb1ae516fc8614dae136006773e1.jpg", "https://www.linkedin.com/in/gopal-sapkale-8bb13b354/", "https://www.instagram.com/ip_sapkale_12/", "https://github.com/sapkaleGopal15")
        ));

        
        model.addAttribute("reviews", List.of(
            new Review("Gopal Sapkale", "Excellent service and great quality!"),
            new Review("Sudarshan Sapkale", "Fast delivery and responsive support!"),
            new Review("Paras Thakare", "Loved the products. Highly recommend ShopEase!")
        ));
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("paramValue", "about");
        return "about";
    }
    
    @GetMapping("/product")
    public String product(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<Products> listProduct = adminServiceImpl.getAllActiveProducts(page, size);

        int totalPages = listProduct.getTotalPages();
        int currentPage = listProduct.getNumber();

        int startPage = Math.max(0, currentPage - 2);
        int endPage = Math.min(totalPages - 1, currentPage + 2);
        
        if (currentPage == 0) {
            startPage = 0;
            endPage = Math.min(4, totalPages - 1);
        }

        if (currentPage >= totalPages - 3) {
            startPage = Math.max(0, totalPages - 5);
            endPage = totalPages - 1;
        }
        
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageProducts", listProduct);
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("paramValue", "product");
        model.addAttribute("categoryName", "products");
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

        List<Products> listProduct = adminServiceImpl.getAllActiveProductsAndCategory(category);

        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("pageProduct", listProduct);
        model.addAttribute("paramValue", category);
        model.addAttribute("categoryName", "categoryName");
        return "product";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contactInfo", new ContactInfo(
                "12 Valmik Nagar, Chopda, Jalgaon, Maharashtra, India",
                "+91 7498050364",
                "sapkalegopal15@gmail.com",
                "https://www.google.com/maps/embed?pb=!1m18..."
        ));

        ContactForm contactForm = new ContactForm();

        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("paramValue", "contact");
        return "contact";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginType", "user");
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        return "login";
    }

    @GetMapping("/admin-login")
    public String adminLogin(Model model) {
        model.addAttribute("loginType", "admin");
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        return "admin/login";
    }

    @GetMapping("/forget-password")
    public String forgetPassword(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        return "forget-password";
    }


    // ===================== Processing ====================

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userForm") UserForm userForm, Model model, BindingResult result, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
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
            return "redirect:/home";
        }else{
            session.setAttribute("errorMsg", "Somathing went wrong..!");
            return "redirect:/register";
        }
    }

    @PostMapping("/dologin")
    public String doLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        if (email.isEmpty() && password.isEmpty()) {
            session.setAttribute("errorMsg", "Enter email or password..!");
            model.addAttribute("loginType", "user");
            return "login";
        }

        if (!userServiceImpl.getUserByEmail(email)) {
            session.setAttribute("errorMsg", "Invalid Email..!");
            model.addAttribute("loginType", "user");
            return "login";
        }

        User user2 = userServiceImpl.findUserByEmail(email);

        if (!user2.getPassword().equals(password)) {
            session.setAttribute("errorMsg", "Invalid Password..!");
            model.addAttribute("loginType", "user");
            return "login";
        }

        if (user2.getRole().equals("Admin")) {
            session.setAttribute("errorMsg", "Invalid User..!");
            model.addAttribute("loginType", "user");
            return "login";
        }

        if (!user2.getStatus().equals("Active")) {
            session.setAttribute("errorMsg", "Your Account is inActive..!");
            model.addAttribute("loginType", "user");
            return "login";
        }

        session.setAttribute("userId", user2.getId());
        session.setAttribute("logedUser", user2);
        return "redirect:/user/dashboard";
    }


    @PostMapping("/admin-dologin")
    public String adminDoLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        if (email.isEmpty() && password.isEmpty()) {
            session.setAttribute("errorMsg", "Enter email or password..!");
            model.addAttribute("loginType", "admin");
            return "admin/login";
        }

        if (!userServiceImpl.getUserByEmail(email)) {
            session.setAttribute("errorMsg", "Invalid Email..!");
            model.addAttribute("loginType", "admin");
            return "admin/login";
        }

        User user2 = userServiceImpl.findUserByEmail(email);

        if (!user2.getPassword().equals(password)) {
            session.setAttribute("errorMsg", "Invalid Password..!");
            model.addAttribute("loginType", "admin");
            return "admin/login";
        }

        if (user2.getRole().equals("Guest")) {
            session.setAttribute("errorMsg", "Invalid Admin..!");
            model.addAttribute("loginType", "admin");
            return "admin/login";
        }

        if (!user2.getStatus().equals("Active")) {
            session.setAttribute("errorMsg", "Your Account is inActive..!");
            model.addAttribute("loginType", "admin");
            return "admin/login";
        }

        session.setAttribute("userId", user2.getId());
        session.setAttribute("logedUser", user2);
        return "redirect:/admin/";
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestParam String email, @RequestParam String contact, Model model, HttpSession session) {
        
        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());

        if (email.isEmpty() || email == null) {
            session.setAttribute("errorMsg", "Please enter Email..!");
            return "forget-password";
        }
        if (contact.isBlank() || contact == null) {
            session.setAttribute("errorMsg", "Please enter Mobile number..!");
            return "forget-password";
        }
        User user = userServiceImpl.findUserByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            session.setAttribute("errorMsg", "Invalid user..!");
            return "forget-password";
        }
        if (!user.getContact().equals(contact)) {
            session.setAttribute("errorMsg", "Invalid Mobile number..!");
            return "forget-password";
        }
        session.setAttribute("successMsg", "Your Password : "+user.getPassword());
        return "forget-password";
    }


    @PostMapping("/product-search")
    public String productSearch(Model model, @RequestParam String search) {

        if (search.isEmpty()) {
            return "redirect:/product";
        }

        List<Products> listProduct =  adminServiceImpl.findBySearch(search);

        if (listProduct.isEmpty()) {
            model.addAttribute("productNotFound", "productNotFound");
        }

        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("pageProduct", listProduct);
        model.addAttribute("categoryName", "search");
        model.addAttribute("paramValue", "product");
        return "product";
    }

    @PostMapping("/contact/submit")
    public String contactSubmit(@Valid @ModelAttribute("contactForm") ContactForm contactForm, BindingResult result, Model model, HttpSession session) {

        model.addAttribute("contactInfo", new ContactInfo(
            "12 Valmik Nagar, Chopda, Jalgaon, Maharashtra, India",
            "+91 7498050364",
            "sapkalegopal15@gmail.com",
            "https://www.google.com/maps/embed?pb=!1m18..."
        ));

        if (result.hasErrors()) {
            return "contact";
        }

        ContactMessage contactMessage = ContactMessage.builder()
                                .name(contactForm.getName())
                                .email(contactForm.getEmail())
                                .subject(contactForm.getSubject())
                                .message(contactForm.getMessage())
                                .build();

        ContactMessage contactMessage2 = userServiceImpl.saveMessage(contactMessage);

        if (ObjectUtils.isEmpty(contactMessage2)) {
            session.setAttribute("errorMsg", "Message not save ! something went wrong");
        } else {
            session.setAttribute("successMsg", "Message save successful...");
        }
        return "redirect:/contact";
    }
}
