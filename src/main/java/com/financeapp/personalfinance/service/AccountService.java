package com.financeapp.personalfinance.service;

import com.financeapp.personalfinance.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private UserService userService;

    // In-memory storage (will be replaced with database in Phase 2)
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Create a new account
    public Account createAccount(Account account) {
        // Validate required fields
        if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Account name is required");
        }
        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (account.getAccountType() == null) {
            throw new IllegalArgumentException("Account type is required");
        }
        if (account.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Validate that user exists
        if (userService.getUserById(account.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + account.getUserId());
        }

        // Check if account number already exists
        if (accountNumberExists(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account with this account number already exists");
        }

        // Generate ID and save
        account.setId(idGenerator.getAndIncrement());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        // Set default balance if not provided
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        accounts.put(account.getId(), account);
        return account;
    }

    // Get account by ID
    public Optional<Account> getAccountById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    // Get account by account number
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accounts.values().stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // Get accounts by user ID
    public List<Account> getAccountsByUserId(Long userId) {
        return accounts.values().stream()
                .filter(account -> account.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // Get accounts by type
    public List<Account> getAccountsByType(Account.AccountType accountType) {
        return accounts.values().stream()
                .filter(account -> account.getAccountType() == accountType)
                .collect(Collectors.toList());
    }

    // Update account
    public Account updateAccount(Long id, Account updatedAccount) {
        Account existingAccount = accounts.get(id);
        if (existingAccount == null) {
            throw new RuntimeException("Account not found with id: " + id);
        }

        // Check if account number is being changed and new number already exists
        if (!existingAccount.getAccountNumber().equals(updatedAccount.getAccountNumber()) &&
                accountNumberExists(updatedAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Account with this account number already exists");
        }

        // Update fields (but not balance - that should be done through transactions)
        existingAccount.setAccountName(updatedAccount.getAccountName());
        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setCurrency(updatedAccount.getCurrency());
        existingAccount.setUpdatedAt(LocalDateTime.now());

        return existingAccount;
    }

    // Delete account
    public boolean deleteAccount(Long id) {
        Account account = accounts.get(id);
        if (account != null) {
            // Business rule: Don't allow deletion of accounts with non-zero balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("Cannot delete account with non-zero balance");
            }
            return accounts.remove(id) != null;
        }
        return false;
    }

    // Update account balance
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }

        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());
        return account;
    }

    // Get total balance for a user
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        return getAccountsByUserId(userId).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Check if account number exists
    private boolean accountNumberExists(String accountNumber) {
        return accounts.values().stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNumber));
    }

    // Get account count
    public long getAccountCount() {
        return accounts.size();
    }

    // Get accounts by user ID and type
    public List<Account> getAccountsByUserIdAndType(Long userId, Account.AccountType accountType) {
        return accounts.values().stream()
                .filter(account -> account.getUserId().equals(userId) &&
                        account.getAccountType() == accountType)
                .collect(Collectors.toList());
    }
}