import bankapp.exceptions.*;
import bankapp.models.User;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        List<User> userList = new ArrayList<>();
        userService = new UserService();
    }

    @Test
    @DisplayName("user with 1 symbol login can be created")
    public void createUserWithOneSymbolLoginTest(){
        String userLogin = Generator.generate(1);
        User user = userService.createUser(userLogin);

        assertEquals(userLogin, user.getLogin());
        assertNotNull(user.getId());
        assertEquals(1, userService.getAllUserList().size());
        assertEquals(user, userService.getAllUserList().getFirst());
    }

    @Test
    @DisplayName("user with random symbol login can be created")
    public void createUserPositiveTest(){
        Random random = new Random();
        int value = random.nextInt(49) + 2;
        String userLogin = Generator.generate(value);
        User user = userService.createUser(userLogin);

        assertEquals(userLogin, user.getLogin());
        assertNotNull(user.getId());
        assertEquals(1, userService.getAllUserList().size());
        assertEquals(user, userService.getAllUserList().getFirst());
    }

    @Test
    @DisplayName("user can be found by ID")
    public void getUserByIdPositiveTest(){
        String userLogin = Generator.generate(10);
        User user = userService.createUser(userLogin);
        String userId = user.getId();

        User foundUser = userService.getUserByUserId(userId);

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getLogin(), foundUser.getLogin());
        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("exception when creating user with not unique login")
    public void returnExceptionGettingCreatingUserWithNotUniqueLoginTest(){
        String userLogin = Generator.generate(5);
        userService.createUser(userLogin);

        NotUniqueLogin exception =  assertThrows(NotUniqueLogin.class, ()->userService.createUser(userLogin));
        assertEquals("Login " + userLogin + " is not unique", exception.getMessage());
    }

    @Test
    @DisplayName("exception when creating user with empty login")
    public void returnExceptionGettingCreatingUserWithEmptyLoginTest(){
        InvalidLogin exception = assertThrows(InvalidLogin.class, ()->userService.createUser("  "));
        assertEquals("Login should not be empty", exception.getMessage());

        //проверка что юзерлист пустой
        assertThrows(NoUsersInUserList.class, ()-> userService.getAllUserList());
    }

    @Test
    @DisplayName("exception when creating user with null login")
    public void returnExceptionGettingCreatingUserWithNullLoginTest(){
        InvalidLogin exception = assertThrows(InvalidLogin.class, ()->userService.createUser(null));
        assertEquals("Login should not be empty", exception.getMessage());

        assertThrows(NoUsersInUserList.class, ()-> userService.getAllUserList());
    }

    @Test
    @DisplayName("exception when trying to find user with invalid ID")
    public void returnExceptionGettingFindingUserWithInvalidIdTest(){
        String invalidId = Generator.generate(30);
        UserNotFound exception = assertThrows(UserNotFound.class, ()->userService.getUserByUserId(invalidId));
        assertEquals("User not found by ID: " + invalidId, exception.getMessage());

    }

    @Test
    @DisplayName("exception when trying to find user with null ID")
    public void returnExceptionGettingFindingUserWithNullIdTest() {
        InvalidUserId  exception = assertThrows(InvalidUserId.class, () -> userService.getUserByUserId(null));
        assertEquals("User Id can not be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("exception when trying to find user with empty ID")
    public void returnExceptionGettingFindingUserWithEmptyIdTest() {
        InvalidUserId  exception = assertThrows(InvalidUserId.class, () -> userService.getUserByUserId("    "));
        assertEquals("User Id can not be empty or null", exception.getMessage());
    }

    @Test
    @DisplayName("exception when getting empty all users list")
    public void returnExceptionGettingEmptyAllUsersListTest(){
        NoUsersInUserList exception = assertThrows(NoUsersInUserList.class, ()->userService.getAllUserList());
        assertEquals("There are no users found", exception.getMessage());
    }


}
