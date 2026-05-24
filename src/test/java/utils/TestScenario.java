package utils;

import bankapp.models.Account;
import bankapp.models.User;

import java.util.List;

public class TestScenario {
    private final User user;
    private final List<Account> accounts;

    public TestScenario(User user, List<Account> accounts) {
        this.user = user;
        this.accounts = accounts;
    }

    public User getUser() {
        return user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
