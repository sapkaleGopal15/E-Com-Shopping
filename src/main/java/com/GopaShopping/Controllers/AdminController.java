package com.GopaShopping.Controllers;

import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Products;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.GopaShopping.Services.ServiceImpl.AdminServiceImpl;

import jakarta.servlet.http.HttpSession;
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
    public String index() {
        return "admin/dashboard";
    }

    @GetMapping("/add-admin")
    public String add_admin() {
        return "admin/add_admin";
    }
    
    @GetMapping("/add-product")
    public String add_product(Model model) {
        model.addAttribute("category", adminServiceImpl.getAllCategory());
        return "admin/add_product";
    }

    @GetMapping("/view-products")
    public String view_products(Model model) {
        model.addAttribute("products", adminServiceImpl.getAllProducts());
        return "admin/view_products";
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

    @GetMapping("/orders")
    public String orders() {
        return "admin/orders";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/edit-product")
    public String edit_product() {
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
    public String profile() {
        return "admin/profile";
    }

    // ===================== Processing ======================

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
    public String update_category(@ModelAttribute Category category, HttpSession session, @RequestParam("file") MultipartFile file ) throws IOException {
    
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
    public String saveProduct(@ModelAttribute Products products, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{
        
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
    public String updateProduct(@ModelAttribute Products products, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{

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
    
}
