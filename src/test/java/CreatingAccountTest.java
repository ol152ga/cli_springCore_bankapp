import bankapp.AppConfig;
import bankapp.exceptions.InvalidUserId;
import bankapp.exceptions.UserNotFound;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CreatingAccountTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountStorage accountStorage;
    @Autowired
    private UserService userService;

    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    @BeforeEach
    void setUp() {
        accountStorage.clear();
        userService.clear();
    }

    @Test
    @DisplayName("account can be created with default balance")
    public void shouldCreateAccount() {

        new AccountScenario(userService, accountService, accountStorage)
                .givenUser()
                .whenUserCreatesAccounts(1)
                .thenUserHasAccounts(1)
                .thenAccountHasBalance(0, defaultAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    @DisplayName("several accounts can be created for one user ID")
    public void shouldCreateTwoAccountsForUser(int count) {

        new AccountScenario(userService, accountService, accountStorage)
                .givenUser()
                .whenUserCreatesAccounts(count)
                .thenUserHasAccounts(count);
    }

    @Test
    @DisplayName("exception when trying to create account with empty user Id")
    public void accountCanNotBeCreatedWithEmptyUserId(){
        InvalidUserId exception = assertThrows(InvalidUserId.class, ()->accountService.createAccount("   "));
        assertEquals("User Id can not be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("exception when trying to create account with null user Id")
    public void accountCanNotBeCreatedWithNullUserId(){
        InvalidUserId exception = assertThrows(InvalidUserId.class, ()->accountService.createAccount(null));
        assertEquals("User Id can not be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("exception when trying to create account with not existing user Id")
    public void accountCanNotBeCreatedWithNotExistingUserId(){
        String invalidUserId = Generator.generate(10);
        UserNotFound exception = assertThrows(UserNotFound.class, ()->
                accountService.createAccount(invalidUserId));
        assertEquals("User not found by ID: " + invalidUserId, exception.getMessage());
        assertTrue(accountStorage.findByUserId(invalidUserId).isEmpty());
    }

}
