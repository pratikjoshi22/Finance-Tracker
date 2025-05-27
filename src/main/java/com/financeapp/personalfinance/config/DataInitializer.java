package com.financeapp.personalfinance.config;

import com.financeapp.personalfinance.model.Account;
import com.financeapp.personalfinance.model.User;
import com.financeapp.personalfinance.service.AccountService;
import com.financeapp.personalfinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("development") // Only run in development profile
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 DataInitializer is running...");
        initializeTestData();
    }

    private void initializeTestData() {
        System.out.println("🌱 Initializing test data...");

        try {
            // Create test users
            User user1 = new User("John", "Doe", "john.doe@email.com", "+1-555-0101");
            User user2 = new User("Jane", "Smith", "jane.smith@email.com", "+1-555-0102");
            User user3 = new User("Bob", "Johnson", "bob.johnson@email.com", "+1-555-0103");

            user1 = userService.createUser(user1);
            user2 = userService.createUser(user2);
            user3 = userService.createUser(user3);

            System.out.println("✅ Created " + userService.getUserCount() + " test users");

            // Create test accounts for user1
            Account checking1 = new Account("Main Checking", "CHK-001", Account.AccountType.CHECKING, user1.getId());
            checking1.setBalance(new BigDecimal("2500.00"));

            Account savings1 = new Account("Emergency Fund", "SAV-001", Account.AccountType.SAVINGS, user1.getId());
            savings1.setBalance(new BigDecimal("10000.00"));

            Account credit1 = new Account("Main Credit Card", "CC-001", Account.AccountType.CREDIT_CARD, user1.getId());
            credit1.setBalance(new BigDecimal("-1250.75"));

            // Create test accounts for user2
            Account checking2 = new Account("Primary Checking", "CHK-002", Account.AccountType.CHECKING, user2.getId());
            checking2.setBalance(new BigDecimal("3200.50"));

            Account investment2 = new Account("Investment Portfolio", "INV-001", Account.AccountType.INVESTMENT, user2.getId());
            investment2.setBalance(new BigDecimal("25000.00"));

            // Create test accounts for user3
            Account savings3 = new Account("Vacation Fund", "SAV-002", Account.AccountType.SAVINGS, user3.getId());
            savings3.setBalance(new BigDecimal("5500.25"));

            // Save accounts
            accountService.createAccount(checking1);
            accountService.createAccount(savings1);
            accountService.createAccount(credit1);
            accountService.createAccount(checking2);
            accountService.createAccount(investment2);
            accountService.createAccount(savings3);

            System.out.println("✅ Created " + accountService.getAccountCount() + " test accounts");

            // Print summary
            System.out.println("\n📊 Test Data Summary:");
            System.out.println("Users: " + userService.getUserCount());
            System.out.println("Accounts: " + accountService.getAccountCount());
            System.out.println("\n🔗 Try these endpoints:");
            System.out.println("GET  http://localhost:8080/api/v1/users");
            System.out.println("GET  http://localhost:8080/api/v1/accounts");
            System.out.println("GET  http://localhost:8080/api/v1/accounts/user/1");
            System.out.println("GET  http://localhost:8080/api/v1/users/1");
            System.out.println("POST http://localhost:8080/api/v1/users");

        } catch (Exception e) {
            System.err.println("❌ Error initializing test data: " + e.getMessage());
        }
    }

}