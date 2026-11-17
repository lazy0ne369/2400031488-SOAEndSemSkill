package soa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import soa.model.Users;
import soa.repo.UsersRepo;

@Service
public class AuthService {

    UsersRepo UR;
    JWTService JWT;
    PasswordEncoder passwordEncoder;

    public AuthService(UsersRepo UR, JWTService JWT, PasswordEncoder passwordEncoder) {
        this.UR = UR;
        this.JWT = JWT;
        this.passwordEncoder = passwordEncoder;
    }

    public Object login(String username, String password) {

        Map<String, Object> response = new HashMap<>();

        try {

            Users U = UR.findByUsername(username);

            if (U == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return response;
            }

            if (password == null || !passwordEncoder.matches(password, U.getPassword())) {
                response.put("status", "error");
                response.put("message", "Invalid password");
                return response;
            }

            String token = (String) JWT.generateToken(
                    U.getUsername(),
                    U.getRole()
            );

            response.put("status", "success");
            response.put("token", token);
            response.put("role", U.getRole());
            response.put("message", "Login successful");

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}
