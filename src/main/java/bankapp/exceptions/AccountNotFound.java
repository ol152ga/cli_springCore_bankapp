package bankapp.exceptions;

public class AccountNotFound extends RuntimeException {
    public AccountNotFound(String accountId) {
        super("Account not found with id: " + accountId);
    }
}
