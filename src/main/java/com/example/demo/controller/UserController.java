package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Tạo mới (POST)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Lấy tất cả (GET)
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // PUT - Cập nhật TOÀN BỘ
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        // Nếu userDetails không có age/email → gán null!

        return ResponseEntity.ok(userRepository.save(user));
    }

    // PATCH - Cập nhật MỘT PHẦN
    @PatchMapping("/{id}")
    public ResponseEntity<User> partialUpdateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }

        return ResponseEntity.ok(userRepository.save(user));
    }

    // Xóa (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
