package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.entity.Bank;
import com.bellamyphan.finora_2026_spring.repository.BankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByName(String name) {
        return bankRepository.existsByNameIgnoreCase(name);
    }

    @Transactional
    public Bank save(Bank bank) {
        String uniqueId = nanoIdService.generateUniqueId(bankRepository);
        bank.setId(uniqueId);
        return bankRepository.save(bank);
    }
}
