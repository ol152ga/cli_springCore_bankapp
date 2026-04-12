import bankapp.exceptions.InvalidLogin;
import bankapp.exceptions.NoUsersInUserList;
import bankapp.exceptions.NotUniqueLogin;
import bankapp.exceptions.UserNotFound;
import bankapp.models.User;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.Generator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        List<User> userList = new ArrayList<>();
        userService = new UserService(userList);
    }

    @Test
    @DisplayName("user with 1 symbol login can be created")
    public void createUserPositiveTest(){
        String userLogin = Generator.generate(1);
        User user = userService.createUser(userLogin, new ArrayList<>());

        assertEquals(userLogin, user.getLogin());
        assertNotNull(user.getId());
        assertEquals(1, userService.getAllUserList().size());
        assertEquals(user, userService.getAllUserList().getFirst());
    }

    @Test
    @DisplayName("user can be found by ID")
    public void getUserByIdPositiveTest(){
        String userLogin = Generator.generate(10);
        User user = userService.createUser(userLogin, new ArrayList<>());
        String userId = user.getId();

        User foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getLogin(), foundUser.getLogin());
        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("exception when creating user with not unique login")
    public void returnExceptionGettingCreatingUserWithNotUniqueLoginTest(){
        String userLogin = Generator.generate(5);
        userService.createUser(userLogin, new ArrayList<>());

        assertThrows(NotUniqueLogin.class, ()->userService.createUser(userLogin, new ArrayList<>()));
    }

    @Test
    @DisplayName("exception when creating user with empty login")
    public void returnExceptionGettingCreatingUserWithEmptyLoginTest(){
        assertThrows(InvalidLogin.class, ()->userService.createUser("  ", new ArrayList<>()));
        assertThrows(NoUsersInUserList.class, ()-> userService.getAllUserList());
    }

    @Test
    @DisplayName("exception when creating user with null login")
    public void returnExceptionGettingCreatingUserWithNullLoginTest(){
        assertThrows(InvalidLogin.class, ()->userService.createUser(null, new ArrayList<>()));
        assertThrows(NoUsersInUserList.class, ()-> userService.getAllUserList());
    }

    @Test
    @DisplayName("exception when trying to find user with invalid ID")
    public void returnExceptionGettingFindingUserWithInvalidIdTest(){
        String invalidId = Generator.generate(30);
        assertThrows(UserNotFound.class, ()->userService.getUserById(invalidId));
    }

    @Test
    @DisplayName("exception when getting empty all users list")
    public void returnExceptionGettingEmptyAllUsersListTest(){
        assertThrows(NoUsersInUserList.class, ()->userService.getAllUserList());
    }


}
