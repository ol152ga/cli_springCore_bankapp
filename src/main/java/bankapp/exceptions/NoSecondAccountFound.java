package bankapp.exceptions;

public class NoSecondAccountFound extends RuntimeException {
    public NoSecondAccountFound(String accountId) {
        super("No second user account found except ID " + accountId);
    }
}
