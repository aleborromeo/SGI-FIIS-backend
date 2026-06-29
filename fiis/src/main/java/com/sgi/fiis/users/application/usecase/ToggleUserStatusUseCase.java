package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Activate or deactivate a user (RF-11, RF-13).
 */
@Service
public class ToggleUserStatusUseCase {

    private final UserRepositoryPort userRepository;

    public ToggleUserStatusUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(Long id, boolean activate, Long authenticatedUserId) {
        // RF-13: Prevent admin from deactivating their own account
        if (id.equals(authenticatedUserId) && !activate) {
            throw new BusinessException("No puedes desactivar tu propia cuenta");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (activate) {
            user.activate();
        } else {
            user.deactivate();
        }

        return userRepository.save(user);
    }
}
