import bankapp.AppConfig;
import bankapp.exceptions.EmptyAccountIds;
import bankapp.repo.AccountStorage;
import bankapp.services.AccountService;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import utils.AccountScenario;
import utils.TestDataFactory;

import java.math.BigDecimal;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CloseAccountTest {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountStorage accountStorage;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    private static final Logger logger = Logger.getLogger(CloseAccountTest.class.getName());

    @BeforeEach
    void setup() {
        accountStorage.clear();
        userService.clear();
    }

    @Test
    @DisplayName("account with default amount can be closed")
    public void shouldCloseAccountWithDefaultAmount(){
        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);

        scenario
                .givenUserWithAccounts(2);
        String accountIdToClose = scenario.getAccountId(1);

        BigDecimal expected = TestDataFactory.norm(defaultAmount.add(defaultAmount));

        scenario
                .whenUserClosesAccount(accountIdToClose)
                .thenUserHasAccounts(1)
                .thenAccountHasBalance(0, expected);
    }

    @Test
    @DisplayName("account with zero amount can be closed")
    public void shouldCloseAccountWithZeroAmount(){
        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);

        scenario
                .givenUserWithAccounts(2);
        String accountIdToClose = scenario.getAccountId(1);
        accountStorage.findById(accountIdToClose).setCurrentAmount(BigDecimal.ZERO);

        scenario
                .whenUserClosesAccount(accountIdToClose)
                .thenUserHasAccounts(1)
                .thenAccountHasBalance(0, defaultAmount);
    }


    @Test
    @DisplayName("exception when account id is null")
    public void accountWithNullIdCanNotBeClosedTest() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> accountService.closeAccount(null)
        );

        assertEquals("accountId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("exception when account id is empty")
    void exceptionWhenEmptyAccountIdTest() {
        assertThrows(
                EmptyAccountIds.class,
                () -> accountService.closeAccount(" ")
        );
    }

}
