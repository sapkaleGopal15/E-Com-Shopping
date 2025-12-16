package com.GopaShopping.Services.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.GopaShopping.Entities.Cart;
import com.GopaShopping.Entities.ContactMessage;
import com.GopaShopping.Entities.Orders;
import com.GopaShopping.Entities.Products;
import com.GopaShopping.Entities.User;
import com.GopaShopping.Repositories.CartRepository;
import com.GopaShopping.Repositories.ContactMessageRepository;
import com.GopaShopping.Repositories.OrdersRepoitory;
import com.GopaShopping.Repositories.ProductsRepository;
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

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;


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
        return cartRepository.findByUserIdOrderByIdDesc(userId);
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

    @Override
    public void deleteCartProduct(String sy, Long cid) {
        Cart cart = cartRepository.findById(cid).get();
        cartRepository.delete(cart);
    }

    @Override
    public Products findProductById(Long id) {
        return productsRepository.findByIsActiveTrueAndId(id);
    }

    @Override
    public List<Products> findLatestProducts() {
        return productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Page<Products> findByIsActiveTrueAndTitle(String title, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return productsRepository.findByIsActiveTrueAndTitle(title, pageable);    
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
        return ordersRepoitory.findByUserIdOrderByIdDesc(userId);
    }
    
    // =========================== Contact Message =======================

    @Override
    public ContactMessage saveMessage(ContactMessage message) {
        return contactMessageRepository.save(message);
    }
    
}
