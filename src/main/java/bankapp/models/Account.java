package bankapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@AllArgsConstructor
@Data
@Builder
public class Account {
    private final String accountId;
    private String userId;
    private BigDecimal currentAmount;
}
