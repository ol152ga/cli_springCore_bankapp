package bankapp.commands;

import bankapp.UserOperations;
import bankapp.services.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;
@Component
public class ShowAllUsersCommand implements OperationCommand{
private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(Scanner scanner) {
        userService.getAllUserList()
                .forEach(System.out::println);
    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.SHOW_ALL_USERS;
    }
}
