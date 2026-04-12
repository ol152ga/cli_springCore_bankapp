package bankapp.exceptions;

public class SumLessOrEqualsZero extends RuntimeException {
    public SumLessOrEqualsZero() {
        super("Amount must be greater than zero");
    }
}
