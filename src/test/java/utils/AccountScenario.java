package utils;

import bankapp.models.Account;
import bankapp.models.User;
import bankapp.repo.AccountStorage;
import bankapp.services.AccountService;
import bankapp.services.UserService;
import bdd_interfaces.GivenStage;
import bdd_interfaces.ThenStage;
import bdd_interfaces.WhenStage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountScenario implements GivenStage, WhenStage, ThenStage {

    private final UserService userService;
    private final AccountService accountService;
    private final AccountStorage accountStorage;
    private Throwable thrownException;

    private User user;
    private final List<Account> accounts = new ArrayList<>();

    public AccountScenario(UserService userService,
                           AccountService accountService,
                           AccountStorage accountStorage) {
        this.userService = userService;
        this.accountService = accountService;
        this.accountStorage = accountStorage;

    }



    public String getAccountId(int index) {
        return accountStorage.findByUserId(user.getId())
                .get(index)
                .getAccountId();
    }


    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (Throwable ex) {
            this.thrownException = ex;
        }
    }

    public ThenStage whenAction(Runnable action) {
        runAction(action);
        return this;
    }


    @Override
    public WhenStage givenUser() {
        this.user = userService.createUser(Generator.generate(10));
        return this;
    }

    @Override
    public WhenStage givenUserWithAccounts(int count) {
        this.user = userService.createUser(Generator.generate(10));
        for (int i = 0; i < count; i++) {
            Account account =
                    accountService.createAccount(user.getId());

            accounts.add(account);
        }
        return this;
    }

    @Override
    public ThenStage whenUserCreatesAccounts(int count) {
        for (int i = 0; i < count; i++) {
            Account account =
                    accountService.createAccount(user.getId());

            accounts.add(account);
        }
        return this;
    }

    @Override
    public ThenStage whenUserClosesAccount(String accountId) {
        accountService.closeAccount(accountId);
        return this;
    }

    @Override
    public ThenStage whenUserDeposit(String accountId,
                                     BigDecimal sum,
                                     int times) {

        try {
            for (int i = 0; i < times; i++) {
                accountService.deposit(sum, accountId);
            }

        } catch (Throwable e) {
            this.thrownException = e;
        }

        return this;
    }

    @Override
    public ThenStage thenUserHasAccounts(int expected) {
        int actual = accountStorage.findByUserId(user.getId()).size();
        assertEquals(expected, actual);

        return this;
    }

    @Override
    public ThenStage thenAccountHasBalance(int index, BigDecimal expected) {
        assertEquals(expected, accounts.get(index).getCurrentAmount());
        return this;
    }

    @Override
    public ThenStage thenThrows(Class<? extends Throwable> expected) {

        assertNotNull(
                thrownException,
                "Expected exception but nothing was thrown"
        );

        assertEquals(expected, thrownException.getClass());

        return this;
    }

    @Override
    public void execute() {
        // финальный checkpoint
    }




}
