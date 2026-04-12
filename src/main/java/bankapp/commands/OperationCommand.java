package bankapp.commands;

import bankapp.UserOperations;

import java.util.Scanner;

public interface OperationCommand {
    void execute(Scanner scanner);
    UserOperations getOperation();
}
