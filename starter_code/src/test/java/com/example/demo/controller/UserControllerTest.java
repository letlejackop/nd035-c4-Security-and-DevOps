package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
    @Before
    public void setup(){
        userController = new UserController();
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObjects(userDetailsService, "userRepository", userRepository);

    }
    @Test
    public void createUser_HappyPath() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("faisal");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword("thisIsHashed");

        when(encoder.encode(request.getPassword())).thenReturn("thisIsHashed");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.createUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        User createdUser = response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isEqualTo(0);
        assertThat(createdUser.getUsername()).isEqualTo("faisal");
        assertThat(createdUser.getPassword()).isEqualTo("thisIsHashed");
    }
    @Test
    public void findById() {
        // Arrange
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("faisal");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();

        // Mock the repository's findById method
        when(userRepository.findById(createdUser.getId())).thenReturn(java.util.Optional.ofNullable(createdUser));

        // Act
        ResponseEntity<User> res = userController.findById(createdUser.getId());
        User foundUser = res.getBody();

        // Assert
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals(createdUser.getUsername(), foundUser.getUsername());
        assertEquals(createdUser.getPassword(), foundUser.getPassword());
    }

    @Test
    public void findByUserName() {
        // Arrange
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("faisal");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User createdUser = response.getBody();
        assertThat(createdUser).isNotNull();

        when(userRepository.findByUsername("test")).thenReturn(createdUser);

        // Act
        ResponseEntity<User> res = userController.findByUserName("test");
        User foundUser = res.getBody();

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getUsername()).isEqualTo(createdUser.getUsername());
        assertThat(foundUser.getPassword()).isEqualTo(createdUser.getPassword());
    }

    @Test
    public void userDetailsTest(){
        User user = new User();
        user.setUsername("faisal");
        String password = "password";
        user.setPassword(password);
        user.setId(0L);
        when(userRepository.findByUsername("faisal")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("faisal");
        assertNotNull(userDetails);
        Collection<? extends GrantedAuthority> authorityCollection = userDetails.getAuthorities();
        assertNotNull(authorityCollection);
        assertEquals(0, authorityCollection.size());
        assertEquals(password, userDetails.getPassword());
        assertEquals("faisal", userDetails.getUsername());
    }
}