package bdd_interfaces;

import java.math.BigDecimal;

public interface WhenStage {
    ThenStage whenUserCreatesAccounts(int count);
    ThenStage whenUserClosesAccount(String accountId);
    ThenStage whenUserDeposit(String accountId, BigDecimal sum, int times);
}
