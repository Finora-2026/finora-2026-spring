package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.dto.AccountEditDto;
import com.bellamyphan.finora_2026_spring.dto.AccountResponseDto;
import com.bellamyphan.finora_2026_spring.entity.Account;
import com.bellamyphan.finora_2026_spring.entity.AccountType;
import com.bellamyphan.finora_2026_spring.entity.Bank;
import com.bellamyphan.finora_2026_spring.entity.User;
import com.bellamyphan.finora_2026_spring.repository.AccountRepository;
import com.bellamyphan.finora_2026_spring.repository.AccountTypeRepository;
import com.bellamyphan.finora_2026_spring.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Find all accounts belonging to a given user
     */
    public List<AccountResponseDto> findAllAccountByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        List<Account> accounts = accountRepository.findByUser(user).stream()
                .sorted((b1, b2) -> b1.getBank().getName().compareToIgnoreCase(b2.getBank().getName()))
                .toList();
        return getAccountResponseDtos(accounts);
    }

    /**
     * Find active accounts for a user (closingDate == null)
     */
    public List<AccountResponseDto> findActiveAccountsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<Account> accounts = accountRepository.findByUser(user).stream()
                .filter(account -> account.getClosingDate() == null) // only active accounts
                .sorted((b1, b2) -> b1.getBank().getName().compareToIgnoreCase(b2.getBank().getName()))
                .toList();

        return getAccountResponseDtos(accounts);
    }

    /**
     * Find inactive accounts for a user (closingDate != null)
     */
    public List<AccountResponseDto> findInactiveAccountsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<Account> accounts = accountRepository.findByUser(user).stream()
                .filter(account -> account.getClosingDate() != null) // only inactive accounts
                .sorted((b1, b2) -> b1.getBank().getName().compareToIgnoreCase(b2.getBank().getName()))
                .toList();

        return getAccountResponseDtos(accounts);
    }

    @NonNull
    private List<AccountResponseDto> getAccountResponseDtos(List<Account> accounts) {
        return accounts.stream()
                .map(account -> {
                    // Todo: Calculate this amount later
//                    BigDecimal pendingBalance = calculatePendingBalance(account.getId());
//                    BigDecimal postedBalance = calculatePostedBalance(account.getId());
                    BigDecimal pendingBalance = new BigDecimal("0");
                    BigDecimal postedBalance = new BigDecimal("0");
                    return new AccountResponseDto(
                            account.getId(),
                            account.getName(),
                            account.getBank().getId(),
                            account.getBank().getName(),
                            account.getAccountType().getName(),
                            account.getUser().getEmail(),
                            pendingBalance,
                            postedBalance
                    );
                })
                .collect(Collectors.toList());
    }


}
