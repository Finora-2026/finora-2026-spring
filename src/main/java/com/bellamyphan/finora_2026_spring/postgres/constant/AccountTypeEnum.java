package com.bellamyphan.finora_2026_spring.postgres.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {

    CHECKING,
    SAVINGS,
    CREDIT,
    REWARDS;

    public static AccountTypeEnum fromName(String name) {
        for (AccountTypeEnum accountTypeEnum : values()) {
            if (accountTypeEnum.name().equalsIgnoreCase(name)) {
                return accountTypeEnum;
            }
        }
        throw new IllegalArgumentException("No matching AccountTypeEnum for name: " + name);
    }
}
