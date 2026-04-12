package bankapp.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String userId) {
        super("User not found by ID: " + userId);
    }
}
