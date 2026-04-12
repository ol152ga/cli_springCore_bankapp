package bankapp.exceptions;

public class CannotCloseTheOnlyAccount extends RuntimeException {
    public CannotCloseTheOnlyAccount() {
        super("Cannot close the only user account");
    }
}
