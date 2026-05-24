package bdd_interfaces;

import java.math.BigDecimal;

public interface ThenStage {
    ThenStage thenUserHasAccounts(int expected);
    ThenStage thenAccountHasBalance(int index, BigDecimal expected);

    ThenStage thenThrows(Class<? extends Throwable> expectedException);

    // завершение цепочки
    void execute();
}
