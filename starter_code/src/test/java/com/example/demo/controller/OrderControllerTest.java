package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        com.example.demo.TestUtils.injectObjects(orderController, "userRepository", userRepository);
        com.example.demo.TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void Submit() {
        // Arrange
        User user = new User();
        user.setUsername("faisal");
        user.setPassword("password");
        user.setId(0L);

        Item item = new Item();
        item.setId(0L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(0L);
        List<Item> itemList = Collections.singletonList(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("faisal")).thenReturn(user);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("faisal");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        UserOrder retrievedUserOrder = response.getBody();
        assertThat(retrievedUserOrder).isNotNull();
        assertThat(retrievedUserOrder.getItems()).isNotNull();
        assertThat(retrievedUserOrder.getTotal()).isNotNull();
        assertThat(retrievedUserOrder.getUser()).isNotNull();
    }


    @Test
    public void SubmitNullUser() {
        // Arrange
        when(userRepository.findByUsername("faisal")).thenReturn(null);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("faisal");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void getOrdersForUser() {
        // Arrange
        User user = new User();
        user.setUsername("faisal");
        user.setPassword("password");
        user.setId(0L);

        Item item = new Item();
        item.setId(0L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(0L);
        List<Item> itemList = Collections.singletonList(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("faisal")).thenReturn(user);

        // Act
        orderController.submit("faisal");
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("faisal");

        // Assert
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        List<UserOrder> userOrders = responseEntity.getBody();
        assertThat(userOrders).isNotNull();
        assertThat(userOrders).isEmpty();
    }

    @Test
    public void getOrdersForUserNullUser() {
        // Arrange
        when(userRepository.findByUsername("faisal")).thenReturn(null);

        // Act
        orderController.submit("faisal");
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("faisal");

        // Assert
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }


    @Test
    public void userOrderTest() {
        // Arrange
        User user = new User();
        user.setUsername("faisal");
        user.setPassword("password");
        user.setId(0L);

        Item item = new Item();
        item.setId(0L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setItems(Collections.singletonList(item));
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("faisal")).thenReturn(user);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("faisal");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        UserOrder retrievedUserOrder = response.getBody();
        assertThat(retrievedUserOrder).isNotNull();

        assertThat(retrievedUserOrder.getItems()).hasSize(1);
        assertThat(retrievedUserOrder.getTotal()).isEqualByComparingTo(new BigDecimal("2.99"));
        assertThat(retrievedUserOrder.getUser()).isEqualTo(user);
    }

}