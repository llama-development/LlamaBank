package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
import net.lldv.llamabank.components.event.BankCreateEvent;
import net.lldv.llamabank.components.event.BankDeleteEvent;
import net.lldv.llamabank.components.event.BankDepositEvent;
import net.lldv.llamabank.components.event.BankWithdrawEvent;
import net.lldv.llamabank.components.language.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class YamlProvider extends Provider {

    private Config bankData;

    @Override
    public void connect(LlamaBank instance) {
        instance.saveResource("/data/bank_data.yml");
        this.bankData = new Config(instance.getDataFolder() + "/data/bank_data.yml", Config.YAML);
        instance.getLogger().info("[Configuration] Ready.");
    }

    @Override
    public void createBankAccount(Player owner, Consumer<String> password) {
        String id = this.getRandomIDCode(7);
        String passwordSet = this.getRandomIDCode(4);
        List<String> log = new ArrayList<>();
        this.bankData.set("bankaccount." + id + ".owner", owner.getName());
        this.bankData.set("bankaccount." + id + ".password", passwordSet);
        this.bankData.set("bankaccount." + id + ".balance", (double) 0);
        this.bankData.set("bankaccount." + id + ".log", log);
        this.bankData.save();
        this.bankData.reload();
        this.giveBankCard(owner, id);
        Server.getInstance().getPluginManager().callEvent(new BankCreateEvent(owner));
        password.accept(passwordSet);
    }

    @Override
    public void getBankAccount(String account, Consumer<BankAccount> bankAccount) {
        BankAccount returnAccount = null;
        if (this.bankData.exists("bankaccount." + account)) {
            String owner = this.bankData.getString("bankaccount." + account + ".owner");
            String password = this.bankData.getString("bankaccount." + account + ".password");
            double balance = this.bankData.getDouble("bankaccount." + account + ".balance");
            List<BankLog> logs = new ArrayList<>();
            this.bankData.getStringList("bankaccount." + account + ".log").forEach(log -> {
                String[] data = log.split(":-:");
                BankLog.Action action = BankLog.Action.valueOf(data[0]);
                String comment = data[1];
                logs.add(new BankLog(account, this.getDate(), action, comment));
            });
            returnAccount = new BankAccount(account, owner, password, balance, logs);
        }
        bankAccount.accept(returnAccount);
    }

    @Override
    public void withdrawMoney(String account, String player, double amount, Consumer<Double> d) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() - amount;
            this.bankData.set("bankaccount." + account + ".balance", amountSet);
            this.bankData.save();
            this.bankData.reload();
            this.createBankLog(bankAccount, BankLog.Action.WITHDRAW, Language.getNP("log-withdraw", player, amount, amountSet, this.getDate()));
            d.accept(amountSet);
            Server.getInstance().getPluginManager().callEvent(new BankWithdrawEvent(player, amount, bankAccount));
        });
    }

    @Override
    public void depositMoney(String account, String player, double amount, Consumer<Double> d) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() + amount;
            this.bankData.set("bankaccount." + account + ".balance", amountSet);
            this.bankData.save();
            this.bankData.reload();
            this.createBankLog(bankAccount, BankLog.Action.DEPOSIT, Language.getNP("log-deposit", player, amount, amountSet, this.getDate()));
            d.accept(amountSet);
            Server.getInstance().getPluginManager().callEvent(new BankDepositEvent(player, amount, bankAccount));
        });
    }

    @Override
    public void createBankLog(BankAccount account, BankLog.Action action, String comment) {
        List<String> list = this.bankData.getStringList("bankaccount." + account.getAccount() + ".log");
        list.add(action.name().toUpperCase() + ":-:" + comment);
        this.bankData.set("bankaccount." + account.getAccount() + ".log", list);
        this.bankData.save();
        this.bankData.reload();
    }

    @Override
    public void deleteAccount(BankAccount account) {
        Map<String, Object> map = this.bankData.getSection("bankaccount").getAllMap();
        map.remove(account.getAccount());
        this.bankData.set("bankaccount", map);
        this.bankData.save();
        this.bankData.reload();
        Server.getInstance().getPluginManager().callEvent(new BankDeleteEvent(account));
    }

    @Override
    public void changePassword(BankAccount bankAccount, String password) {
        this.bankData.set("bankaccount." + bankAccount.getAccount() + ".password", password);
        this.bankData.save();
        this.bankData.reload();
    }

    @Override
    public String getProvider() {
        return "Yaml";
    }

}
