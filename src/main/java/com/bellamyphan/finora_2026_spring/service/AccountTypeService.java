package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.constant.AccountTypeEnum;
import com.bellamyphan.finora_2026_spring.entity.AccountType;
import com.bellamyphan.finora_2026_spring.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByName(AccountTypeEnum type) {
        return accountTypeRepository.findByName(type).isPresent();
    }

    @Transactional
    public AccountType save(AccountType type) {
        String uniqueId = nanoIdService.generateUniqueId(accountTypeRepository);
        type.setId(uniqueId);
        return accountTypeRepository.save(type);
    }

}
