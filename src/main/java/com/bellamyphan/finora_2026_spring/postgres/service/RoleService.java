package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.postgres.entity.Role;
import com.bellamyphan.finora_2026_spring.postgres.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final NanoIdService nanoIdService;
    private final RoleRepository roleRepository;

    /**
     * Check if a role exists by its enum name.
     */
    public boolean existsByName(RoleEnum name) {
        return roleRepository.findByName(name).isPresent();
    }

    /**
     * Save a Role with a generated unique ID. Retries up to 10 times if ID collision occurs.
     */
    @Transactional
    public Role save(Role role) {
        String newId = nanoIdService.generateUniqueId(roleRepository);
        role.setId(newId);
        return roleRepository.save(role);
    }
}
