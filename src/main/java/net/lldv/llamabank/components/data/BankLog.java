package net.lldv.llamabank.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankLog {

    private final String account;
    private final String date;
    private final Action action;
    private final String comment;

    public enum Action {
        LOGIN,
        LOGIN_FAIL,
        DEPOSIT,
        WITHDRAW,
        ACCOUNT
    }

}
