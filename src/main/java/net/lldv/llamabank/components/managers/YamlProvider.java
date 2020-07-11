package net.lldv.llamabank.components.managers;

import cn.nukkit.player.Player;
import cn.nukkit.utils.Config;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.EventLog;
import net.lldv.llamabank.components.data.LogType;
import net.lldv.llamabank.components.managers.database.Provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class YamlProvider extends Provider {

    Config bankAccounts, bankLog;

    @Override
    public void connect(LlamaBank server) {
        CompletableFuture.runAsync(() -> {
            server.saveResource("/data/bankaccounts.yml");
            server.saveResource("/data/banklog.yml");
            this.bankAccounts = new Config(server.getDataFolder() + "/data/bankaccounts.yml", Config.YAML);
            this.bankLog = new Config(server.getDataFolder() + "/data/banklog.yml", Config.YAML);
            server.getLogger().info("[Configuration] Ready.");
        });
    }

    @Override
    public void createBankAccount(Player player) {
        super.createBankAccount(player);
    }

    @Override
    public void createBankLog(Player player, String account, LogType logType, double amount) {
        super.createBankLog(player, account, logType, amount);
    }

    @Override
    public void createBankLog(Player player, String account, LogType logType) {
        super.createBankLog(player, account, logType);
    }

    @Override
    public void giveBankCard(Player player, String account) {
        super.giveBankCard(player, account);
    }

    @Override
    public void withdrawMoney(Player player, String account, double amount) {
        super.withdrawMoney(player, account, amount);
    }

    @Override
    public void depositMoney(Player player, String account, double amount) {
        super.depositMoney(player, account, amount);
    }

    @Override
    public void setMoney(String account, double amount) {
        super.setMoney(account, amount);
    }

    @Override
    public void addMoney(String account, double amount) {
        super.addMoney(account, amount);
    }

    @Override
    public void removeMoney(String account, double amount) {
        super.removeMoney(account, amount);
    }

    @Override
    public BankAccount getBankAccount(String account) {
        return super.getBankAccount(account);
    }

    @Override
    public List<EventLog> getBankLog(String account) {
        return super.getBankLog(account);
    }

    @Override
    public String getProvider() {
        return "Yaml";
    }

}
