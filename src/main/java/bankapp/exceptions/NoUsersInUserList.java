package bankapp.exceptions;

public class NoUsersInUserList extends RuntimeException {
    public NoUsersInUserList() {
        super("There are no users found");
    }
}
