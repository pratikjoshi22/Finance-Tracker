package com.financeapp.personalfinance.service;

import com.financeapp.personalfinance.model.Account;
import com.financeapp.personalfinance.repository.AccountRepository;
import com.financeapp.personalfinance.dto.AccountSummaryDto;
import com.financeapp.personalfinance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository; // Assuming you have this from your User implementation

    /**
     * Create a new account
     */
    public Account createAccount(Account account) {
        // Validate required fields
        validateAccountData(account);

        // Validate that user exists
        if (!userRepository.existsById(account.getUserId())) {
            throw new IllegalArgumentException("User not found with id: " + account.getUserId());
        }

        // Check if account number already exists
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account with this account number already exists");
        }

        // Set default values if not provided
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getCurrency() == null) {
            account.setCurrency("USD");
        }

        return accountRepository.save(account);
    }

    /**
     * Get account by ID
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    /**
     * Get account by account number
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Get all accounts
     */
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Get accounts by user ID
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Get accounts by type
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }

    /**
     * Get accounts by user ID and type
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserIdAndType(Long userId, Account.AccountType accountType) {
        return accountRepository.findByUserIdAndAccountType(userId, accountType);
    }

    /**
     * Update account
     */
    public Account updateAccount(Long id, Account updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        // Check if account number is being changed and new number already exists
        if (!existingAccount.getAccountNumber().equals(updatedAccount.getAccountNumber()) &&
                accountRepository.existsByAccountNumber(updatedAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Account with this account number already exists");
        }

        // Update fields (but preserve balance - that should be done through transactions)
        existingAccount.setAccountName(updatedAccount.getAccountName());
        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setAccountType(updatedAccount.getAccountType());
        existingAccount.setCurrency(updatedAccount.getCurrency());
        existingAccount.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(existingAccount);
    }

    /**
     * Update account balance
     */
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    /**
     * Delete account
     */
    public boolean deleteAccount(Long id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();

            // Business rule: Don't allow deletion of accounts with non-zero balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("Cannot delete account with non-zero balance");
            }

            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get total balance for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        return accountRepository.getTotalBalanceByUserId(userId);
    }

    /**
     * Get total balance by user ID and account type
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByUserIdAndType(Long userId, Account.AccountType accountType) {
        return accountRepository.getTotalBalanceByUserIdAndAccountType(userId, accountType);
    }

    /**
     * Get account count
     */
    @Transactional(readOnly = true)
    public long getAccountCount() {
        return accountRepository.count();
    }

    /**
     * Get account count by user ID
     */
    @Transactional(readOnly = true)
    public long getAccountCountByUserId(Long userId) {
        return accountRepository.countByUserId(userId);
    }

    /**
     * Get accounts with low balance
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsWithLowBalance(BigDecimal threshold) {
        return accountRepository.findAccountsWithLowBalance(threshold);
    }

    /**
     * Get recent accounts (created in last N days)
     */
    @Transactional(readOnly = true)
    public List<Account> getRecentAccounts(int days) {
        return accountRepository.findRecentAccounts(days);
    }

    /**
     * Get inactive accounts (not updated in last N days)
     */
    @Transactional(readOnly = true)
    public List<Account> getInactiveAccounts(int days) {
        return accountRepository.findInactiveAccounts(days);
    }

    /**
     * Get accounts by user ID ordered by balance
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUserIdOrderByBalance(Long userId) {
        return accountRepository.findByUserIdOrderByBalanceDesc(userId);
    }

    /**
     * Credit amount to account
     */
    public Account creditAccount(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.credit(amount);
        return accountRepository.save(account);
    }

    /**
     * Debit amount from account
     */
    public Account debitAccount(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        if (!account.debit(amount)) {
            throw new IllegalStateException("Insufficient balance for debit operation");
        }

        return accountRepository.save(account);
    }

    /**
     * Transfer amount between accounts
     */
    public void transferBetweenAccounts(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found with id: " + fromAccountId));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found with id: " + toAccountId));

        if (!fromAccount.debit(amount)) {
            throw new IllegalStateException("Insufficient balance in source account");
        }

        toAccount.credit(amount);

        // Save both accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    /**
     * Get account summary for a user
     */
    @Transactional(readOnly = true)
    public AccountSummaryDto getAccountSummary(Long userId) {
        return accountRepository.getAccountSummaryByUserId(userId);
    }

    // Private helper methods

    private void validateAccountData(Account account) {
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
    }
}