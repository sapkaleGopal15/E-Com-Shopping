package com.GopaShopping.Services.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        return categoryRepository.findAll();
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
        return categoryRepository.findByIsActiveTrue();
    }


    // ================== Products ==========================

    @Override
    public Products saveProducts(Products products) {
        return productsRepository.save(products);
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
    public List<Products> productsFindByCategory(String category) {
        return productsRepository.findByCategory(category);
    }

    @Override
    public List<Products> getAllActiveProducts() {
        return productsRepository.findByIsActiveTrue();
    }

    @Override
    public List<Products> getAllActiveProductsAndCategory(String category) {
        return productsRepository.findByIsActiveTrueAndCategory(category);
    }


    // =========================== Orders =========================

    @Override
    public List<Orders> getAllOrders() {
        return ordersRepoitory.findAll();
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
    public User savedUser(User user) {
        return userRepository.save(user);
    }
}
