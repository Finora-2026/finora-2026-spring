package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.BankResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Bank;
import com.bellamyphan.finora_2026_spring.postgres.repository.BankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByName(String name) {
        return bankRepository.existsByNameIgnoreCase(name);
    }

    public List<BankResponseDto> getAllBanks() {
        List<Bank> banks = bankRepository.findAll();
        return banks.stream().map(BankResponseDto::fromEntity).toList();
    }

    @Transactional
    public Bank save(Bank bank) {
        String uniqueId = nanoIdService.generateUniqueId(bankRepository);
        bank.setId(uniqueId);
        return bankRepository.save(bank);
    }
}
