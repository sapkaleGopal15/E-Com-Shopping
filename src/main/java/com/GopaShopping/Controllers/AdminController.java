package com.GopaShopping.Controllers;

import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Form.UpdateUserPassword;
import com.GopaShopping.Form.UserForm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.GopaShopping.Services.ServiceImpl.AdminServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminServiceImpl;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/dashboard";
    }

    @GetMapping("/add-admin")
    public String add_admin(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("userForm", userForm);
        return "admin/add_admin";
    }
    
    @GetMapping("/add-product")
    public String add_product(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/add_product";
    }

    @GetMapping("/view-products")
    public String view_products(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Page<Products> listProduct = adminServiceImpl.getAllProducts(page, size);

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

        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("products", listProduct);
        model.addAttribute("allProduct", "allProduct");

        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/view_products";
    }

    @GetMapping("/orders")
    public String orders(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        
        Page<Orders> listOrders = adminServiceImpl.getAllOrders(page, size);

        int totalPages = listOrders.getTotalPages();
        int currentPage = listOrders.getNumber();

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
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("orders", listOrders);
        return "admin/orders";
    }

    @GetMapping("/edit-product/{id}")
    public String edit_product(@PathVariable Long id, Model model) {
        model.addAttribute("product", adminServiceImpl.productFindById(id));
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/edit_product";
    }

    @GetMapping("/delete-product/{id}")
    public String delete_product(@PathVariable Long id, HttpSession session){
        Boolean products = adminServiceImpl.deleteProduct(id);
        if (products) {
            session.setAttribute("successMsg", "Product deleted successfully...");
        }else{
            session.setAttribute("errorMsg", "Product Not Delete ! Something went wrong..!");
        }
        return "redirect:/admin/view-products";
    }
    
    @GetMapping("/add-category")
    public String add_category(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/add_category";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminServiceImpl.getAllUsersByRole("Guest"));
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/users";
    }

    @GetMapping("/edit-product")
    public String edit_product(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/edit_product";
    }

    @GetMapping("/update-category/{id}")
    public String edit_category(@PathVariable Long id, Model model) {
        Category category = adminServiceImpl.findById(id);
        model.addAttribute("category", category);
        return "admin/edit_category";
    }

    @GetMapping("/delete-category/{id}")
    public String delete_category(@PathVariable Long id, HttpSession session) {
        Boolean deleteCategory = adminServiceImpl.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("successMsg", "Category Deleted Successfully...");
        }else{
            session.setAttribute("errorMsg", "Category can not be deleted..!");
        }
        return "redirect:/admin/add-category";
    }
    

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long user = (Long) session.getAttribute("userId");
        model.addAttribute("logedUser", adminServiceImpl.getUserById(user));
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/profile";
    }

    @GetMapping("/review-admin")
    public String reviewAdmin(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/review_admin";
    }
    

    // ===================== Processing ======================
    // =======================================================

    @PostMapping("/add-category")
    public String addCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        
        String name = category.getName();
        Category existCategory = adminServiceImpl.findByName(name);
        String fileName = file != null ? file.getOriginalFilename() : "default.jpg" ;
        category.setImageName(fileName);

        if (existCategory == null) {
            Category savedCategory = adminServiceImpl.saveCategory(category);
            if (ObjectUtils.isEmpty(savedCategory)) {
                session.setAttribute("errorMsg", "Not Saved ! Internal server error");
            }else{
                File savedFile = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("successMsg", "Category saved Successfully...");
            }
        }else{
            session.setAttribute("errorMsg", "Category name already exist");
        }
        return "redirect:/admin/add-category";
    }

    @PostMapping("/update-category")
    public String update_category(@ModelAttribute Category category, HttpSession session, @RequestParam("file") MultipartFile file, Model model ) throws IOException {
    
        model.addAttribute("category", adminServiceImpl.getAllCategory());

        Category oldCategory = adminServiceImpl.findById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();


        if (!ObjectUtils.isEmpty(category)) {

            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }

        Category updateCategory = adminServiceImpl.saveCategory(oldCategory);

        if (!ObjectUtils.isEmpty(updateCategory)) {

            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("successMsg", "Category update successfully...");
            return "redirect:/admin/add-category";
        } else {
            session.setAttribute("errorMsg", "something wrong on server..!");
            return "redirect:/admin/update-category";
        }

    }


    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Products products, Model model, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        if (products.getTitle().length() <= 100) {
            if (products.getDescription().length() <= 500) {
                if (products.getPrice() > 0) {
                    if (products.getStock() >= 0) {
                        if (!file.isEmpty()){
                            String fileName = file.getOriginalFilename();
                            products.setImageName(fileName);
                            products.setDiscount(0.0);
                            products.setDiscountPrice(products.getPrice());
                            Products products2 = adminServiceImpl.saveProducts(products);
                            if (ObjectUtils.isEmpty(products2)) {
                                session.setAttribute("errorMsg", "Product Not Saved ! Internal server error");
                            }else{
                                File savedFile = new ClassPathResource("static/images").getFile();
                                Path path = Paths.get(savedFile.getAbsolutePath() + File.separator + "products" + File.separator + file.getOriginalFilename());
                                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                                session.setAttribute("successMsg", "Product saved Successfully...");
                            }
                        }else{
                            session.setAttribute("errorMsg", "Please select product image");
                        }
                    }else{
                        session.setAttribute("errorMsg", "Product stock level less than 0");
                    }
                }else{
                    session.setAttribute("errorMsg", "Product price can not less than 0");
                }
            }else{
                session.setAttribute("errorMsg", "Product Description less than 500 character");
            }
        }else{
            session.setAttribute("errorMsg", "Product title less than 100 character");
        }

        return "redirect:/admin/add-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Products products, Model model, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        Products oldProduct = adminServiceImpl.productFindById(products.getId());

        oldProduct.setTitle(products.getTitle().isEmpty() ? oldProduct.getTitle() : products.getTitle());
        oldProduct.setDescription(products.getDescription().isEmpty() ? oldProduct.getDescription() : products.getDescription());
        oldProduct.setPrice(products.getPrice() == null ? oldProduct.getPrice() : products.getPrice());
        oldProduct.setCategory(products.getCategory().isEmpty() ? oldProduct.getCategory() : products.getCategory());
        oldProduct.setIsActive(products.getIsActive() == null ? oldProduct.getIsActive() : products.getIsActive());
        oldProduct.setStock(products.getStock() >= 0.0 ? products.getStock() : oldProduct.getStock());
        oldProduct.setDiscount(products.getDiscount() == null ? oldProduct.getDiscount() : products.getDiscount());
        oldProduct.setDiscountPrice(products.getDiscountPrice() == null ? oldProduct.getDiscountPrice() : products.getDiscountPrice());

        if (oldProduct.getDiscount() > 0.0) {
            if (oldProduct.getDiscount() >= 101.0) {
                session.setAttribute("errorMsg", "Invalid Discount");
                return "redirect:/admin/edit-product/" + products.getId();
            }
            Double setDiscountPrice = (oldProduct.getPrice() - (oldProduct.getPrice() * (oldProduct.getDiscount() / 100)));
            oldProduct.setDiscountPrice(setDiscountPrice);
        }else{
            oldProduct.setDiscountPrice(products.getPrice());
        }

        String fileName = file.isEmpty() ? oldProduct.getImageName() : file.getOriginalFilename();
        oldProduct.setImageName(fileName);
        System.out.println("new file address is : "+fileName);

        Products products2 = adminServiceImpl.saveProducts(oldProduct);

        if (ObjectUtils.isEmpty(products2)) {
            session.setAttribute("errorMsg", "Product Not updated ! Internal server error");
            return "redirect:/admin/edit-product/" + products.getId();
        }else{
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "products" + File.separator
                        + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("successMsg", "Product Update Successfully...");
            return "redirect:/admin/view-products";
        }
    }
    
    @PostMapping("/update-status/{id}/{a}")
    public String updateStatus(@PathVariable Long id, @PathVariable int a, HttpSession session) {
        User user = adminServiceImpl.getUserById(id);
        if (a == 0) {
            user.setStatus("inActive");
        }else{
            user.setStatus("Active");
        }
        adminServiceImpl.savedUser(user);
        session.setAttribute("successMsg", "User status update successful...");
        return "redirect:/admin/users";
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result, @RequestParam("file") MultipartFile file, HttpSession session, Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        if (result.hasErrors()) {
            return "admin/add_admin";
        }

        Boolean existEmail = adminServiceImpl.getUserByEmail(userForm.getEmail());

        if (existEmail) {
            session.setAttribute("errorMsg", "Email is already existed..!");
            return "redirect:/admin/add-admin";
        }

        if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
            session.setAttribute("errorMsg", "Please enter same confirm password..!");
            return "redirect:/admin/add-admin";
        }

        String profileImage = file.isEmpty() || file == null ? "default_profile_image.png" : file.getOriginalFilename();

        User user = User.builder()
            .name(userForm.getName())
            .contact(userForm.getContact())
            .email(userForm.getEmail())
            .address(userForm.getAddress())
            .city(userForm.getCity())
            .state(userForm.getState())
            .pincode(userForm.getPincode())
            .password(userForm.getPassword())
            .profileImage(profileImage)
            .role("Admin")
            .status("Active")
            .build();

        adminServiceImpl.savedUser(user);
        session.setAttribute("successMsg", "Admin Register successful...");
        return "redirect:/admin";
    }


    @PostMapping("/update-profile/{id}")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User user, @RequestParam("file") MultipartFile file, HttpSession session, Model model) throws IOException {
        
        User user2 = adminServiceImpl.getUserById(id);
        String image = file == null || file.isEmpty() ? user2.getProfileImage() : file.getOriginalFilename();
        
        user.setProfileImage(image);
        user.setId(id);
        user.setEmail(user2.getEmail());
        user.setPassword(user2.getPassword());
        user.setRole(user2.getRole());
        user.setStatus(user2.getStatus());

        adminServiceImpl.savedUser(user);

        if (!file.isEmpty()) {
            File saveFile = new ClassPathResource("static/images").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                    + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        }
        session.setAttribute("successMsg", "Profile update successful...");
        return "redirect:/admin/profile";
    }


    @PostMapping("/update-password/{id}") 
    public String updatePassword(@PathVariable Long id, @ModelAttribute UpdateUserPassword updatePassword, HttpSession session) {
        
        User user = adminServiceImpl.getUserById(id);

        if (!user.getPassword().equals(updatePassword.getCurrentPassword())) {
            session.setAttribute("errorMsg", "Wrong Current Password..!");
            return "redirect:/admin/profile";
        }

        if (!updatePassword.getNewPassword().equals(updatePassword.getConfirmPassword())) {
            session.setAttribute("errorMsg", "Wrong Confirm Password..!");
            return "redirect:/admin/profile";
        }

        user.setPassword(updatePassword.getNewPassword());
        adminServiceImpl.savedUser(user);
        session.setAttribute("successMsg", "Password update successful...");
        return "redirect:/admin/profile";
    
    }
    

    @PostMapping("/update-order-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @ModelAttribute Orders orders, HttpSession session, Model model) {
        Orders orders2 = adminServiceImpl.getOneOrderById(id);
        orders2.setStatus(orders.getStatus());
        orders2.setCancelReason("Delivered Successfully...");
        adminServiceImpl.saveOrders(orders2);
        session.setAttribute("successMsg", "Order status update successful...");
        return "redirect:/admin/orders";
    }
    
    @PostMapping("/search-product")
    public String searchProduct(@RequestParam String search, Model model) {

        if (search.isEmpty()) {
            return "redirect:/admin/view-products";
        }

        List<Products> products = adminServiceImpl.findProductBySearch(search);

        if (products.isEmpty()) {
            model.addAttribute("productNotFound", "productNotFound");
        }
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        model.addAttribute("searchProducts", products);
        return "admin/view_products";
    }

    @PostMapping("/search-order")
    public String searchOrder(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        if (keyword.isEmpty()) {
            return "redirect:/admin/orders";
        }

        System.out.println("Order Keyword : "+keyword);

        List<Orders> orders = adminServiceImpl.getOrderBySearch(keyword);

        if (ObjectUtils.isEmpty(orders)) {
            model.addAttribute("orderNotFound", "orderNotFound");
            model.addAttribute("search", "search");
            return "admin/orders";
        } else {
            model.addAttribute("orders", orders);
            model.addAttribute("search", "search"); 
            return "admin/orders";
        }
    }

}
