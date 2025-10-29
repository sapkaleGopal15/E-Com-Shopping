package com.GopaShopping.Services.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.GopaShopping.Entities.Category;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Repositories.CategoryRepository;
import com.GopaShopping.Repositories.ProductsRepository;
import com.GopaShopping.Services.AdminServices;

@Service
public class AdminServiceImpl implements AdminServices{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductsRepository productsRepository;


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
    
}
