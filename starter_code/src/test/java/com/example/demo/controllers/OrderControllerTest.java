package com.example.demo.controllers;

import static com.example.demo.constants.TestConstants.TEST_ITEM_DESCRIPTION;
import static com.example.demo.constants.TestConstants.TEST_ITEM_ID;
import static com.example.demo.constants.TestConstants.TEST_ITEM_NAME;
import static com.example.demo.constants.TestConstants.TEST_ITEM_PRICE;
import static com.example.demo.constants.TestConstants.TEST_USER_ID;
import static com.example.demo.constants.TestConstants.TEST_USER_PASSWORD;
import static com.example.demo.constants.TestConstants.TEST_USER_USERNAME;
import static com.example.demo.helpers.TestHelper.buildTestItem;
import static com.example.demo.helpers.TestHelper.buildTestUser;
import static com.example.demo.helpers.TestHelper.buildTestUserCart;
import static com.example.demo.helpers.TestHelper.calculateCartTotalPrice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

/**
 * Tests class {@link OrderController}
 * 
 * @author "dmullandev"
 *
 */
@TestInstance(Lifecycle.PER_METHOD)
public class OrderControllerTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private OrderRepository mockOrderRepository;

    @InjectMocks
    private OrderController testOrderController;

    // multiple items
    private static final Long TEST_ITEM_ID2 = 2L;
    private static final String TEST_ITEM_NAME2 = "testItemName2";
    private static final BigDecimal TEST_ITEM_PRICE2 = new BigDecimal(2.99);
    private static final String TEST_ITEM_DESCRIPTION2 = "testDescription2";

    @BeforeEach
    public void reset() {
        MockitoAnnotations.initMocks(this);

        mockUserRepository.deleteAll();
        mockOrderRepository.deleteAll();
    }

    @Test
    public void submit() {
        User user = buildTestUser(TEST_USER_ID, TEST_USER_USERNAME, TEST_USER_PASSWORD);

        List<Item> items = new ArrayList<>();

        items.add(buildTestItem(TEST_ITEM_ID, TEST_ITEM_NAME, TEST_ITEM_PRICE, TEST_ITEM_DESCRIPTION));
        items.add(buildTestItem(TEST_ITEM_ID2, TEST_ITEM_NAME2, TEST_ITEM_PRICE2, TEST_ITEM_DESCRIPTION2));

        Cart cart = buildTestUserCart(TEST_USER_ID, items, user, calculateCartTotalPrice(items));

        user.setCart(cart);

        when(mockUserRepository.findByUsername(TEST_USER_USERNAME)).thenReturn(user);

        ResponseEntity<UserOrder> response = testOrderController.submit(TEST_USER_USERNAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody().getUser());
        assertEquals(items, response.getBody().getItems());
        assertEquals(calculateCartTotalPrice(items), response.getBody().getTotal());

        // user not found
        when(mockUserRepository.findByUsername(TEST_USER_USERNAME)).thenReturn(null);

        ResponseEntity<UserOrder> response2 = testOrderController.submit(TEST_USER_USERNAME);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
    }

}
