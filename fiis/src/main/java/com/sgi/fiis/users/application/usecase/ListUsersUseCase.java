package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use case: List and search users with filters (RF-14).
 */
@Service
public class ListUsersUseCase {

    private final UserRepositoryPort userRepository;

    public ListUsersUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository.search(query.trim());
    }
}
