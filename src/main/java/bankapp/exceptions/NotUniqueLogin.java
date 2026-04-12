package bankapp.exceptions;

public class NotUniqueLogin extends RuntimeException {
    public NotUniqueLogin(String login) {
        super("Login " + login + " is not unique");
    }
}
