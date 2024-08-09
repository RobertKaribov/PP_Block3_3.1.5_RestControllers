package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    void addUser(UserDto userDto);

    void updateUser(UserDto userDto);

    void deleteUser(long id);

    User findByFirstname(String firstname);

    Map<User, List<String>> getAllUsersWithRoles();
}
