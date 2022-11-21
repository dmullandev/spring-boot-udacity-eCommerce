package com.example.demo.controllers;

import static com.example.demo.constants.TestConstants.TEST_INCORRECT_USER_NAME;
import static com.example.demo.constants.TestConstants.TEST_USER_ID;
import static com.example.demo.constants.TestConstants.TEST_USER_PASSWORD;
import static com.example.demo.constants.TestConstants.TEST_USER_PASSWORD_INCORRECT_LENGTH;
import static com.example.demo.constants.TestConstants.TEST_USER_PASSWORD_UNEQUAL;
import static com.example.demo.constants.TestConstants.TEST_USER_USERNAME;
import static com.example.demo.helpers.TestHelper.buildCreateUserRequest;
import static com.example.demo.helpers.TestHelper.buildTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

/**
 * Tests class {@link UserController}
 * 
 * @author "dmullandev"
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private CartRepository mockCartRepository;

    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    @InjectMocks
    private UserController testUserController;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findById() {

        buildCreateUserRequest(TEST_USER_USERNAME, TEST_USER_PASSWORD, TEST_USER_PASSWORD);

        when(mockUserRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(buildTestUser(TEST_USER_ID, TEST_USER_USERNAME, TEST_USER_PASSWORD)));

        ResponseEntity<User> response = testUserController.findById(TEST_USER_ID);

        assertEquals(TEST_USER_ID, response.getBody().getId());
    }

    @Test
    public void findByUsername() {
        User testUser = buildTestUser(TEST_USER_ID, TEST_USER_USERNAME, TEST_USER_PASSWORD);

        // happy path
        when(mockUserRepository.findByUsername(TEST_USER_USERNAME)).thenReturn(testUser);

        ResponseEntity<User> response = testUserController.findByUserName(testUser.getUsername());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TEST_USER_USERNAME, response.getBody().getUsername());
        assertEquals(TEST_USER_USERNAME, response.getBody().getUsername());

        // find non-existent
        ResponseEntity<User> response2 = testUserController.findByUserName(TEST_INCORRECT_USER_NAME);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        assertEquals(null, response2.getBody());
    }

    @Test
    public void createUser() {

        // ok
        ResponseEntity<User> response = testUserController.createUser(
                buildCreateUserRequest(TEST_USER_USERNAME, TEST_USER_PASSWORD, TEST_USER_PASSWORD));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // incorrect length
        ResponseEntity<User> response2 = testUserController.createUser(
                buildCreateUserRequest(TEST_USER_USERNAME, TEST_USER_PASSWORD_INCORRECT_LENGTH, TEST_USER_PASSWORD_INCORRECT_LENGTH));
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

        // password do not equal
        ResponseEntity<User> response3 = testUserController.createUser(
                buildCreateUserRequest(TEST_USER_USERNAME, TEST_USER_PASSWORD, TEST_USER_PASSWORD_UNEQUAL));
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());

    }
}
