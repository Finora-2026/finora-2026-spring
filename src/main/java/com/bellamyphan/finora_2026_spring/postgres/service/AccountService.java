package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.AccountBalanceRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.AccountBalanceResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.AccountEditDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.AccountResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Account;
import com.bellamyphan.finora_2026_spring.postgres.entity.AccountType;
import com.bellamyphan.finora_2026_spring.postgres.entity.Bank;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.repository.AccountRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class AccountService {

    private final NanoIdService nanoIdService;
    private final BankService bankService;
    private final AccountTypeService accountTypeService;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Save a new account with unique 10-char ID
     */
    @Transactional
    public Account createAccount(@Valid AccountEditDto accountEditDto, User user) {
        Bank bank = bankService.findBankById(accountEditDto.getBankId());
        AccountType type = accountTypeService.findAccountTypeById(accountEditDto.getTypeId());

        Account account = new Account(
                accountEditDto.getName(),
                accountEditDto.getOpeningDate(),
                accountEditDto.getClosingDate(),
                bank,
                type,
                user
        );

        String accountId = nanoIdService.generateUniqueId(accountRepository);
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
        return getAccountResponseDtos(accounts, user);
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

        return getAccountResponseDtos(accounts, user);
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

        return getAccountResponseDtos(accounts, user);
    }

    public Account findAccountEntityByIdAndUser(String accountId, User user) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }

        return accountRepository.findByIdAndUser_Id(accountId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Account not found or does not belong to user for id: " + accountId
                ));
    }

    public AccountResponseDto findAccountDtoByIdAndUser(String accountId, User user) {
        Account account = findAccountEntityByIdAndUser(accountId, user);
        BigDecimal pendingBalance = transactionRepository.calculatePendingBalance(account.getId(), user.getId());
        BigDecimal postedBalance = transactionRepository.calculatePostedBalance(account.getId(), user.getId());
        return AccountResponseDto.fromEntity(account, pendingBalance, postedBalance);
    }

    public AccountBalanceResponseDto findAccountBalanceAsOfDate(AccountBalanceRequestDto requestDto, User user) {
        // Verify ownership
        Account account = findAccountEntityByIdAndUser(requestDto.getAccountId(), user);

        BigDecimal pendingBalance = transactionRepository
                .calculatePendingBalanceAsOfDate(account.getId(), user.getId(), requestDto.getAsOfDate());

        BigDecimal postedBalance = transactionRepository
                .calculatePostedBalanceAsOfDate(account.getId(), user.getId(), requestDto.getAsOfDate());

        return AccountBalanceResponseDto.fromRequestDto(requestDto, pendingBalance, postedBalance);
    }

    @NonNull
    private List<AccountResponseDto> getAccountResponseDtos(List<Account> accounts, User user) {
        return accounts.stream()
                .map(account -> {
                    BigDecimal pendingBalance = transactionRepository
                            .calculatePendingBalance(account.getId(), user.getId());
                    BigDecimal postedBalance = transactionRepository
                            .calculatePostedBalance(account.getId(), user.getId());
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
