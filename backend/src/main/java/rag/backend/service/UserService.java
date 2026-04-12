package rag.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.User;
import rag.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(User user) {
        // TODO: hash password before saving
        return userRepository.save(user);
    }

    public User login(String email) {
        // TODO: custom exception
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
