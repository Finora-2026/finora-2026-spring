package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.dto.AccountEditDto;
import com.bellamyphan.finora_2026_spring.entity.Account;
import com.bellamyphan.finora_2026_spring.entity.AccountType;
import com.bellamyphan.finora_2026_spring.entity.Bank;
import com.bellamyphan.finora_2026_spring.entity.User;
import com.bellamyphan.finora_2026_spring.repository.AccountRepository;
import com.bellamyphan.finora_2026_spring.repository.AccountTypeRepository;
import com.bellamyphan.finora_2026_spring.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final NanoIdService nanoIdService;

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final BankRepository bankRepository;

    /**
     * Save a new account with unique 10-char ID
     */
    @Transactional
    public Account createAccount(AccountEditDto accountEditDto, User user) {

        Bank bank = bankRepository.findById(accountEditDto.getBankId())
                .orElseThrow(() -> new RuntimeException(
                        "Bank group not found: " + accountEditDto.getBankId()));

        AccountType type = accountTypeRepository.findById(accountEditDto.getTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Account type not found for this typeId: " + accountEditDto.getTypeId()));

        Account account = new Account(
                accountEditDto.getName(),
                accountEditDto.getOpeningDate(),
                accountEditDto.getClosingDate(),
                bank,
                type,
                user
        );

        String accountId = nanoIdService.generateUniqueId(bankRepository);
        account.setId(accountId);
        accountRepository.save(account);

        return account;
    }


}
