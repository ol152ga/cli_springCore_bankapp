package bankapp.exceptions;

public class NullAccount extends RuntimeException {
    public NullAccount() {
        super("Account can not be null or empty");
    }
}
