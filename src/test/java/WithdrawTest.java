import bankapp.AppConfig;
import bankapp.exceptions.AccountNotFound;
import bankapp.exceptions.NotEnoughMoney;
import bankapp.exceptions.NullSum;
import bankapp.exceptions.SumLessOrEqualsZero;
import bankapp.models.Account;
import bankapp.models.User;
import bankapp.services.AccountService;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import utils.Generator;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class WithdrawTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;
    private BigDecimal balance;

    private String user1Id;
    private User user1;
    private Account account;
    private String accountId;
    private BigDecimal deposit = BigDecimal.valueOf(500);


    @BeforeEach
    void setUp() {
        user1 = userService.createUser(Generator.generate(20), new ArrayList<>());
        user1Id = user1.getId();
        account = accountService.createAccount(user1Id);
        accountId = account.getAccountId();
        accountService.deposit(deposit, account.getAccountId());
        balance = account.getCurrentAmount();

    }

    @Test
    @DisplayName("amount equals balance can be withrawn")
    public void withdrawAmountEqualBalanceTest() {
        accountService.withdraw(balance, accountId);
        assertEquals(BigDecimal.ZERO, account.getCurrentAmount());
    }

    @Test
    @DisplayName("several withdraws can be made")
    public void severalWithdrawsTest() {
        int count = 5;
        for (int i = 0; i < count; i++) {
            accountService.withdraw(balance.divide(BigDecimal.valueOf(5)), accountId);
        }
        assertEquals(BigDecimal.ZERO, account.getCurrentAmount());
    }

    @Test
    @DisplayName("amount over balance can not be withrawn")
    public void exceptionWhenTryWithdrawAmountOverBalanceTest() {
        assertThrows(NotEnoughMoney.class,
                () -> accountService.withdraw(balance.add(new BigDecimal("0.01")), accountId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01"})
    @DisplayName("amount 0 or less can not be withrawn")
    public void exceptionWhenTryWithdrawZeroAmountTest(String amountStr) {
        BigDecimal amount = new BigDecimal(amountStr);
        assertThrows(SumLessOrEqualsZero.class,
                () -> accountService.withdraw(amount, accountId));
    }

    @Test
    @DisplayName("null amount can not be withrawn")
    public void exceptionWhenTryWithdrawNullAmountTest() {
        assertThrows(NullSum.class,
                () -> accountService.withdraw(null, accountId));
    }

    @Test
    @DisplayName("amount can not be withrawn from invalid accountId")
    public void exceptionWhenTryWithdrawFromInvalidAccountIdAmountTest() {
        assertThrows(AccountNotFound.class,
                () -> accountService.withdraw(deposit, Generator.generate(5)));
    }

}
