package net.lldv.llamabank.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BankAccount {

    private final String account;
    private final String owner;
    private final String password;
    private final double balance;
    private final List<BankLog> bankLogs;

}
