package bankapp.commands;

import bankapp.UserOperations;
import bankapp.services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;
@Component
public class AccountDepositCommand implements OperationCommand{
    private final AccountService accountService;

    public AccountDepositCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter account ID: ");
        String accountId = scanner.next();
        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = scanner.nextBigDecimal();
        accountService.deposit(amount, accountId);
        System.out.println("Amount "+ amount + " deposited to account ID: " + accountId);

    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.ACCOUNT_DEPOSIT;
    }
}
