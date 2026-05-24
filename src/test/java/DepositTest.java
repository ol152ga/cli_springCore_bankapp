import bankapp.AppConfig;
import bankapp.exceptions.AccountNotFound;
import bankapp.exceptions.NullAccount;
import bankapp.exceptions.NullSum;
import bankapp.exceptions.SumLessOrEqualsZero;
import bankapp.models.Account;
import bankapp.models.User;
import bankapp.repo.AccountStorage;
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
import utils.AccountScenario;
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
    @Autowired
    private AccountStorage accountStorage;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    private String user1Id;
    private User user1;
    private Account account;

    BigDecimal deposit = BigDecimal.valueOf(500);


    @BeforeEach
    void setUp() {
        accountStorage.clear();
        userService.clear();
    }

    @Test
    @DisplayName("valid amount can be deposited")
    public void shouldDepositValidSum(){
        BigDecimal expected = TestDataFactory.norm(defaultAmount.add(deposit));

        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);
        scenario
                .givenUserWithAccounts(1);

        String accountId = scenario.getAccountId(0);

        scenario
                .whenUserDeposit(accountId, deposit, 1)
                .thenAccountHasBalance(0, expected);

    }


    @Test
    @DisplayName("0.01 amount can be deposited")
    public void minSumCanBeDepositedTest(){
        BigDecimal value = new BigDecimal("0.01");
        BigDecimal expected = TestDataFactory.norm(defaultAmount.add(value));

        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);
        scenario
                .givenUserWithAccounts(1);

        String accountId = scenario.getAccountId(0);
        scenario
                .whenUserDeposit(accountId, value, 1)
                .thenAccountHasBalance(0, expected);
    }

    @Test
    @DisplayName("valid amount can be deposited several times")
    public void validSumCanBeDepositedSeveralTimesTest(){
        int times = 4;
        BigDecimal expected = TestDataFactory.norm(defaultAmount.add(deposit.multiply(BigDecimal.valueOf(times))));

        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);
        scenario
                .givenUserWithAccounts(1);

        String accountId = scenario.getAccountId(0);

        scenario
                .whenUserDeposit(accountId, deposit, times)
                .thenAccountHasBalance(0, expected);
    }

    @Test
    @DisplayName("negative amount can not be deposited")
    public void negativeSumCanNotBeDepositedTest(){

        BigDecimal negative = BigDecimal.valueOf(Math.random() * 1000).negate();

        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);

        scenario
                .givenUserWithAccounts(1);

        String accountId = scenario.getAccountId(0);
        scenario
                .whenUserDeposit(accountId, negative, 1)
                .thenThrows(SumLessOrEqualsZero.class);

    }

    @Test
    @DisplayName("0 amount can not be deposited")
    public void zeroSumCanNotBeDepositedTest(){
        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);
        scenario
                .givenUserWithAccounts(1);
        User user = userService.getAllUserList().get(0);
        String accountId = String.valueOf(accountStorage.findByUserId(user.getId()).get(0));

        scenario
                .whenUserDeposit(accountId, BigDecimal.ZERO, 1)
                .thenThrows(SumLessOrEqualsZero.class);

    }

    @Test
    @DisplayName("null amount can not be deposited")
    public void nullSumCanNotBeDepositedTest(){
        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);

        scenario
                .givenUserWithAccounts(1);
        User user = userService.getAllUserList().get(0);
        String accountId = String.valueOf(accountStorage.findByUserId(user.getId()).get(0));
        scenario
                .whenUserDeposit(accountId, null, 1)
                .thenThrows(NullSum.class);

    }

    @Test
    @DisplayName("amount can not be deposited to invalid account ID")
    public void sumCanNotBeDepositedToInvalidAccountIdTest(){
        AccountScenario scenario = new AccountScenario(userService, accountService, accountStorage);

        scenario
                .givenUserWithAccounts(1)
                .whenUserDeposit(Generator.generate(5), deposit, 1)
                .thenThrows(AccountNotFound.class);

    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   ", "\t"})
    @DisplayName("amount can not be deposited to empty account ID")
    public void sumCanNotBeDepositedToEmptyAccountIdTest(String accountId){
        assertThrows(AccountNotFound.class, ()->
                accountService.deposit(deposit, accountId));
    }

    @Test
    @DisplayName("amount can not be deposited to null account ID")
    public void sumCanNotBeDepositedToNullAccountIdTest(){
        assertThrows(AccountNotFound.class, ()->
                accountService.deposit(deposit, null));
    }

}
