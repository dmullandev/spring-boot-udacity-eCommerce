package com.example.demo.helpers;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;

public class TestHelper {

    public static User buildTestUser(Long id, String username, String password) {
        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }

    public static CreateUserRequest buildCreateUserRequest(String username, String password, String confirmPassword) {
        CreateUserRequest request = new CreateUserRequest();

        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);

        return request;
    }

    public static Item buildTestItem(Long id, String name, BigDecimal price, String description) {
        Item item = new Item();

        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);

        return item;
    }

    public static ModifyCartRequest buildModifyCartRequest(String username, long itemId, int quantity) {
        ModifyCartRequest request = new ModifyCartRequest();

        request.setUsername(username);
        request.setItemId(itemId);
        request.setQuantity(quantity);

        return request;
    }

    public static Cart buildTestUserCart(Long id, List<Item> items, User user, BigDecimal total) {
        Cart cart = new Cart();

        cart.setId(id);
        cart.setItems(items);
        cart.setUser(user);
        cart.setTotal(total);

        return cart;
    }

}
