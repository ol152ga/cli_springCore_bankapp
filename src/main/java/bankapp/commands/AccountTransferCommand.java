package bankapp.commands;

import bankapp.UserOperations;
import bankapp.services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;
@Component
public class AccountTransferCommand implements OperationCommand{
    private final AccountService accountService;

    public AccountTransferCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Note: commission will be applied");
        System.out.print("Enter source account ID: ");
        String sourceAccountId = scanner.next();
        System.out.print("Enter target account ID: ");
        String targetAccountId = scanner.next();
        System.out.print("Enter amount to transfer: ");
        BigDecimal amount = scanner.nextBigDecimal();

        accountService.transfer(sourceAccountId, targetAccountId, amount);
        System.out.println("Amount " + amount + " transferred from account ID " +
                sourceAccountId + " to account ID " + targetAccountId);


    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.ACCOUNT_TRANSFER;
    }
}
