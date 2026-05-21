package utils;

import bankapp.models.Account;
import bankapp.models.User;
import bankapp.services.AccountService;
import bankapp.services.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static List<Account> createAccounts(
            String userId,
            AccountService accountService,
            int accountsCount) {

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < accountsCount; i++) {
            accounts.add(accountService.createAccount(userId));
        }

        return accounts;
        }

    public static Account createAccount(
            String userId,
            AccountService accountService) {

        return accountService.createAccount(userId);
    }


    public static User createUser(UserService userService){
        User user = userService.createUser(
                Generator.generate(20),
                new ArrayList<>());

        return user;

    }

    public static BigDecimal norm(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
