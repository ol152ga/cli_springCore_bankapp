package bankapp.repo;

import bankapp.exceptions.AccountNotFound;
import bankapp.models.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccountStorage {

    private final List<Account> accounts = new ArrayList<>();

    public void update(Account account) {
        remove(account);
        accounts.add(account);
    }

    public void remove(Account account) {
        accounts.remove(account);
    }

    public List<Account> findAll() {
        return List.copyOf(accounts);
    }

    public Account findById(String accountId) {
        return accounts.stream()
                .filter(a -> a.getAccountId().equalsIgnoreCase(accountId))
                .findFirst()
                .orElseThrow(() -> new AccountNotFound(accountId));
    }

    public List<Account> findByUserId(String userId){
        return accounts.stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }

    public void clear() {
        accounts.clear();
    }
}
