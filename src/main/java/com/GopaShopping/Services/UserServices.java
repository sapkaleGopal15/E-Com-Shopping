package com.GopaShopping.Services;

import java.util.List;

import com.GopaShopping.Entities.Cart;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.User;

public interface UserServices {

    // User
    public User savedUser(User user);
    public Boolean getUserByEmail(String email);
    public User findUserByEmail(String email);
    public User getUserById(Long id);
   // public Cart getCartUserByUserId(Long userId);

    // Product
    public Cart saveCart(Cart cart);
    public void deleteCartById(Long id);
    public List<Cart> getAllCartProductsByUserId(Long userId);
    public void updateQuantity(String sy, Long cid);
    public void deleteCartByUserId(Long id);

    // Orders
    public Orders saveOrders(Orders orders);
    public Orders findById(Long id);
    public List<Orders> ordersFindByUserId(Long userId);

}
