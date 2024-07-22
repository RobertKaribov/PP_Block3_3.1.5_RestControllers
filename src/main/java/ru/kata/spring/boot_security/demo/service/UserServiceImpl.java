package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String firstname) throws UsernameNotFoundException {
        return userRepository.findByFirstname(firstname);
    }

    @Override
    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void addNewUser(String role,User saveUser) {
            Role role1 = roleRepository.findByRole(role);
            saveUser.getRoles().add(role1);
            userRepository.save(saveUser);
    }

    @Override
    @Transactional
    public void updateUser(String role,User updateUser) {
        User user = userRepository.findById(updateUser.getId()).orElse(null);
        Role role1 = roleRepository.findByRole(role);
        Set<Role> roles = user.getRoles();
        roles.add(role1);
        updateUser.setRoles(roles);
        userRepository.save(updateUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            //Удаляем связанные роли пользователя из user_roles
            user.getRoles().clear();
            userRepository.deleteById(id);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByFirstname(String firstname) {
        return userRepository.findByFirstname(firstname);
    }
}
