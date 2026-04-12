package rag.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rag.backend.entity.User;
import rag.backend.exception.CustomException;
import rag.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(User user) {
        return userRepository.save(user);
    }

    public User login(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException.ResourceNotFoundException("user not found"));
    }
}
