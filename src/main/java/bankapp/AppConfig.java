package bankapp;

import bankapp.models.Account;
import bankapp.models.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.ArrayList;
import java.util.List;

/*
т.к. чистый Spring (без Spring Boot).
 @Configuration= здесь описано, как создавать бины
 @ComponentScan("bankapp") - Автоматически находит в пакете "bankapp"  @Component @Service @Repository
 создает их как бины (объекты в контейнере)
 создает зависимости (Dependency Injection)
OperationsConsoleListener
        ↓
List<OperationCommand>
        ↓
AccountTransferCommand
        ↓
AccountService
        ↓
UserService

подставляет значения

 @PropertySource("classpath:application.properties") - Подключает файл
 бин заставляет работать @Value
 PropertySourcesPlaceholderConfigurer - Чтобы Spring мог подставлять значения из application.properties в @Value
 */

@Configuration
@ComponentScan("bankapp")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public List<User> userList() {
        return new ArrayList<>();
    }

    @Bean
    public List<Account> accountList() {
        return new ArrayList<>();
    }
}
