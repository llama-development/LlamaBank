package net.lldv.llamabank.components.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BankAccount {

    private String accountID;
    private String accountOwner;
    private String accountPassword;
    private double balance;
    private List<EventLog> eventLog;

}
