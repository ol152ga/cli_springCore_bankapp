package bankapp.services;

import bankapp.exceptions.*;
import bankapp.models.Account;
import bankapp.models.User;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/*
Сервис для управления счетами. Содержит методы для создания счета, пополнения и
снятия средств, перевода средств между счетами и закрытия счета.
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final List<Account> accountList;
    private final UserService userService;

    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    @Value("${account.transfer-commission}")
    private BigDecimal comission;

    public Account createAccount(String userId) {
        if(userId == null || userId.trim().isEmpty()){
            throw new InvalidUserId();
        }

        boolean userIdExists = userService.getAllUserList().stream()
                .anyMatch(user -> user.getId().equalsIgnoreCase(userId));
        if(!userIdExists){
            throw new UserNotFound(userId);
        }

        Account account;
        account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .userId(userId)
                .currentAmount(defaultAmount)
                .build();

        accountList.add(account);
    User user = userService.getUserById(userId);
    user.getAccountList().add(account);

    return account;
}

    public void deposit(BigDecimal sum, String accountId) {
        if(sum == null){
            throw new NullSum();
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SumLessOrEqualsZero();
        }

        Account account = getAccountById(accountId);
        if(account == null){
            throw new AccountNotFound(accountId);
        }

        // Обновляем баланс
        account.setCurrentAmount(
                account.getCurrentAmount()
                        .add(sum)
                        .setScale(2, RoundingMode.HALF_UP)
        );

    }

    public void withdraw(BigDecimal sum, String accountId) {
        Account account = getAccountById(accountId);
        if(account == null){
            throw new AccountNotFound(accountId);
        }

        if(sum == null){
            throw new NullSum();
        }

        // Проверка: сумма должна быть положительной
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SumLessOrEqualsZero();
        }

        // Проверка: нельзя снять больше, чем есть на счету
        if (sum.compareTo(account.getCurrentAmount()) > 0) {
            throw new NotEnoughMoney(accountId);
        }

        // Обновляем баланс
        account.setCurrentAmount(account.getCurrentAmount().subtract(sum));

    }

    public void transfer(String senderId, String recipientId, BigDecimal sum) {
        Objects.requireNonNull(senderId, "SenderId cannot be null");
        Objects.requireNonNull(recipientId, "RecipientId cannot be null");
        Objects.requireNonNull(sum, "Sum cannot be null");

        if(senderId.trim().isEmpty()|| recipientId.trim().isEmpty()){
            throw new EmptyAccountIds();
        }

        Account senderAccount = getAccountById(senderId);
        if(senderAccount == null){
            throw new AccountNotFound(senderId);
        }

        Account recipientAccount = getAccountById(recipientId);
        if(recipientAccount == null){
            throw new AccountNotFound(recipientId);
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SumLessOrEqualsZero();
        }

        if (senderId.equals(recipientId)) {
            throw new TransferToSameAccountForbidden();
        }

        BigDecimal normalizedSum = sum.setScale(2, RoundingMode.HALF_UP);

        BigDecimal fee = normalizedSum
                .multiply(comission)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal sumWithComission = normalizedSum.add(fee);

        if (senderAccount.getCurrentAmount()
                .compareTo(sumWithComission) < 0) {
            throw new NotEnoughMoney(senderId);
        }
        // Списываем с отправителя
        withdraw(sumWithComission, senderId);

        // Зачисляем на получателя
        deposit(normalizedSum, recipientId);

    }

    public void closeAccount(String accountId){
        Objects.requireNonNull(accountId, "accountId cannot ne null");

        Account account = getAccountById(accountId);

        String userId =  account.getUserId();
        User user = userService.getUserById(userId);

        if(user.getAccountList().size() == 1){
            throw new CannotCloseTheOnlyAccount();
        }

        // Находим другой (первый) счет пользователя
        Account anotherAccount= user.getAccountList().stream()
                .filter(ac -> !ac.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new NoSecondAccountFound(accountId));


        // Переводим баланс закрываемого счета на другой
        BigDecimal balanceToTransfer =account.getCurrentAmount();
        deposit(balanceToTransfer, anotherAccount.getAccountId());

        // Удаляем закрываемый счет
        accountList.remove(account);
        user.getAccountList().remove(account);

    }

    public Account getAccountById(String accountId){
        if(accountId == null || accountId.trim().isEmpty()){
            throw new NullAccount();
        }
        return (Account) accountList.stream()
                .filter(acc ->acc.getAccountId().equalsIgnoreCase(accountId))
                .findFirst()
                .orElseThrow(()->new AccountNotFound(accountId));

    }


}
