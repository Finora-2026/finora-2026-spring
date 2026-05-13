package com.bellamyphan.finora_2026_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAccountId implements Serializable {
    private String report;
    private String account;
}
