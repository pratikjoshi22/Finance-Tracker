package com.financeapp.personalfinance.repository;

import com.financeapp.personalfinance.dto.AccountSummaryDto;
import com.financeapp.personalfinance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Derived query methods - Spring Data JPA will automatically implement these

    /**
     * Find account by account number
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Find all accounts for a specific user
     */
    List<Account> findByUserId(Long userId);

    /**
     * Find accounts by account type
     */
    List<Account> findByAccountType(Account.AccountType accountType);

    /**
     * Find accounts by user ID and account type
     */
    List<Account> findByUserIdAndAccountType(Long userId, Account.AccountType accountType);

    /**
     * Check if account number exists
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Count accounts by user ID
     */
    long countByUserId(Long userId);

    /**
     * Find accounts by user ID and currency
     */
    List<Account> findByUserIdAndCurrency(Long userId, String currency);

    /**
     * Find accounts with balance greater than specified amount
     */
    List<Account> findByBalanceGreaterThan(BigDecimal amount);

    /**
     * Find accounts with balance between specified amounts
     */
    List<Account> findByBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);

    // Custom queries using @Query annotation

    /**
     * Get total balance for a user across all accounts
     */
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.userId = :userId")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

    /**
     * Get total balance by user ID and account type
     */
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.userId = :userId AND a.accountType = :accountType")
    BigDecimal getTotalBalanceByUserIdAndAccountType(@Param("userId") Long userId,
                                                     @Param("accountType") Account.AccountType accountType);

    /**
     * Find accounts by user ID ordered by balance descending
     */
    @Query("SELECT a FROM Account a WHERE a.userId = :userId ORDER BY a.balance DESC")
    List<Account> findByUserIdOrderByBalanceDesc(@Param("userId") Long userId);

    /**
     * Get account statistics for a user
     */
    @Query("SELECT new com.financeapp.personalfinance.dto.AccountSummaryDto(" +
            "COUNT(a), " +
            "COALESCE(SUM(a.balance), 0), " +
            "CAST(COALESCE(AVG(a.balance), 0) AS java.math.BigDecimal), " +
            "COALESCE(MAX(a.balance), 0), " +
            "COALESCE(MIN(a.balance), 0)) " +
            "FROM Account a WHERE a.userId = :userId")
    AccountSummaryDto getAccountSummaryByUserId(@Param("userId") Long userId);

    /**
     * Find accounts with low balance (less than specified amount)
     */
    @Query("SELECT a FROM Account a WHERE a.balance < :threshold AND a.accountType != 'CREDIT_CARD'")
    List<Account> findAccountsWithLowBalance(@Param("threshold") BigDecimal threshold);

    /**
     * Get accounts created in the last N days
     */
    @Query("SELECT a FROM Account a WHERE a.createdAt >= CURRENT_TIMESTAMP - :days DAY")
    List<Account> findRecentAccounts(@Param("days") int days);

    /**
     * Find inactive accounts (not updated in the last N days)
     */
    @Query("SELECT a FROM Account a WHERE a.updatedAt < CURRENT_TIMESTAMP - :days DAY")
    List<Account> findInactiveAccounts(@Param("days") int days);

    /**
     * Get account count by type
     */
    @Query("SELECT a.accountType, COUNT(a) FROM Account a GROUP BY a.accountType")
    List<Object[]> getAccountCountByType();
}