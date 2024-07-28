package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@Controller
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/admin")
    public String showAllUsers(ModelMap modelMap, Principal principal) {
        User user = userService.findByFirstname(principal.getName());
        String userRoles = userService.getUserRoles(user);
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("userRoles", userRoles);

        Map<User, String> usersWithRoles = userService.getAllUsersWithRoles();
        modelMap.addAttribute("usersWithRoles", usersWithRoles);

        return "admin";
    }

    @PostMapping(value = "/admin/addUser")
    public String addNewUser(@RequestParam("role") List<String> roles, @ModelAttribute("user") User user) {
        userService.addNewUser(roles,user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/updateUser")
    public String editUserForm(@RequestParam("id") Long id, ModelMap modelMap) {
        User userUpdate = userService.findById(id).orElse(null);
        List<Role> roles = roleService.findAll();
        modelMap.addAttribute("user", userUpdate);
        modelMap.addAttribute("listRoles", roles);
        return "admin";
    }

    @PostMapping(value = "/admin/updateUser")
    public String updateUser(@RequestParam("role") String role, @ModelAttribute("user") User user) {
        userService.updateUser(role ,user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/admin/deleteUser")
    public String deleteUser(@RequestParam long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}
