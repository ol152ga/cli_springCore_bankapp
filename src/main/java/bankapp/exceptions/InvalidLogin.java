package bankapp.exceptions;

public class InvalidLogin extends RuntimeException {
    public InvalidLogin() {
        super("Login should not be empty");
    }
}
