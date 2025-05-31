package com.financeapp.personalfinance.dto;

import java.math.BigDecimal;

/**
 * DTO for account summary information
 */
public class AccountSummaryDto {
    private Long totalAccounts;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private BigDecimal maxBalance;
    private BigDecimal minBalance;

    // Default constructor
    public AccountSummaryDto() {}

    // Constructor for JPQL query projection
    public AccountSummaryDto(Long totalAccounts, BigDecimal totalBalance,
                             BigDecimal averageBalance, BigDecimal maxBalance, BigDecimal minBalance) {
        this.totalAccounts = totalAccounts;
        this.totalBalance = totalBalance;
        this.averageBalance = averageBalance;
        this.maxBalance = maxBalance;
        this.minBalance = minBalance;
    }

    // Getters and setters
    public Long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(Long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getAverageBalance() {
        return averageBalance;
    }

    public void setAverageBalance(BigDecimal averageBalance) {
        this.averageBalance = averageBalance;
    }

    public BigDecimal getMaxBalance() {
        return maxBalance;
    }

    public void setMaxBalance(BigDecimal maxBalance) {
        this.maxBalance = maxBalance;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    @Override
    public String toString() {
        return "AccountSummaryDto{" +
                "totalAccounts=" + totalAccounts +
                ", totalBalance=" + totalBalance +
                ", averageBalance=" + averageBalance +
                ", maxBalance=" + maxBalance +
                ", minBalance=" + minBalance +
                '}';
    }
}