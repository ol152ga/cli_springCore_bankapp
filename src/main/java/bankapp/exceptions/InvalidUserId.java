package bankapp.exceptions;

public class InvalidUserId extends RuntimeException {
    public InvalidUserId() {
        super("User Id can not be empty or null");
    }
}
