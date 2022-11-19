package com.example.demo.controllers;

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
import com.example.demo.model.requests.CreateUserRequest;

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

    private static final Long TEST_USER_ID = 1L;

    private static final String TEST_USER_NAME = "testUser";

    private static final String TEST_USER_PASSWORD = "testPass";

    private static final String TEST_USER_PASSWORD_INCORRECT_LENGTH = "pass";

    private static final String TEST_INCORRECT_USER_NAME = "fakeUserName";

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        createInitialUserRequest();
    }

    @Test
    public void findById() {

        Optional<User> testUserOptional = Optional.of(buildTestUser());

        when(mockUserRepository.findById(TEST_USER_ID)).thenReturn(testUserOptional);

        ResponseEntity<User> response = testUserController.findById(TEST_USER_ID);

        Long responseId = response.getBody().getId();

        assertEquals(TEST_USER_ID, responseId);
    }

    @Test
    public void findByUsername() {
        User testUser = buildTestUser();

        when(mockUserRepository.findByUsername(TEST_USER_NAME)).thenReturn(testUser);

        ResponseEntity<User> response = testUserController.findByUserName(testUser.getUsername());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TEST_USER_NAME, response.getBody().getUsername());
        // verify

        ResponseEntity<User> response2 = testUserController.findByUserName(TEST_INCORRECT_USER_NAME);

        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());

        // when(mockUserRepository.findByUsername(TEST_USER_NAME)).thenReturn(testUser);

        // response.getStatusCode().compareTo(R)
    }

    private void createInitialUserRequest() {

        // when
        CreateUserRequest cur = new CreateUserRequest();

        cur.setUsername(TEST_USER_NAME);
        cur.setPassword(TEST_USER_PASSWORD);
        cur.setConfirmPassword(TEST_USER_PASSWORD);

        testUserController.createUser(cur);
    }

    private User buildTestUser() {
        User user = new User();

        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USER_NAME);
        user.setPassword(TEST_USER_PASSWORD);

        return user;
    }
}
