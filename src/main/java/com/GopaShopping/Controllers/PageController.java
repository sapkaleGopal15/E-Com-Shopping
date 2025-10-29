package com.GopaShopping.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.GopaShopping.Entities.ContactForm;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Services.ServiceImpl.AdminServiceImpl;




@Controller
public class PageController {

    public record TeamMember(String name, String role, String photoUrl, String linkedin, String twitter, String github) {}
    public record Review(String userName, String comment) {}

    public record ContactInfo(String address, String phone, String email, String mapEmbed) {}

    @Autowired
    private AdminServiceImpl adminServiceImpl;
    
    @GetMapping("/")
    public String homepage(Model model){
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("products", adminServiceImpl.getAllProducts());
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String index(Model model){
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("products", adminServiceImpl.getAllProducts());
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
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("products", adminServiceImpl.getAllProducts());
        return "product";
    }

    @GetMapping("/product/view/{id}")
    public String productView(@PathVariable Long id, Model model) {
        model.addAttribute("products", adminServiceImpl.productFindById(id));
        return "view_product";
    }

    @GetMapping("product/view/category/{category}")
    public String productViewByCategory(@PathVariable String category, Model model){
        List<Products> products = adminServiceImpl.productsFindByCategory(category);
        model.addAttribute("product", products);
        System.out.println("Find products using category : ");
        for (Products products2 : adminServiceImpl.productsFindByCategory(category)) {
            System.out.println(products2);
        }
        return "view_product";
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
    
    
    
}
