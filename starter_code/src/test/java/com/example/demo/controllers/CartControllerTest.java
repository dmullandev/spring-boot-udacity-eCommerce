package com.example.demo.controllers;

import static com.example.demo.constants.TestConstants.TEST_ITEM_DESCRIPTION;
import static com.example.demo.constants.TestConstants.TEST_ITEM_ID;
import static com.example.demo.constants.TestConstants.TEST_ITEM_NAME;
import static com.example.demo.constants.TestConstants.TEST_ITEM_PRICE;
import static com.example.demo.constants.TestConstants.TEST_USER_ID;
import static com.example.demo.constants.TestConstants.TEST_USER_PASSWORD;
import static com.example.demo.constants.TestConstants.TEST_USER_USERNAME;
import static com.example.demo.helpers.TestHelper.buildModifyCartRequest;
import static com.example.demo.helpers.TestHelper.buildTestItem;
import static com.example.demo.helpers.TestHelper.buildTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
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
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

/**
 * Tests class {@link CartController}
 * 
 * @author "dmullandev"
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class CartControllerTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private CartRepository mockCartRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @InjectMocks
    private CartController testCartController;

    // multiple items
    private static final Long TEST_ITEM_ID2 = 2L;
    private static final String TEST_ITEM_NAME2 = "testItemName2";
    private static final BigDecimal TEST_ITEM_PRICE2 = new BigDecimal(2.99);
    private static final String TEST_ITEM_DESCRIPTION2 = "testDescription2";

    // user not found
    private static final String TEST_USER_NOTFOUND = "testUserNotfound";

    // item not found
    private static final Long TEST_ITEM_ID3_NOTFOUND = 3L;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);

        User user = buildTestUser(TEST_USER_ID, TEST_USER_USERNAME, TEST_USER_PASSWORD);
        user.setCart(new Cart());
        when(mockUserRepository.findByUsername(TEST_USER_USERNAME)).thenReturn(user);
    }

    @Test
    public void addItemToCart() {

        // one item
        when(mockItemRepository.findById(TEST_ITEM_ID)).thenReturn(
                Optional.of(buildTestItem(TEST_ITEM_ID, TEST_ITEM_NAME, TEST_ITEM_PRICE, TEST_ITEM_DESCRIPTION)));

        ResponseEntity<Cart> response = testCartController.addTocart(buildModifyCartRequest(TEST_USER_USERNAME, TEST_ITEM_ID, 1));

        List<Item> itemTracker = new ArrayList<>();

        itemTracker.add(buildTestItem(TEST_ITEM_ID, TEST_ITEM_NAME, TEST_ITEM_PRICE, TEST_ITEM_DESCRIPTION));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemTracker, response.getBody().getItems());
        assertEquals(calculateCartTotalPrice(itemTracker), response.getBody().getTotal());

        // multiple items
        when(mockItemRepository.findById(TEST_ITEM_ID2)).thenReturn(
                Optional.of(buildTestItem(TEST_ITEM_ID2, TEST_ITEM_NAME2, TEST_ITEM_PRICE2, TEST_ITEM_DESCRIPTION2)));

        itemTracker.add(buildTestItem(TEST_ITEM_ID2, TEST_ITEM_NAME2, TEST_ITEM_PRICE2, TEST_ITEM_DESCRIPTION2));

        ResponseEntity<Cart> response2 = testCartController.addTocart(buildModifyCartRequest(TEST_USER_USERNAME, TEST_ITEM_ID2, 1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemTracker, response.getBody().getItems());
        assertEquals(calculateCartTotalPrice(itemTracker), response.getBody().getTotal());

        // user not found
        ResponseEntity<Cart> response3 = testCartController.addTocart(buildModifyCartRequest(TEST_USER_NOTFOUND, TEST_ITEM_ID2, 1));

        assertEquals(HttpStatus.NOT_FOUND, response3.getStatusCode());

        // item not found
        when(mockItemRepository.findById(TEST_ITEM_ID3_NOTFOUND)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response4 = testCartController.addTocart(buildModifyCartRequest(TEST_USER_USERNAME, TEST_ITEM_ID3_NOTFOUND, 1));

        assertEquals(HttpStatus.NOT_FOUND, response4.getStatusCode());
    }

    private BigDecimal calculateCartTotalPrice(List<Item> items) {
        return items.stream()
                    .map(item -> item.getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
