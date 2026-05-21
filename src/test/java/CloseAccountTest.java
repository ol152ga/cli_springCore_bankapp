import bankapp.AppConfig;
import bankapp.exceptions.EmptyAccountIds;
import bankapp.models.Account;
import bankapp.models.User;
import bankapp.services.AccountService;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import utils.TestDataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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

    private User user;
    private String userId;
    private Account account1;
    private String account1Id;
    private Account account2;
    private BigDecimal account1Amount;
    private BigDecimal account2Amount;

    private static final Logger logger = Logger.getLogger(TransferTest.class.getName());

    /*
    @BeforeEach
    void setUp() {
        user = TestDataFactory.createUser(userService);
        userId = user.getId();
        List<Account> accountList = TestDataFactory.createAccounts(userId, accountService, 2);
        account1 = accountList.getFirst();
        account1Id = account1.getAccountId();
        account1Amount = account1.getCurrentAmount();
        account2 = accountList.getLast();
        account2Amount = account2.getCurrentAmount();

    }

     */

    @Test
    @DisplayName("account with default amount can be closed")
    public void closeAccountWithDefaultAmountTest() {
        User user = TestDataFactory.createUser(userService);
        List<Account> accounts = TestDataFactory.createAccounts(user.getId(), accountService, 2);

        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);

        BigDecimal normalizedAccount1Amount = TestDataFactory.norm(account1.getCurrentAmount());
        BigDecimal normalizedAccount2Amount = TestDataFactory.norm(account2.getCurrentAmount());

        accountService.closeAccount(account1.getAccountId());

        BigDecimal account2NewAmount = TestDataFactory.norm(account2.getCurrentAmount());
        user.getAccountList()
                .forEach(System.out::println);
        List<Account> accountsList = accountService.
        assertEquals(1, accounts.size());
        assertEquals(normalizedAccount1Amount.add(normalizedAccount2Amount), account2NewAmount);
    }

    @Test
    @DisplayName("account with 0.00 amount can be closed")
    public void closeAccountWithZeroAmountTest() {
        account1.setCurrentAmount(BigDecimal.ZERO);
        BigDecimal normalizedAccount2Amount = account2Amount.setScale(2, RoundingMode.HALF_UP);
        accountService.closeAccount(account1Id);
        BigDecimal account2NewAmount = account2.getCurrentAmount();
        BigDecimal normalizedAccount2NewAmount = account2NewAmount.setScale(2, RoundingMode.HALF_UP);
        user.getAccountList()
                .forEach(System.out::println);
        assertEquals(1, user.getAccountList().size());
        assertEquals(normalizedAccount2Amount, normalizedAccount2NewAmount);
    }

    @Test
    @DisplayName("exception when account id is null")
    public void accountWithNullIdCanNotBeClosedTest() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> accountService.closeAccount(null)
        );

        assertEquals("accountId cannot ne null", ex.getMessage());
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
