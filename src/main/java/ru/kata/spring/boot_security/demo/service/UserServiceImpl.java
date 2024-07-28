package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void addNewUser(List<String> roles,User saveUser) {
        for (String role : roles) {
            Role role1 = roleRepository.findByRole(role);
            saveUser.getRoles().add(role1);
        }
        userRepository.save(saveUser);
    }

    @Override
    @Transactional
    public void updateUser(String role,User updateUser) {
        Role role1 = roleRepository.findByRole(role);
        updateUser.getRoles().add(role1);
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

    @Override
    public String getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getRole)
                .map(roleName -> roleName.replace("ROLE_", ""))
                .sorted()
                .collect(Collectors.joining(" "));
    }

    @Override
    public Map<User, String> getAllUsersWithRoles() {
        return userRepository.findAll(Sort.by("id")).stream()
                .collect(Collectors.toMap(user -> user, this::getUserRoles,
                        (oldValue,newValue) -> oldValue, LinkedHashMap::new));
    }

}
