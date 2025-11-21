package com.GopaShopping.Services;

import java.util.List;


import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;

public interface AdminServices {

    // Categories
    public Category saveCategory(Category category);
    public Category findByName(String name);
    public List<Category> getAllCategory();
    public Boolean deleteCategory(Long id);
    public Category findById(Long id);
    public List<Category> getAllActiveCategory();


    // Products
    public Products saveProducts(Products products);
    public List<Products> getAllProducts();
    public Products productFindById(Long id);
    public Boolean deleteProduct(Long id);
    public List<Products> productsFindByCategory(String category);
    public List<Products> getAllActiveProducts();
    public List<Products> getAllActiveProductsAndCategory(String category);

    // Orders
    public List<Orders> getAllOrders();

    // Users
    public List<User> getAllUsersByRole(String role);
    public User getUserById(Long id);
    public User savedUser(User user);
}
