package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.*;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.AccountRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    public AccountEditDto createAccount(@Valid AccountEditDto accountEditDto, User user) {
        try {

            if (accountRepository.existsByUser_IdAndNameIgnoreCase(
                    user.getId(),
                    accountEditDto.getName()
            )) {
                throw new IllegalArgumentException(
                        "Account name already exists for this user"
                );
            }

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
            Account savedAccount = accountRepository.save(account);
            return AccountEditDto.fromEntity(savedAccount);

        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(
                    "Account name already exists for this user"
            );
        }
    }

    public boolean accountNameExists(String name, User user) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be blank");
        }

        return accountRepository.existsByUser_IdAndNameIgnoreCase(
                user.getId(),
                name.trim()
        );
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

    public AccountEditDto findAccountEditDtoByIdAndUser(String accountId, User user) {
        Account account = findAccountEntityByIdAndUser(accountId, user);
        return AccountEditDto.fromEntity(account);
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

    public List<AccountDailyBalanceDto> calculateLastNDaysBalances(String accountId, User user, int days) {
        // Days must be valid, natural number, non-zero
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive number");
        }

        // Check ownership
        findAccountEntityByIdAndUser(accountId, user);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);


        // Get balances before startDate
        AccountBalanceRequestDto startingRequestDto = new AccountBalanceRequestDto();
        startingRequestDto.setAccountId(accountId);
        startingRequestDto.setAsOfDate(startDateTime.minusDays(1));
        AccountBalanceResponseDto startingBalances =
                findAccountBalanceAsOfDate(startingRequestDto, user);
        BigDecimal runningPosted = startingBalances.getPostedBalance();
        BigDecimal runningPending = startingBalances.getPendingBalance();

        // Load all transaction belong to this account and between the dates range
        List<Transaction> transactions =
                transactionRepository.findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                        accountId,
                        startDateTime,
                        endDateTime
                );

        // Group by DATE (not datetime)
        Map<LocalDate, List<Transaction>> groupedByDate = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));
        List<AccountDailyBalanceDto> result = new ArrayList<>();

        // Build rolling balances
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            List<Transaction> dailyTransactions =
                    groupedByDate.getOrDefault(date, List.of());

            BigDecimal postedSum = dailyTransactions.stream()
                    .filter(Transaction::isPosted)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal allSum = dailyTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            runningPosted = runningPosted.add(postedSum);
            runningPending = runningPending.add(allSum);

            result.add(new AccountDailyBalanceDto(
                    date,
                    runningPending,
                    runningPosted
            ));
        }

        // Newest first, reverse the list then return
        Collections.reverse(result);
        return result;
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
