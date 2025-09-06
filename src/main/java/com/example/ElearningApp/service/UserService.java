package com.example.ElearningApp.service;

import com.example.ElearningApp.entity.User;
import com.example.ElearningApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lưu user mới
    public User save(User user) {
        return userRepository.save(user);
    }

    // Tìm user theo username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Lấy toàn bộ user (nếu cần test)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
}
