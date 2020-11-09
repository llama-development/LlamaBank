package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.api.LlamaBankAPI;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
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
        String id = LlamaBankAPI.getRandomIDCode(7);
        String passwordSet = LlamaBankAPI.getRandomIDCode(4);
        List<String> log = new ArrayList<>();
        this.bankData.set("bankaccount." + id + ".owner", owner.getName());
        this.bankData.set("bankaccount." + id + ".password", passwordSet);
        this.bankData.set("bankaccount." + id + ".balance", (double) 0);
        this.bankData.set("bankaccount." + id + ".log", log);
        this.bankData.save();
        this.bankData.reload();
        LlamaBankAPI.giveBankCard(owner, id);
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
                logs.add(new BankLog(account, LlamaBankAPI.getDate(), action, comment));
            });
            returnAccount = new BankAccount(account, owner, password, balance, logs);
        }
        bankAccount.accept(returnAccount);
    }

    @Override
    public void withdrawMoney(String account, Player player, double amount) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() - amount;
            this.bankData.set("bankaccount." + account + ".balance", amountSet);
            this.bankData.save();
            this.bankData.reload();
            this.createBankLog(bankAccount, BankLog.Action.WITHDRAW, Language.getNP("log-withdraw", player.getName(), amount, amountSet, LlamaBankAPI.getDate()));
        });
    }

    @Override
    public void depositMoney(String account, Player player, double amount) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() + amount;
            this.bankData.set("bankaccount." + account + ".balance", amountSet);
            this.bankData.save();
            this.bankData.reload();
            this.createBankLog(bankAccount, BankLog.Action.DEPOSIT, Language.getNP("log-deposit", player.getName(), amount, amountSet, LlamaBankAPI.getDate()));
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
