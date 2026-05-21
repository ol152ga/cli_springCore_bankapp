import bankapp.AppConfig;
import bankapp.exceptions.AccountNotFound;
import bankapp.exceptions.NullAccount;
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
import utils.TestDataFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class DepositTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    private String user1Id;
    private User user1;
    private Account account;

    BigDecimal deposit = BigDecimal.valueOf(500);


    @BeforeEach
    void setUp() {
        user1 = TestDataFactory.createUser(userService);
        user1Id = user1.getId();
        account =TestDataFactory.createAccount(user1Id, accountService);
    }

    @Test
    @DisplayName("valid amount can be deposited")
    public void validSumCanBeDepositedTest(){
        accountService.deposit(deposit, account.getAccountId());

        Account updated = accountService.getAccountById(account.getAccountId());
        assertEquals(
                0,
                defaultAmount.add(deposit)
                        .compareTo(updated.getCurrentAmount())
        );
    }

    @Test
    @DisplayName("0.01 amount can be deposited")
    public void minSumCanBeDepositedTest(){
        BigDecimal value = new BigDecimal("0.01");
        accountService.deposit(value, account.getAccountId());

        Account updated = accountService.getAccountById(account.getAccountId());
        assertEquals(defaultAmount.add(value), updated.getCurrentAmount());
    }

    @Test
    @DisplayName("valid amount can be deposited several times")
    public void validSumCanBeDepositedSeveralTimesTest(){
        BigDecimal deposit = BigDecimal.valueOf(500);
        int count = 5;
        for (int i = 0; i<count; i++){
            accountService.deposit(deposit, account.getAccountId());
        }

        Account updated = accountService.getAccountById(account.getAccountId());
        BigDecimal expected =
                defaultAmount.add(
                        deposit.multiply(BigDecimal.valueOf(count))
                );

        assertEquals(
                0,
                expected.compareTo(updated.getCurrentAmount())
        );
    }

    @Test
    @DisplayName("negative amount can not be deposited")
    public void negativeSumCanNotBeDepositedTest(){
        BigDecimal negative = BigDecimal.valueOf(Math.random() * 1000).negate();
        assertThrows(SumLessOrEqualsZero.class, ()->
                accountService.deposit(negative, account.getAccountId()));
    }

    @Test
    @DisplayName("0 amount can not be deposited")
    public void zeroSumCanNotBeDepositedTest(){
        assertThrows(SumLessOrEqualsZero.class, ()->
                accountService.deposit(BigDecimal.ZERO, account.getAccountId()));
    }

    @Test
    @DisplayName("null amount can not be deposited")
    public void nullSumCanNotBeDepositedTest(){
        assertThrows(NullSum.class, ()->
                accountService.deposit(null, account.getAccountId()));
    }

    @Test
    @DisplayName("amount can not be deposited to invalid account ID")
    public void sumCanNotBeDepositedToInvalidAccountIdTest(){
        assertThrows(AccountNotFound.class, ()->
                accountService.deposit(deposit, Generator.generate(5)));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   ", "\t"})
    @DisplayName("amount can not be deposited to empty account ID")
    public void sumCanNotBeDepositedToEmptyAccountIdTest(String accountId){
        assertThrows(NullAccount.class, ()->
                accountService.deposit(deposit, accountId));
    }

    @Test
    @DisplayName("amount can not be deposited to null account ID")
    public void sumCanNotBeDepositedToNullAccountIdTest(){
        assertThrows(NullAccount.class, ()->
                accountService.deposit(deposit, null));
    }

}
