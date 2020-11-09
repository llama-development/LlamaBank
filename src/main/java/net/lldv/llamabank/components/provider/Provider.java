package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;

import java.util.function.Consumer;

public class Provider {

    public void connect(LlamaBank instance) {

    }

    public void disconnect(LlamaBank instance) {

    }

    public void createBankAccount(Player owner, Consumer<String> password) {

    }

    public void getBankAccount(String account, Consumer<BankAccount> bankAccount) {

    }

    public void withdrawMoney(String account, Player player, double amount) {

    }

    public void depositMoney(String account, Player player, double amount) {

    }

    public void createBankLog(BankAccount account, BankLog.Action action, String comment) {

    }

    public void deleteAccount(BankAccount account) {

    }

    public void changePassword(BankAccount bankAccount, String password) {

    }

    public String getProvider() {
        return null;
    }

}
