package bankapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class User {
    private final String id;
    private String login;
    private List<Account> accountList;

}
