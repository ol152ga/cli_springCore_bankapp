import bankapp.AppConfig;
import bankapp.exceptions.*;
import bankapp.models.Account;
import bankapp.models.User;
import bankapp.services.AccountService;
import bankapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import utils.Generator;
import utils.TestDataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class TransferTest {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    private User sender;
    private User recipient;
    private String senderId;
    private String recipientId;
    private Account senderAccount;
    private Account recipientAccount;
    private String senderAccountId;
    private String recipientAccountId;
    private BigDecimal deposit = BigDecimal.valueOf(1000);
    private BigDecimal senderBalance;
    private BigDecimal recipientBalance;
    @Value("${account.transfer-commission}")
    private BigDecimal commission;

    private static final Logger logger = Logger.getLogger(TransferTest.class.getName());

    @BeforeEach
    void setUp() {
        sender = TestDataFactory.createUser(userService);
        senderId = sender.getId();
        senderAccount = TestDataFactory.createAccounts(senderId, accountService, 1).getFirst();
        senderAccountId = senderAccount.getAccountId();
        accountService.deposit(deposit, senderAccountId);
        senderBalance = accountService
                .getAccountById(senderAccountId)
                .getCurrentAmount();

        recipient = TestDataFactory.createUser(userService);
        recipientId = recipient.getId();
        recipientAccount = TestDataFactory.createAccounts(recipientId, accountService, 1).getFirst();
        recipientAccountId = recipientAccount.getAccountId();
        recipientBalance = accountService
                .getAccountById(recipientAccountId)
                .getCurrentAmount();

    }

    @Test
    @DisplayName("amount less balance can be transferred to another user account")
    public void transferLessBalanceCanBeMadeTest() {
        BigDecimal percent = commission;

        BigDecimal transferAmount = senderBalance
                .subtract(new BigDecimal("0.01"))
                .divide(BigDecimal.ONE.add(percent), 2, RoundingMode.DOWN);

        BigDecimal normalizedSum = transferAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = normalizedSum
                .multiply(percent)
                .setScale(2, RoundingMode.HALF_UP);

        logger.info("senderBalance = " + senderBalance);
        logger.info("recipientBalance = " + recipientBalance);
        logger.info("transferAmount + fee = " + normalizedSum.add(fee));

        accountService.transfer(senderAccountId, recipientAccountId, normalizedSum);

        Account updatedSender = accountService.getAccountById(senderAccountId);
        Account updatedRecipient = accountService.getAccountById(recipientAccountId);

        BigDecimal expectedSender = senderBalance.subtract(normalizedSum).subtract(fee);
        BigDecimal expectedRecipient = recipientBalance.add(normalizedSum);
        BigDecimal finalSenderAccount = updatedSender.getCurrentAmount();
        BigDecimal finalRecipientAccount = updatedRecipient.getCurrentAmount();

        logger.info("finalSenderAccountSum = " + finalSenderAccount);
        logger.info("finalRecipientAccountSum = " + finalRecipientAccount);

        assertThat(finalSenderAccount).isEqualByComparingTo(expectedSender);
        assertThat(finalRecipientAccount).isEqualByComparingTo(expectedRecipient);
    }
    @Test
    @DisplayName("amount less balance can be transferred to the same user account")
    public void transferLessBalanceToSameUserAccountCanBeMadeTest() {
        BigDecimal percent = commission;

        BigDecimal transferAmount = senderBalance
                .subtract(new BigDecimal("0.01"))
                .divide(BigDecimal.ONE.add(percent), 2, RoundingMode.DOWN);

        BigDecimal normalizedSum = transferAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = normalizedSum
                .multiply(percent)
                .setScale(2, RoundingMode.HALF_UP);

        Account senderAccount2 = TestDataFactory.createAccount(senderId, accountService);
        String account2Id = senderAccount2.getAccountId();

        logger.info("Account1 balance = " + senderBalance);
        logger.info("Account2 balance = " + recipientBalance);
        logger.info("transferAmount + fee = " + normalizedSum.add(fee));

        accountService.transfer(senderAccountId, account2Id, normalizedSum);

        Account updatedAccount1 = accountService.getAccountById(senderAccountId);
        Account updatedAccount2 = accountService.getAccountById(account2Id);

        BigDecimal expectedSender = senderBalance.subtract(normalizedSum).subtract(fee);
        BigDecimal expectedRecipient = recipientBalance.add(normalizedSum);
        BigDecimal finalAccount1Amount = updatedAccount1.getCurrentAmount();
        BigDecimal finalAccount2Amount = updatedAccount2.getCurrentAmount();

        logger.info("final Account1 Amount = " + finalAccount1Amount);
        logger.info("final Account2 Amount = " + finalAccount2Amount);

        assertThat(finalAccount1Amount).isEqualByComparingTo(expectedSender);
        assertThat(finalAccount2Amount).isEqualByComparingTo(expectedRecipient);
    }

    @Test
    @DisplayName("amount equals balance can be transferred")
    public void transferEqualsBalanceCanBeMadeTest() {
        BigDecimal percent = commission;

        BigDecimal transferAmount = senderBalance
                .divide(BigDecimal.ONE.add(percent), 2, RoundingMode.DOWN);

        BigDecimal normalizedSum = transferAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = normalizedSum
                .multiply(percent)
                .setScale(2, RoundingMode.HALF_UP);
        accountService.transfer(senderAccountId, recipientAccountId, normalizedSum);

        logger.info("senderBalance = " + senderBalance);
        logger.info("recipientBalance = " + recipientBalance);
        logger.info("transferAmount + fee = " + normalizedSum.add(fee));

        Account updatedSender = accountService.getAccountById(senderAccountId);
        Account updatedRecipient = accountService.getAccountById(recipientAccountId);

        BigDecimal expectedRecipient = recipientBalance.add(normalizedSum);
        BigDecimal finalSenderAccount = updatedSender.getCurrentAmount();
        BigDecimal finalRecipientAccount = updatedRecipient.getCurrentAmount();

        logger.info("finalSenderAccountSum = " + finalSenderAccount);
        logger.info("finalRecipientAccountSum = " + finalRecipientAccount);

        assertThat(finalSenderAccount).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(finalRecipientAccount).isEqualByComparingTo(expectedRecipient);
    }

    @Test
    @DisplayName("amount 0.01 can be transferred")
    public void minTransferCanBeMadeTest() {
        BigDecimal percent = commission;
        BigDecimal transferAmount = new BigDecimal("0.01").setScale(2, RoundingMode.DOWN);
        BigDecimal fee = transferAmount
                .multiply(percent)
                .setScale(2, RoundingMode.HALF_UP);
        accountService.transfer(senderAccountId, recipientAccountId, transferAmount);

        logger.info("senderBalance = " + senderBalance);
        logger.info("recipientBalance = " + recipientBalance);
        logger.info("transferAmount = " + transferAmount);

        Account updatedSender = accountService.getAccountById(senderAccountId);
        Account updatedRecipient = accountService.getAccountById(recipientAccountId);

        BigDecimal expectedSender = senderBalance.subtract(transferAmount).subtract(fee);
        BigDecimal expectedRecipient = recipientBalance.add(transferAmount);
        BigDecimal finalSenderAccount = updatedSender.getCurrentAmount();
        BigDecimal finalRecipientAccount = updatedRecipient.getCurrentAmount();

        logger.info("finalSenderAccountSum = " + finalSenderAccount);
        logger.info("finalRecipientAccountSum = " + finalRecipientAccount);

        assertThat(finalSenderAccount).isEqualByComparingTo(expectedSender);
        assertThat(finalRecipientAccount).isEqualByComparingTo(expectedRecipient);
    }


    @Test
    @DisplayName("transfer to the same account can not be made")
    public void transferToTheSameAccCanNotBeMadeTest() {
        assertThrows(TransferToSameAccountForbidden.class, () -> accountService
                .transfer(senderAccountId, senderAccountId, new BigDecimal("1.00")));
    }

    @Test
    @DisplayName("amount over balance can not be transferred")
    public void transferOverBalanceCanNotBeMadeTest() {
        BigDecimal percent = commission;

        BigDecimal transferAmount = senderBalance
                .add(new BigDecimal("0.01"))
                .divide(BigDecimal.ONE.add(percent), 2, RoundingMode.DOWN);

        BigDecimal normalizedSum = transferAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = normalizedSum
                .multiply(percent)
                .setScale(2, RoundingMode.HALF_UP);
        logger.info("senderBalance = " + senderBalance);
        logger.info("transferAmount + fee = " + normalizedSum.add(fee));

        assertThrows(NotEnoughMoney.class, () ->
                accountService.transfer(senderAccountId, recipientAccountId, normalizedSum));

    }

    @Test
    @DisplayName("zero amount can not be transferred")
    public void zeroSumTransferCanNotBeMadeTest() {
        assertThrows(SumLessOrEqualsZero.class, () ->
                accountService.transfer(senderAccountId, recipientAccountId, BigDecimal.ZERO));

    }

    @Test
    @DisplayName("negative amount can not be transferred")
    public void negativeSumTransferCanNotBeMadeTest() {
        assertThrows(SumLessOrEqualsZero.class, ()->
                accountService.transfer(senderAccountId, recipientAccountId, new BigDecimal("0.01").negate()));
    }

    @Test
    @DisplayName("transfer can not be made from invalid senderId")
    public void transferFromInvalidSenderAccountIdCanNotBeMadeTest() {
        String invalidAccountId = Generator.generate(10);
        assertThrows(AccountNotFound.class, ()->
                accountService.transfer(invalidAccountId, recipientAccountId, new BigDecimal("1.99")));
    }

    @Test
    @DisplayName("transfer can not be made to invalid recipientAccountId")
    public void transferToInvalidSenderAccountIdCanNotBeMadeTest() {
        String invalidAccountId = Generator.generate(10);
        assertThrows(AccountNotFound.class, ()->
                accountService.transfer(senderAccountId, invalidAccountId, new BigDecimal("10.01")));
    }

    @Test
    @DisplayName("transfer can not be made between invalid AccountIds")
    public void transferBetweenInvalidAccountIdsCanNotBeMadeTest() {
        String invalidAccountId1 = Generator.generate(10);
        String invalidAccountId2 = Generator.generate(10);
        assertThrows(AccountNotFound.class, ()->
                accountService.transfer(invalidAccountId1, invalidAccountId2, new BigDecimal("1.00")));
    }

    @Test
    @DisplayName("null amount can not be transferred")
    public void nullSumTransferCanNotBeMadeTest() {
        assertThrows(SumLessOrEqualsZero.class, ()->
                accountService.transfer(senderAccountId, recipientAccountId, new BigDecimal("0.01").negate()));
    }

    @Test
    @DisplayName("exception when sender id is null")
    void exceptionWhenSenderIdIsNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> accountService.transfer(null, recipientAccountId, new BigDecimal("10"))
        );

        assertEquals("SenderId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("exception when recipient id is null")
    void exceptionWhenRecipientIdIsNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> accountService.transfer(senderId, null, new BigDecimal("10"))
        );

        assertEquals("RecipientId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("exception when sum is null")
    void exceptionWhenSumIsNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> accountService.transfer(senderId, recipientId, null)
        );

        assertEquals("Sum cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("exception when sender id is empty")
    void exceptionWhenEmptySenderIdTest() {
        assertThrows(
                EmptyAccountIds.class,
                () -> accountService.transfer(" ", recipientId, new BigDecimal("10"))
        );
    }

    @Test
    @DisplayName("exception when recipient id is empty")
    void exceptionWhenEmptyRecipientIdTest() {
        assertThrows(
                EmptyAccountIds.class,
                () -> accountService.transfer(senderId, "", new BigDecimal("10"))
        );
    }


}