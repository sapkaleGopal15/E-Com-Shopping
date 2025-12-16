package com.GopaShopping.Services.ServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Repositories.CategoryRepository;
import com.GopaShopping.Repositories.OrdersRepoitory;
import com.GopaShopping.Repositories.ProductsRepository;
import com.GopaShopping.Repositories.UserRepository;
import com.GopaShopping.Services.AdminServices;

@Service
public class AdminServiceImpl implements AdminServices{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepoitory ordersRepoitory;


    // ================== Categories =======================
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category findByName(String name) {
        Category optional = categoryRepository.findByName(name);
        return optional;
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(category)) {
            categoryRepository.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category findById(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        return category;
    }

    @Override
    public List<Category> getAllActiveCategory() {
        return categoryRepository.findByIsActiveTrueOrderByIdDesc();
    }


    // ================== Products ==========================

    @Override
    public Products saveProducts(Products products) {
        return productsRepository.save(products);
    }

    @Override
    public Page<Products> getAllProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productsRepository.findAllByOrderByIdDesc(pageable);
    }

    @Override
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }


    @Override
    public Products productFindById(Long id) {
        Products products = productsRepository.findById(id).orElse(null);
        return products;
    }

    @Override
    public Boolean deleteProduct(Long id) {
        Products products = productsRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(products)) {
            productsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Page<Products> productsFindByCategory(String category, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productsRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Products> getAllActiveProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productsRepository.findByIsActiveTrueOrderByIdDesc(pageable);
    }

    @Override
    public List<Products> getAllActiveProductsAndCategory(String category) {
        return productsRepository.findByIsActiveTrueAndCategoryOrderByIdDesc(category);
    }

    @Override
    public List<Products> findBySearch(String title) {
        return productsRepository.searchActiveProducts(title);
    }

    @Override
    public List<Products> findProductBySearch(String keyword) {

        if (keyword.matches("\\d+")) {
            Long id = Long.valueOf(keyword);
            return productsRepository.findById(id)
                    .map(List::of)
                    .orElse(productsRepository.findByPrice(Double.valueOf(keyword)));
        }
        
        String status = keyword.toLowerCase();

        if (status.equals("active")) {
            return productsRepository.findByIsActive(true);
        } else if (status.equals("inactive")) {
            return productsRepository.findByIsActive(false);
        } else {
            return productsRepository.searchProductsByAll(keyword);
        }
    }

    // =========================== Orders =========================

    @Override
    public List<Orders> getOrderBySearch(String keyword) {

        if (keyword.matches("\\d+")) {
            Long orderId = Long.valueOf(keyword);
            return ordersRepoitory.findByIdOrderByIdDesc(orderId);
        }

        if (keyword.matches("\\d{2}[-]\\d{2}[-]\\d{4}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(keyword, formatter);
            return ordersRepoitory.findByOrderDate(date);
        }

        if (keyword.matches("\\d{2}[/]\\d{2}[/]\\d{4}")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(keyword, formatter);
            return ordersRepoitory.findByOrderDate(date);
        }

        return ordersRepoitory.findByStatusOrderByIdDesc(keyword);
    }

    @Override
    public Page<Orders> getAllOrders(int page, int size) {
        var pageable = PageRequest.of(page, size);
        return ordersRepoitory.findAllByOrderByIdDesc(pageable);
    }

    @Override
    public List<Orders> getAllOrdersBy() {
        return ordersRepoitory.findAll();
    }

    @Override
    public Orders getOneOrderById(Long id) {
        return ordersRepoitory.getById(id);
    }

    @Override
    public void saveOrders(Orders orders) {
        ordersRepoitory.save(orders);
    }

    // ========================== Users =======================

    @Override
    public List<User> getAllUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (ObjectUtils.isEmpty(user)) 
            return false;
        else
            return true;
    }

    @Override
    public User savedUser(User user) {
        return userRepository.save(user);
    }
}
