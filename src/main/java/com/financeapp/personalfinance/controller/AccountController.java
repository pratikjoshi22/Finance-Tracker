package com.financeapp.personalfinance.controller;

import com.financeapp.personalfinance.model.Account;
import com.financeapp.personalfinance.dto.AccountSummaryDto;
import com.financeapp.personalfinance.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create a new account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Get all accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    // Get account by account number
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByNumber(accountNumber);
        return account.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    // Get accounts by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get accounts by type
    @GetMapping("/type/{accountType}")
    public ResponseEntity<List<Account>> getAccountsByType(@PathVariable Account.AccountType accountType) {
        List<Account> accounts = accountService.getAccountsByType(accountType);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get accounts by user ID and type
    @GetMapping("/user/{userId}/type/{accountType}")
    public ResponseEntity<List<Account>> getAccountsByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable Account.AccountType accountType) {
        List<Account> accounts = accountService.getAccountsByUserIdAndType(userId, accountType);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Update account
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        try {
            Account updatedAccount = accountService.updateAccount(id, account);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Update account balance
    @PatchMapping("/{id}/balance")
    public ResponseEntity<Account> updateBalance(@PathVariable Long id, @RequestBody BalanceUpdateRequest request) {
        try {
            Account updatedAccount = accountService.updateBalance(id, request.getNewBalance());
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Credit amount to account
    @PutMapping("/{id}/credit")
    public ResponseEntity<Account> creditAccount(@PathVariable Long id, @RequestBody TransactionRequest request) {
        try {
            Account updatedAccount = accountService.creditAccount(id, request.getAmount());
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Debit amount from account
    @PutMapping("/{id}/debit")
    public ResponseEntity<Account> debitAccount(@PathVariable Long id, @RequestBody TransactionRequest request) {
        try {
            Account updatedAccount = accountService.debitAccount(id, request.getAmount());
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Transfer between accounts
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferBetweenAccounts(@RequestBody TransferRequest request) {
        try {
            accountService.transferBetweenAccounts(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount()
            );

            TransferResponse response = new TransferResponse();
            response.setMessage("Transfer completed successfully");
            response.setFromAccountId(request.getFromAccountId());
            response.setToAccountId(request.getToAccountId());
            response.setAmount(request.getAmount());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            boolean deleted = accountService.deleteAccount(id);
            return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                    : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Get total balance for a user
    @GetMapping("/user/{userId}/total-balance")
    public ResponseEntity<BalanceResponse> getTotalBalanceByUserId(@PathVariable Long userId) {
        BigDecimal totalBalance = accountService.getTotalBalanceByUserId(userId);
        BalanceResponse response = new BalanceResponse();
        response.setTotalBalance(totalBalance);
        response.setUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get total balance by user ID and account type
    @GetMapping("/user/{userId}/type/{accountType}/total-balance")
    public ResponseEntity<BalanceResponse> getTotalBalanceByUserIdAndType(
            @PathVariable Long userId,
            @PathVariable Account.AccountType accountType) {
        BigDecimal totalBalance = accountService.getTotalBalanceByUserIdAndType(userId, accountType);
        BalanceResponse response = new BalanceResponse();
        response.setTotalBalance(totalBalance);
        response.setUserId(userId);
        response.setAccountType(accountType);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get accounts ordered by balance for a user
    @GetMapping("/user/{userId}/ordered-by-balance")
    public ResponseEntity<List<Account>> getAccountsByUserIdOrderByBalance(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserIdOrderByBalance(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get accounts with low balance
    @GetMapping("/low-balance")
    public ResponseEntity<List<Account>> getAccountsWithLowBalance(
            @RequestParam(defaultValue = "100.00") BigDecimal threshold) {
        List<Account> accounts = accountService.getAccountsWithLowBalance(threshold);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get recent accounts (created in last N days)
    @GetMapping("/recent")
    public ResponseEntity<List<Account>> getRecentAccounts(
            @RequestParam(defaultValue = "30") int days) {
        List<Account> accounts = accountService.getRecentAccounts(days);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get inactive accounts (not updated in last N days)
    @GetMapping("/inactive")
    public ResponseEntity<List<Account>> getInactiveAccounts(
            @RequestParam(defaultValue = "90") int days) {
        List<Account> accounts = accountService.getInactiveAccounts(days);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Get account summary for a user
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<AccountSummaryDto> getAccountSummary(@PathVariable Long userId) {
        AccountSummaryDto summary = accountService.getAccountSummary(userId);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    // Get account statistics
    @GetMapping("/stats")
    public ResponseEntity<AccountStats> getAccountStats() {
        AccountStats stats = new AccountStats();
        stats.setTotalAccounts(accountService.getAccountCount());
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // Get account count by user
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<UserAccountStats> getAccountCountByUserId(@PathVariable Long userId) {
        UserAccountStats stats = new UserAccountStats();
        stats.setUserId(userId);
        stats.setAccountCount(accountService.getAccountCountByUserId(userId));
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // Inner classes for request/response DTOs
    public static class BalanceUpdateRequest {
        private BigDecimal newBalance;

        public BigDecimal getNewBalance() {
            return newBalance;
        }

        public void setNewBalance(BigDecimal newBalance) {
            this.newBalance = newBalance;
        }
    }

    public static class TransactionRequest {
        private BigDecimal amount;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class TransferRequest {
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;

        public Long getFromAccountId() {
            return fromAccountId;
        }

        public void setFromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
        }

        public Long getToAccountId() {
            return toAccountId;
        }

        public void setToAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class TransferResponse {
        private String message;
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getFromAccountId() {
            return fromAccountId;
        }

        public void setFromAccountId(Long fromAccountId) {
            this.fromAccountId = fromAccountId;
        }

        public Long getToAccountId() {
            return toAccountId;
        }

        public void setToAccountId(Long toAccountId) {
            this.toAccountId = toAccountId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class BalanceResponse {
        private Long userId;
        private BigDecimal totalBalance;
        private Account.AccountType accountType;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public BigDecimal getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(BigDecimal totalBalance) {
            this.totalBalance = totalBalance;
        }

        public Account.AccountType getAccountType() {
            return accountType;
        }

        public void setAccountType(Account.AccountType accountType) {
            this.accountType = accountType;
        }
    }

    public static class AccountStats {
        private long totalAccounts;

        public long getTotalAccounts() {
            return totalAccounts;
        }

        public void setTotalAccounts(long totalAccounts) {
            this.totalAccounts = totalAccounts;
        }
    }

    public static class UserAccountStats {
        private Long userId;
        private long accountCount;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public long getAccountCount() {
            return accountCount;
        }

        public void setAccountCount(long accountCount) {
            this.accountCount = accountCount;
        }
    }

}