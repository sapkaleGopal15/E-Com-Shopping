package com.GopaShopping.Services.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.GopaShopping.Entities.Cart;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Repositories.CartRepository;
import com.GopaShopping.Repositories.OrdersRepoitory;
import com.GopaShopping.Repositories.UserRepository;
import com.GopaShopping.Services.UserServices;

@Service
public class UserServiceImpl implements UserServices {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepoitory ordersRepoitory;


    // =============================== User ===========================

    @Override
    public Boolean getUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if (!ObjectUtils.isEmpty(user)) {
            return true;
        }
        return false;
    }

    @Override
    public User savedUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // @Override
    // public Cart getCartUserByUserId(Long userId) {
    //     return cartRepository.findByuserId(userId);
    // }

    // =========================== Product ====================
    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteCartById(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public void deleteCartByUserId(Long id) {
        cartRepository.deleteByUserId(id);
    }

    @Override 
    public List<Cart> getAllCartProductsByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
    
    @Override
	public void updateQuantity(String sy, Long cid) {

		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;

		if (sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
			} else {
				cart.setQuantity(updateQuantity);
                cart.setTotalPrice(cart.getTotalPrice() - cart.getPrice());
				cartRepository.save(cart);
			}

		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
            cart.setTotalPrice(cart.getTotalPrice() + cart.getPrice());
			cartRepository.save(cart);
		}

	}


    // ============================ Orders =================================

    @Override
    public Orders saveOrders(Orders orders) {
        return ordersRepoitory.save(orders);
    }

    @Override
    public Orders findById(Long id) {
        return ordersRepoitory.findById(id).orElse(null);
    }

    @Override
    public List<Orders> ordersFindByUserId(Long userId) {
        return ordersRepoitory.findByUserId(userId);
    }
    
    
}
