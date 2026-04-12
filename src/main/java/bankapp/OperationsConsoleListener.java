package bankapp;
import bankapp.commands.OperationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class OperationsConsoleListener implements Runnable {

    private final Map<UserOperations, OperationCommand> commandMap = new HashMap<>();

    //Spring автоматически: находит ВСЕ реализации OperationCommand
    //кладёт их в List - это collection injection
    @Autowired
    public OperationsConsoleListener(List<OperationCommand> commands) {
        commands.forEach(command -> commandMap.put(command.getOperation(), command));
    }


    @Override
    public void run() {
        //Открываем поток для чтения ввода из консоли.
        Scanner scanner = new Scanner(System.in);

        //Бесконечный цикл - программа работает постоянно, пока ты её не остановишь вручную (Ctrl+C).
        while (true) {
            //Показ доступных операций
            printOperations();
            System.out.print("Enter operation type: ");

            //Чтение ввода пользователя - читаем строку, убираем пробелы, приводим к формату enum
            String input = scanner.nextLine().trim().toUpperCase();

            try {
                UserOperations op = UserOperations.valueOf(input);

                //Берём нужную команду из Map
                OperationCommand command = commandMap.get(op);

                //На случай, если команда не зарегистрирована
                if (command == null) {
                    System.out.println("No command found for operation: " + input);
                    continue;
                }
                command.execute(scanner); // Передаем scanner для считывания аргументов
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid operation type: " + input);
            } catch (Exception e) {
                System.out.println("Error executing operation: " + e.getMessage());
            }
        }
    }

    private void printOperations() {
        System.out.println("\nPlease enter one of operation type:");
        for (UserOperations op : UserOperations.values()) {
            System.out.println("-" + op.name());
        }
    }
}