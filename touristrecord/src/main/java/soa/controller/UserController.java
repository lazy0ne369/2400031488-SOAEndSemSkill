package soa.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import soa.service.UsersService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    UsersService US;

    public UserController(UsersService US) {
        this.US = US;
    }

    @GetMapping
    public Object getAllUsers() {
        return US.getAllUsers();
    }

    @GetMapping("/{id}")
    public Object getUserById(@PathVariable Long id) {
        return US.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public Object deleteUser(@PathVariable Long id) {
        return US.deleteUser(id);
    }
}
