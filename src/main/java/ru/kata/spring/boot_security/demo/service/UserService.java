package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> showAllUsers();

    void addNewUser(String role, User saveUser);

    void updateUser(String role, User updateUser);

    void deleteUser(long id);

    Optional<User> findById(Long id);

    User findByFirstname(String firstname);
}
