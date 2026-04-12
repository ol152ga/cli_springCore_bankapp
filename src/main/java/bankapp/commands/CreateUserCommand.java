package bankapp.commands;

import bankapp.UserOperations;
import bankapp.models.User;
import bankapp.services.UserService;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand{
    private final UserService userService;

    public CreateUserCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter login: ");
        String login = scanner.nextLine();

        User user = userService.createUser(login, new ArrayList<>());
        System.out.println("bankapp.models.User created: " + user);
    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.USER_CREATE;
    }
}
