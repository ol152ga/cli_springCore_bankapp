import bankapp.AppConfig;
import bankapp.exceptions.InvalidUserId;
import bankapp.exceptions.UserNotFound;
import bankapp.models.Account;
import bankapp.models.User;
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
import utils.Generator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CreatingAccountTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    private String user1Id;
    private User user1;
    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    @BeforeEach
    void setUp() {
        user1 = userService.createUser(Generator.generate(20), new ArrayList<>());
        user1Id = user1.getId();
    }

    @Test
    @DisplayName("account can be created with accountId")
    public void accountCanBeCreatedTest(){
        Account account = accountService.createAccount(user1Id);

        assertEquals(1, user1.getAccountList().size());
        assertNotNull(user1.getAccountList().get(0).getAccountId());
        assertEquals(defaultAmount, account.getCurrentAmount());
    }

    @Test
    @DisplayName("several accounts can be created for one user ID")
    public void severalAccountsCanBeCreatedTest(){
        Random random = new Random();
        int count = random.nextInt(20) + 1;
        for (int i = 0; i < count; i++) {
            accountService.createAccount(user1Id);
        }
        assertEquals(count, user1.getAccountList().size());
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
    }


}
