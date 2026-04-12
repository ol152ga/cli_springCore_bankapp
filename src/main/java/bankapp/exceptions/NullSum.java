package bankapp.exceptions;

public class NullSum extends RuntimeException {
    public NullSum() {
        super("Sum can not be null");
    }
}
