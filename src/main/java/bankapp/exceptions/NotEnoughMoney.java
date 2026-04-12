package bankapp.exceptions;

public class NotEnoughMoney extends RuntimeException {
    public NotEnoughMoney(String accountId) {
        super("Not enough money on account ID: " + accountId);
    }
}
