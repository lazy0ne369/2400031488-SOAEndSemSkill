package soa.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import soa.model.Users;
import soa.service.AuthService;
import soa.service.UsersService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    UsersService US;
    AuthService AS;

    public AuthController(UsersService US, AuthService AS) {
        this.US = US;
        this.AS = AS;
    }

    @PostMapping("/register")
    public Object register(@RequestBody Users U) {
        return US.registerUser(U);
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> data) {

        String username = data.get("username");
        String password = data.get("password");

        return AS.login(username, password);
    }
}
