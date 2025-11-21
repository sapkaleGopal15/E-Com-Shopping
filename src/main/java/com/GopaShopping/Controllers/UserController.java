package com.GopaShopping.Controllers;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.GopaShopping.Entities.Cart;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Form.LoginUserForm;
import com.GopaShopping.Form.UpdateUserPassword;
import com.GopaShopping.Form.UserForm;
import com.GopaShopping.Services.ServiceImpl.AdminServiceImpl;
import com.GopaShopping.Services.ServiceImpl.UserServiceImpl;

import jakarta.servlet.http.HttpSession;





@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

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


    
    
    


    @GetMapping("/add")
    public String addUser(Model model){
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "addUser";
    }

    






// =========================== User Controller ============================== 

    @GetMapping("/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    @GetMapping("/cart")
    public String userCart(Model model, HttpSession session) {
        Long existId = (Long) session.getAttribute("userId");
        List<Cart> allProduct = userServiceImpl.getAllCartProductsByUserId(existId);
        Double orderPrice = 0.0;
        for (Cart cart : allProduct) {
            orderPrice = orderPrice + cart.getTotalPrice();
            cart.setTotalOrderPrice(orderPrice);
            userServiceImpl.saveCart(cart);
        }
        System.out.println("Total Order Price by Order : " +orderPrice);
        model.addAttribute("products", allProduct);
        model.addAttribute("totalOrderPrice", orderPrice);
        return "user/cart";
    }

    @GetMapping("/add-to-cart/{id}")
    public String userCartAddProduct(@PathVariable Long id, Model model, HttpSession session) {
       
        Cart cart = new Cart();
        Products products = adminServiceImpl.productFindById(id);

        List<Cart> lisCarts = userServiceImpl.getAllCartProductsByUserId((Long) session.getAttribute("userId"));

        for (Cart cart2 : lisCarts) {
            if (cart2.getProductId() == id) {
                model.addAttribute("products", products);
                session.setAttribute("errorMsg", "Product already added to cart..!");
                return "view_product";
            }
        }

        // if (cart.getTotalOrderPrice() == null) {
        //     cart.setTotalOrderPrice(0.0);
        // }

        cart.setName(products.getTitle());
        cart.setImage(products.getImageName());
        cart.setPrice(products.getDiscountPrice());
        cart.setQuantity(1);
        cart.setTotalPrice(cart.getPrice() * cart.getQuantity());
        cart.setTotalOrderPrice(0.0);
        cart.setProductId(id);
        cart.setUserId((Long) session.getAttribute("userId"));

        // orderPrice = cart.getTotalOrderPrice();

        Cart saveCart = userServiceImpl.saveCart(cart);

        model.addAttribute("category", adminServiceImpl.getAllActiveCategory());
        model.addAttribute("products", adminServiceImpl.getAllActiveProducts());
        session.setAttribute("successMsg", "Product added to cart...");
        return "redirect:/product";
    }

    @GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Long cid) {
		userServiceImpl.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}
    
    @GetMapping("/orders")
    public String userOrders(HttpSession session, Model model) {
        Long existUser = (Long) session.getAttribute("userId");
        List<Orders> orders = userServiceImpl.ordersFindByUserId(existUser);
        // List<List<String>> bigList = new ArrayList<>();
        // for (Orders orders2 : orders) {

            
        //     List<Long> product =  orders2.getProductId();
        //     List<Products> products = new ArrayList<>();
        //     List<String> prodName = new ArrayList<>();
        //     for (Long element : product) {
        //         products.add(adminServiceImpl.productFindById(element));
        //     }
        //     for (Products string : products) {
        //         prodName.add(string.getTitle());
        //     }
        //     bigList.add(prodName);
        // }

        

    

        model.addAttribute("orders", orders);
       //model.addAttribute("productsName", bigList);
        return "user/my_orders";
    }

    @GetMapping("/order")
    public String userOrder(Model model, HttpSession session) {
        Long existUserId = (Long) session.getAttribute("userId");
        User existUser = userServiceImpl.getUserById(existUserId);
        List<Cart> carts = userServiceImpl.getAllCartProductsByUserId(existUserId);
        Double orderPrice = 0.0;
        for (Cart cart : carts) {
            orderPrice = cart.getTotalOrderPrice();
        }
        Double userPayPrice = orderPrice + 50 + 20;
        System.out.println("After order total Price " + userPayPrice);
        model.addAttribute("orderPrice", orderPrice);
        model.addAttribute("user", existUser);
        model.addAttribute("userPayPrice", userPayPrice);
        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute Orders orders, HttpSession session, Model model) {

        Long existUserId = (Long) session.getAttribute("userId");
        List<Cart> carts = userServiceImpl.getAllCartProductsByUserId(existUserId);
        Double orderPrice = 0.0;
        List<Long> prodId = new ArrayList<>();
        List<String> productNames = new ArrayList<>();
        for (Cart cart : carts) {
            orderPrice = cart.getTotalOrderPrice();
            prodId.add(cart.getProductId());
            productNames.add(cart.getName());
        }
        orders.setUserId(existUserId);
        orders.setOrderPrice(orderPrice + 50 + 20);
        orders.setStatus("Delivered");
        orders.setProductId(prodId);
        orders.setProductNames(productNames);

        Orders orders2 = userServiceImpl.saveOrders(orders);

        if (orders2.getPaymentMode().equals("ONLINE")) {
            model.addAttribute("orderPrice", orders2.getOrderPrice());
            return "user/onlinePayment";
        }
        userServiceImpl.deleteCartByUserId(existUserId);
        return "user/success";
    }

    @GetMapping("/onlinePayment/{id}")
    public String onlinePayment(@PathVariable Long id, HttpSession session, Model model) {
        Orders orders = userServiceImpl.findById(id);
        model.addAttribute("orderPrice", orders.getOrderPrice());
        return "user/success";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("userId");
        model.addAttribute("logedUser", userServiceImpl.getUserById(id));
        return "user/profile";
    }

    @PostMapping("/update-profile/{id}")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session, Model model) throws IOException {
        
        User user2 = userServiceImpl.getUserById(id);
        String image = file == null || file.isEmpty() ? user2.getProfileImage() : file.getOriginalFilename();
        
        user.setProfileImage(image);
        user.setId(id);
        user.setEmail(user2.getEmail());
        user.setPassword(user2.getPassword());
        user.setRole(user2.getRole());
        user.setStatus(user2.getStatus());

        userServiceImpl.savedUser(user);

        if (!file.isEmpty()) {
            File saveFile = new ClassPathResource("static/images").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                    + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
        session.setAttribute("successMsg", "Profile update successful...");
        return "redirect:/user/profile";
    }

    @PostMapping("/update-password/{id}") 
    public String updatePassword(@PathVariable Long id, @ModelAttribute UpdateUserPassword updatePassword, HttpSession session) {
        User user = userServiceImpl.getUserById(id);

        if (!user.getPassword().equals(updatePassword.getCurrentPassword())) {
            session.setAttribute("errorMsg", "Wrong Current Password..!");
            return "redirect:/user/profile";
        }

        if (!updatePassword.getNewPassword().equals(updatePassword.getConfirmPassword())) {
            session.setAttribute("errorMsg", "Wrong Confirm Password..!");
            return "redirect:/user/profile";
        }
        user.setPassword(updatePassword.getNewPassword());
        userServiceImpl.savedUser(user);
        session.setAttribute("successMsg", "Password update successful...");
        return "redirect:/user/profile";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "user/success";
    }

    @GetMapping("/logout")
    public String userLogout(HttpSession session) {
        session.invalidate();
        return "home";
    }



}
