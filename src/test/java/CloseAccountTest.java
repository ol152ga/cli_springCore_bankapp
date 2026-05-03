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
import utils.Generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

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

    @BeforeEach
    void setUp() {
        user = userService.createUser(Generator.generate(20), new ArrayList<>());
        userId = user.getId();
        account1 = accountService.createAccount(userId);
        account1Id = account1.getAccountId();
        account1Amount = account1.getCurrentAmount();
        account2 = accountService.createAccount(userId);
        account2Amount = account2.getCurrentAmount();

    }

    @Test
    @DisplayName("account with default amount can be closed")
    public void closeAccountWithDefaultAmountTest() {
        BigDecimal normalizedAccount1Amount = account1Amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal normalizedAccount2Amount = account2Amount.setScale(2, RoundingMode.HALF_UP);
        accountService.closeAccount(account1Id);
        BigDecimal account2NewAmount = account2.getCurrentAmount();
        BigDecimal normalizedAccount2NewAmount = account2NewAmount.setScale(2, RoundingMode.HALF_UP);
        user.getAccountList()
                .forEach(System.out::println);
        assertEquals(1, user.getAccountList().size());
        assertEquals(normalizedAccount1Amount.add(normalizedAccount2Amount), normalizedAccount2NewAmount);
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
