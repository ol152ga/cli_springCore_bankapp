package bankapp.commands;

import bankapp.models.Account;
import bankapp.services.AccountService;
import bankapp.UserOperations;
import bankapp.services.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class CreateAccountCommand implements OperationCommand {
    private final AccountService accountService;
    private final UserService userService;


    public CreateAccountCommand(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter the user id for which to create an account: ");
        String userId = scanner.next();

        Account account = accountService.createAccount(userId);
        System.out.println("New account created with ID: " + account.getAccountId() +
                " for user: " + userService.getUserById(userId));
    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.ACCOUNT_CREATE;
    }
}
