package bankapp.commands;

import bankapp.services.AccountService;
import bankapp.UserOperations;
import org.springframework.stereotype.Component;

import java.util.Scanner;
@Component
public class AccountCloseCommand implements OperationCommand {
    private final AccountService accountService;

    public AccountCloseCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter account ID to close: ");
        String accountId = scanner.next();

        accountService.closeAccount(accountId);
        System.out.println("Account " + accountId + " successfully closed.");
    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.ACCOUNT_CLOSE;
    }
}
