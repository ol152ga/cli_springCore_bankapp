package bankapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class User {
    private final String id;
    private String login;

}
