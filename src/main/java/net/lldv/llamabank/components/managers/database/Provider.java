package net.lldv.llamabank.components.managers.database;

import cn.nukkit.player.Player;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.EventLog;
import net.lldv.llamabank.components.data.LogType;

import java.util.List;

public class Provider {

    public void connect(LlamaBank server) {

    }

    public void disconnect(LlamaBank server) {

    }

    public void createBankAccount(Player player) {

    }

    public void createBankLog(Player player, String account, LogType logType, double amount) {

    }

    public void createBankLog(Player player, String account, LogType logType) {

    }

    public void giveBankCard(Player player, String account) {

    }

    public void withdrawMoney(Player player, String account, double amount) {

    }

    public void depositMoney(Player player, String account, double amount) {

    }

    public void setMoney(String account, double amount) {

    }

    public void addMoney(String account, double amount) {

    }

    public void removeMoney(String account, double amount) {

    }

    public BankAccount getBankAccount(String account) {
        return null;
    }

    public List<EventLog> getBankLog(String account) {
        return null;
    }

    public String getProvider() {
        return null;
    }

}
