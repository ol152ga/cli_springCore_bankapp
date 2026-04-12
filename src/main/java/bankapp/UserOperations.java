package bankapp;

import lombok.Getter;

@Getter
public enum UserOperations {
    USER_CREATE("USER_CREATE"),
    SHOW_ALL_USERS("SHOW_ALL_USERS"),
    ACCOUNT_CREATE("ACCOUNT_CREATE"),
    ACCOUNT_CLOSE("ACCOUNT_CLOSE"),
    ACCOUNT_DEPOSIT("ACCOUNT_DEPOSIT"),
    ACCOUNT_TRANSFER("ACCOUNT_TRANSFER"),
    ACCOUNT_WITHDRAW("ACCOUNT_WITHDRAW");


    private final String operation;

    UserOperations(String operation) {
        this.operation = operation;
    }


    }
