package bankapp.commands;

import bankapp.UserOperations;
import bankapp.services.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;
@Component
public class AccountWithdrawCommand implements OperationCommand{
    private final AccountService accountService;

    public AccountWithdrawCommand(AccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter account ID to withdraw from: ");
        String accountId = scanner.next();
        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = scanner.nextBigDecimal();

        accountService.withdraw(amount, accountId);
        System.out.println("Amount " + amount + " withdrawn from account ID " + accountId);

    }

    @Override
    public UserOperations getOperation() {
        return UserOperations.ACCOUNT_WITHDRAW;
    }
}
