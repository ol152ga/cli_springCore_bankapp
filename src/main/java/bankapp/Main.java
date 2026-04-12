package bankapp;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

//Spring:
//Читает AppConfig
//Начинает строить контекст (ApplicationContext)
// Запуск логики = OperationsConsoleListener listener

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        OperationsConsoleListener listener =
                context.getBean(OperationsConsoleListener.class);

        listener.run();
    }
}
