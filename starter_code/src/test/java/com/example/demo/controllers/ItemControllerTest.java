package com.example.demo.controllers;

import static com.example.demo.constants.TestConstants.TEST_ITEM_DESCRIPTION;
import static com.example.demo.constants.TestConstants.TEST_ITEM_ID;
import static com.example.demo.constants.TestConstants.TEST_ITEM_NAME;
import static com.example.demo.constants.TestConstants.TEST_ITEM_PRICE;
import static com.example.demo.helpers.TestHelper.buildTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

/**
 * Tests class {@link ItemController}
 * 
 * @author "dmullandev"
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class ItemControllerTest {

    @Mock
    private ItemRepository mockItemRepository;

    @InjectMocks
    private ItemController testItemController;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getItems() {
        ResponseEntity<List<Item>> response = testItemController.getItems();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getItemById() {

        when(mockItemRepository.findById(TEST_ITEM_ID)).thenReturn(
                Optional.of(buildTestItem(TEST_ITEM_ID, TEST_ITEM_NAME, TEST_ITEM_PRICE, TEST_ITEM_DESCRIPTION)));

        // item found
        ResponseEntity<Item> response = testItemController.getItemById(TEST_ITEM_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getId(), TEST_ITEM_ID);
        assertEquals(response.getBody().getName(), TEST_ITEM_NAME);
        assertEquals(response.getBody().getPrice(), TEST_ITEM_PRICE);
        assertEquals(response.getBody().getDescription(), TEST_ITEM_DESCRIPTION);

        final Long TEST_ITEM_ID2 = 2L;

        // item notfound
        ResponseEntity<Item> response2 = testItemController.getItemById(TEST_ITEM_ID2);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());

    }

    @Test
    public void getItemByName() {
        List<Item> items = new ArrayList<>();
        items.add(buildTestItem(TEST_ITEM_ID, TEST_ITEM_NAME, TEST_ITEM_PRICE, TEST_ITEM_DESCRIPTION));

        when(mockItemRepository.findByName(TEST_ITEM_NAME)).thenReturn(items);

        // items found
        ResponseEntity<List<Item>> response = testItemController.getItemsByName(TEST_ITEM_NAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().get(0).getId(), TEST_ITEM_ID);
        assertEquals(response.getBody().get(0).getName(), TEST_ITEM_NAME);
        assertEquals(response.getBody().get(0).getPrice(), TEST_ITEM_PRICE);
        assertEquals(response.getBody().get(0).getDescription(), TEST_ITEM_DESCRIPTION);

        final String TEST_ITEM_NAME2 = "testItemName2";
        when(mockItemRepository.findByName(TEST_ITEM_NAME2)).thenReturn(null);

        // items not found
        ResponseEntity<List<Item>> response2 = testItemController.getItemsByName(TEST_ITEM_NAME2);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertEquals(null, response2.getBody());
    }
}
