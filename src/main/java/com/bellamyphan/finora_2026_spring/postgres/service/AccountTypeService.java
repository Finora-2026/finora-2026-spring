package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.AccountTypeEnum;
import com.bellamyphan.finora_2026_spring.postgres.dto.AccountTypeResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.AccountType;
import com.bellamyphan.finora_2026_spring.postgres.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByName(AccountTypeEnum type) {
        return accountTypeRepository.findByName(type).isPresent();
    }

    public List<AccountTypeResponseDto> getAllAccountTypes() {
        List<AccountType> types = accountTypeRepository.findAll();
        return types.stream().map(AccountTypeResponseDto::fromEntity).toList();
    }

    @Transactional
    public AccountType save(AccountType type) {
        String uniqueId = nanoIdService.generateUniqueId(accountTypeRepository);
        type.setId(uniqueId);
        return accountTypeRepository.save(type);
    }

}
