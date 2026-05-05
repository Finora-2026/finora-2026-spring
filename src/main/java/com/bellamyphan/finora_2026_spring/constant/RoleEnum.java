package com.bellamyphan.finora_2026_spring.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ROLE_ADMIN,
    ROLE_SUPPORT,
    ROLE_USER;

    // Find role enum from a string (case-insensitive)
    public static RoleEnum fromRoleName(String roleName) {
        for (RoleEnum role : values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for name: " + roleName);
    }

}
