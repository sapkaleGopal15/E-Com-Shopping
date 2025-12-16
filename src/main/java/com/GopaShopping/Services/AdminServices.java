package com.GopaShopping.Services;

import java.util.List;

import org.springframework.data.domain.Page;

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
    public Page<Products> getAllProducts(int page, int size);
    public Products productFindById(Long id);
    public Boolean deleteProduct(Long id);
    public Page<Products> productsFindByCategory(String category, int page, int size);
    public Page<Products> getAllActiveProducts(int page, int size);
    public List<Products> getAllActiveProductsAndCategory(String category);
    public List<Products> findBySearch(String title);
    public List<Products> findProductBySearch(String search);
    public List<Products> getAllProducts();

    // Orders
    public Page<Orders> getAllOrders(int page, int size);
    public void saveOrders(Orders orders);
    public Orders getOneOrderById(Long id);
    public List<Orders> getAllOrdersBy();
    public List<Orders> getOrderBySearch(String keyword);

    // Users
    public List<User> getAllUsersByRole(String role);
    public User getUserById(Long id);
    public User savedUser(User user);
    public Boolean getUserByEmail(String email);
}
