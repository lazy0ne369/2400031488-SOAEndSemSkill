package soa.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import soa.model.Users;
import soa.repo.UsersRepo;

@Service
public class UsersService {

    UsersRepo UR;
    PasswordEncoder passwordEncoder;

    public UsersService(UsersRepo UR, PasswordEncoder passwordEncoder) {
        this.UR = UR;
        this.passwordEncoder = passwordEncoder;
    }

    public Object registerUser(Users U) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (U.getUsername() == null || U.getUsername().trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Username cannot be empty");
                return response;
            }

            if (UR.findByUsername(U.getUsername().trim()) != null) {
                response.put("status", "error");
                response.put("message", "Username already exists");
                return response;
            }

            U.setUsername(U.getUsername().trim());
            if (U.getPassword() == null || U.getPassword().isBlank()) {
                response.put("status", "error");
                response.put("message", "Password cannot be empty");
                return response;
            }
            // Public registration always creates a normal user. Seed/admin-provision
            // administrator accounts directly in the database.
            U.setRole("USER");
            U.setPassword(passwordEncoder.encode(U.getPassword()));
            UR.save(U);

            response.put("status", "success");
            response.put("message", "User registered successfully");

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Object getAllUsers() {

        Map<String, Object> response = new HashMap<>();

        try {
            response.put("status", "success");
            response.put("data", UR.findAll());

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Object getUserById(Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Users> userOpt = UR.findById(id);
            if (userOpt.isPresent()) {
                response.put("status", "success");
                response.put("data", userOpt.get());
            } else {
                response.put("status", "error");
                response.put("message", "User not found with id: " + id);
            }

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Object deleteUser(Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (!UR.existsById(id)) {
                response.put("status", "error");
                response.put("message", "User not found with id: " + id);
                return response;
            }

            UR.deleteById(id);

            response.put("status", "success");
            response.put("message", "User deleted successfully");

        } catch (Exception e) {

            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }
}
