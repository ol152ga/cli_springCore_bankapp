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
        int count = 5;
        for (int i = 0; i < count; i++) {
            accountService.createAccount(user1Id);
        }
        assertEquals(5, user1.getAccountList().size());
    }

    @Test
    @DisplayName("exception when trying to create account with empty user Id")
    public void accountCanNotBeCreatedWithEmptyUserId(){
        assertThrows(InvalidUserId.class, ()->accountService.createAccount("   "));
    }

    @Test
    @DisplayName("exception when trying to create account with null user Id")
    public void accountCanNotBeCreatedWithNullUserId(){
        assertThrows(InvalidUserId.class, ()->accountService.createAccount(null));
    }

    @Test
    @DisplayName("exception when trying to create account with not existing user Id")
    public void accountCanNotBeCreatedWithNotExistingUserId(){
        assertThrows(UserNotFound.class, ()->
                accountService.createAccount(Generator.generate(10)));
    }


}
