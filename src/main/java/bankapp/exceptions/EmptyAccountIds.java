package bankapp.exceptions;

public class EmptyAccountIds extends RuntimeException {
    public EmptyAccountIds() {
        super("Account ID(s) can not be empty");
    }
}
