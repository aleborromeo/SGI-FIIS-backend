package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.application.dto.PageDto;
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

    public PageDto<User> execute(String query, int page, int size) {
        return execute(query, page, size, null, null);
    }

    public PageDto<User> execute(String query, int page, int size, String role, Boolean active) {
        if (query == null && role == null && active == null) {
            List<User> users = userRepository.findAllPaged(page, size);
            long total = userRepository.countAll();
            return new PageDto<>(users, total, page, size);
        }
        String searchQuery = (query != null) ? query.trim() : "";
        List<User> users = userRepository.search(searchQuery, role, active);
        return new PageDto<>(users, users.size(), 0, users.size());
    }
}
