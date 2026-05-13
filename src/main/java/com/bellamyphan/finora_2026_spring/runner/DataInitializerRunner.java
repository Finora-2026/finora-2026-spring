package com.bellamyphan.finora_2026_spring.runner;

import com.bellamyphan.finora_2026_spring.config.DefaultAccountProperties;
import com.bellamyphan.finora_2026_spring.constant.AccountTypeEnum;
import com.bellamyphan.finora_2026_spring.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_2026_spring.dto.BrandCreateRequestDto;
import com.bellamyphan.finora_2026_spring.dto.UserCreateRequestDto;
import com.bellamyphan.finora_2026_spring.entity.AccountType;
import com.bellamyphan.finora_2026_spring.entity.Bank;
import com.bellamyphan.finora_2026_spring.entity.Role;
import com.bellamyphan.finora_2026_spring.entity.TransactionType;
import com.bellamyphan.finora_2026_spring.service.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class DataInitializerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerRunner.class);

    private final DefaultAccountProperties props;

    private final RoleService roleService;
    private final UserService userService;
    private final BrandService brandService;
    private final BankService bankService;
    private final TransactionTypeService transactionTypeService;
    private final AccountTypeService accountTypeService;

    private record BankSeed(String name, String url) {}

    @Override
    public void run(String @NonNull ... args) {
        initRoles();
        initAccountTypes();
        initTransactionTypes();
        initBrands();
        initBanks();
        createAdminAccount();
        createUserAccount();
    }

    private void initRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleService.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                Role saved = roleService.save(role);
                logger.info("✅ Role created: {}", saved.getName().name());
            } else {
                logger.info("ℹ️ Role already exists: {}", roleEnum.name());
            }
        }
    }

    private void createAdminAccount() {
        try {
            UserCreateRequestDto adminDto = new UserCreateRequestDto(
                    props.getAdmin().getName(),
                    props.getAdmin().getEmail(),
                    props.getAdmin().getPassword()
            );
            userService.createUser(adminDto, RoleEnum.ROLE_ADMIN);
            logger.info("✅ Default ADMIN account created: {}", adminDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.info("ℹ️ ADMIN account already exists or invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to create ADMIN account: {}", e.getMessage(), e);
        }
    }

    private void createUserAccount() {
        try {
            UserCreateRequestDto userDto = new UserCreateRequestDto(
                    props.getUser().getName(),
                    props.getUser().getEmail(),
                    props.getUser().getPassword()
            );
            userService.createUser(userDto, RoleEnum.ROLE_USER);
            logger.info("✅ Default USER account created: {}", userDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.info("ℹ️ USER account already exists or invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to create USER account: {}", e.getMessage(), e);
        }
    }

    private void initAccountTypes() {
        for (AccountTypeEnum typeEnum : AccountTypeEnum.values()) {
            if (!accountTypeService.existsByName(typeEnum)) {
                AccountType type = new AccountType();
                type.setName(typeEnum);
                AccountType created = accountTypeService.save(type);
                logger.info("✅ Account type created: {}", created.getName().name());
            } else {
                logger.info("ℹ️ Account type already exists: {}", typeEnum.name());
            }
        }
    }

    private void initBanks() {

        List<BankSeed> banks = List.of(
                new BankSeed("Chase", "https://www.chase.com"),
                new BankSeed("Bank of America", "https://www.bankofamerica.com"),
                new BankSeed("Wells Fargo", "https://www.wellsfargo.com"),
                new BankSeed("Citibank", "https://www.citi.com"),
                new BankSeed("U.S. Bank", "https://www.usbank.com"),
                new BankSeed("PNC Bank", "https://www.pnc.com"),
                new BankSeed("Truist Bank", "https://www.truist.com"),
                new BankSeed("Capital One", "https://www.capitalone.com"),
                new BankSeed("Goldman Sachs (Marcus)", "https://www.marcus.com"),
                new BankSeed("TD Bank", "https://www.td.com")
        );

        for (BankSeed bankSeed : banks) {

            if (!bankService.existsByName(bankSeed.name())) {

                Bank bank = new Bank();
                bank.setName(bankSeed.name());
                bank.setUrl(bankSeed.url());

                Bank created = bankService.save(bank);

                logger.info("✅ Bank created: {}", created.getName());
            } else {
                logger.info("ℹ️ Bank already exists: {}", bankSeed.name());
            }
        }
    }

    private void initTransactionTypes() {
        for (TransactionTypeEnum typeEnum : TransactionTypeEnum.values()) {
            if (!transactionTypeService.existsByType(typeEnum)) {
                TransactionType type = new TransactionType();
                type.setName(typeEnum);
                TransactionType saved = transactionTypeService.save(type);
                logger.info("✅ Transaction type created: {}", saved.getName().name());
            } else {
                logger.info("ℹ️ Transaction type already exists: {}", typeEnum.name());
            }
        }
    }

    private void initBrands() {
        Object[][] brands = {
                {"Netflix", "https://netflix.com"},
                {"Amazon", "https://amazon.com"},
                {"Walmart", "https://walmart.com"},
                {"Pizza Hut", "https://pizzahut.com"},
                {"Chipotle", "https://chipotle.com"},
                {"Starbucks", "https://starbucks.com"},
                {"McDonald's", "https://mcdonalds.com"},
                {"H-E-B", "https://heb.com"},
                {"Target", "https://target.com"},
                {"Spotify", "https://spotify.com"}
        };

        for (Object[] b : brands) {
            String name = (String) b[0];
            String url = (String) b[1];

            try {
                BrandCreateRequestDto dto = new BrandCreateRequestDto(name, url);
                brandService.createBrand(dto);
                logger.info("✅ Brand created: {}", name);
            } catch (IllegalArgumentException e) {
                logger.info("ℹ️ Brand already exists: {}", name);
            } catch (Exception e) {
                logger.error("❌ Failed to create brand: {} -> {}", name, e.getMessage(), e);
            }
        }
    }
}

