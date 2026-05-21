package bankapp.services;

import bankapp.exceptions.*;
import bankapp.models.Account;
import bankapp.repo.AccountStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountStorage accountStorage;

    @Value("${account.default-amount}")
    private BigDecimal defaultAmount;

    @Value("${account.transfer-commission}")
    private BigDecimal commission;

    private static final Logger logger = Logger.getLogger(AccountService.class.getName());

    public Account createAccount(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUserId();
        }

        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .userId(userId)
                .currentAmount(defaultAmount)
                .build();

        accountStorage.update(account);
        return account;
    }

    public void deposit(BigDecimal sum, String accountId) {
        if (sum == null) {
            throw new NullSum();
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SumLessOrEqualsZero();
        }
        BigDecimal normalizedSum = sum.setScale(2, RoundingMode.HALF_UP);

        Account account = accountStorage.findById(accountId);

        // Обновляем баланс
        BigDecimal newBalance =
                account.getCurrentAmount().add(normalizedSum);

        account.setCurrentAmount(newBalance.setScale(2, RoundingMode.HALF_UP));

        accountStorage.update(account);

    }

    public void withdraw(BigDecimal sum, String accountId) {
        Account account = accountStorage.findById(accountId);

        if (sum == null) {
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
        BigDecimal normalizedSum = sum.setScale(2, RoundingMode.HALF_UP);

        // Обновляем баланс
        account.setCurrentAmount(
                account.getCurrentAmount()
                .subtract(normalizedSum));

        accountStorage.update(account);
    }

    public void transfer(String senderAccountId, String recipientAccountId, BigDecimal sum) {
        Objects.requireNonNull(senderAccountId, "senderAccountId cannot be null");
        Objects.requireNonNull(recipientAccountId, "recipientAccountId cannot be null");
        Objects.requireNonNull(sum, "Sum cannot be null");

        if (commission == null ||
                commission.compareTo(BigDecimal.ZERO) < 0 ||
                commission.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Invalid commission");
        }

        if (senderAccountId.trim().isEmpty() || recipientAccountId.trim().isEmpty()) {
            throw new EmptyAccountIds();
        }

        Account senderAccount = accountStorage.findById(senderAccountId);
        Account recipientAccount = accountStorage.findById(recipientAccountId);

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SumLessOrEqualsZero();
        }

        if (senderAccountId.equals(recipientAccountId)) {
            throw new TransferToSameAccountForbidden();
        }

        BigDecimal normalizedSum = sum.setScale(2, RoundingMode.HALF_UP);

        BigDecimal fee = normalizedSum
                .multiply(commission)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal sumWithComission = normalizedSum.add(fee);

        if (senderAccount.getCurrentAmount().compareTo(sumWithComission) < 0) {
            throw new NotEnoughMoney(senderAccountId);
        }
        BigDecimal newSenderBalance = senderAccount.getCurrentAmount().subtract(sumWithComission);
        BigDecimal newRecipientBalance = recipientAccount.getCurrentAmount().add(normalizedSum);

        senderAccount.setCurrentAmount(newSenderBalance);
        recipientAccount.setCurrentAmount(newRecipientBalance);

        accountStorage.update(senderAccount);
        accountStorage.update(recipientAccount);
    }

    public void closeAccount(String accountId) {
        Objects.requireNonNull(accountId, "accountId cannot be null");

        if (accountId.trim().isEmpty()) {
            throw new EmptyAccountIds();
        }

        Account account = accountStorage.findById(accountId);

        String userId = account.getUserId();

        List<Account> accountsUserList = accountStorage.findByUserId(userId);

        if (accountsUserList.size() == 1) {
            throw new CannotCloseTheOnlyAccount();
        }

        // Находим другой (первый) счет пользователя
        Account anotherAccount = accountsUserList.stream()
                .filter(ac -> !ac.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new NoSecondAccountFound(accountId));

        String anotherAccountId = anotherAccount.getAccountId();

        // Переводим баланс закрываемого счета на другой
        BigDecimal balanceToTransfer = account.getCurrentAmount().setScale(2, RoundingMode.HALF_UP);
        if (balanceToTransfer.compareTo(BigDecimal.ZERO) > 0) {
            anotherAccount.setCurrentAmount(
                    anotherAccount.getCurrentAmount().add(balanceToTransfer));
            account.setCurrentAmount(BigDecimal.ZERO);
            accountStorage.update(anotherAccount);
            accountStorage.update(account);
        }

        logger.info("amount " + balanceToTransfer + " is transferred from account ID "
                + account.getAccountId() + " to account ID " + anotherAccountId);
        logger.info("account ID " + anotherAccountId + " balance is "
                + anotherAccount.getCurrentAmount());
        logger.info("account ID " + accountId + " balance is "
                + account.getCurrentAmount());

        // Удаляем закрываемый счет
        accountStorage.remove(account);
        logger.info("account ID " + accountId + " is removed");

    }

}
