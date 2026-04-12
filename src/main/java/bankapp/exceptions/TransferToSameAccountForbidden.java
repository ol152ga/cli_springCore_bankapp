package bankapp.exceptions;

public class TransferToSameAccountForbidden extends RuntimeException {
    public TransferToSameAccountForbidden() {
        super("Transfer to the same account is forbidden");
    }
}
