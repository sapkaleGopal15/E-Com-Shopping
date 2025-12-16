package com.GopaShopping.Services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.GopaShopping.Entities.Cart;
import com.GopaShopping.Entities.ContactMessage;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;

public interface UserServices {

    // User
    public User savedUser(User user);
    public Boolean getUserByEmail(String email);
    public User findUserByEmail(String email);
    public User getUserById(Long id);

    // Product
    public Cart saveCart(Cart cart);
    public void deleteCartById(Long id);
    public List<Cart> getAllCartProductsByUserId(Long userId);
    public void updateQuantity(String sy, Long cid);
    public void deleteCartByUserId(Long id);
    public Page<Products> findByIsActiveTrueAndTitle(String title, int page, int size);
    public List<Products> findLatestProducts();
    public Products findProductById(Long id);
    public void deleteCartProduct(String sy, Long cid);
    

    // Orders
    public Orders saveOrders(Orders orders);
    public Orders findById(Long id);
    public List<Orders> ordersFindByUserId(Long userId);


    // Contact Message
    public ContactMessage saveMessage(ContactMessage message);

}
