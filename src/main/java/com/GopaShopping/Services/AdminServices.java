package com.GopaShopping.Services;

import java.util.List;


import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Products;

public interface AdminServices {

    // Categories
    public Category saveCategory(Category category);
    public Category findByName(String name);
    public List<Category> getAllCategory();
    public Boolean deleteCategory(Long id);
    public Category findById(Long id);


    // Products
    public Products saveProducts(Products products);
    public List<Products> getAllProducts();
    public Products productFindById(Long id);
    public Boolean deleteProduct(Long id);
    public List<Products> productsFindByCategory(String category);
}
