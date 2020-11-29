package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.api.LlamaBankAPI;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
import net.lldv.llamabank.components.language.Language;
import net.lldv.llamabank.components.simplesqlclient.MySqlClient;
import net.lldv.llamabank.components.simplesqlclient.objects.SqlColumn;
import net.lldv.llamabank.components.simplesqlclient.objects.SqlDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MySqlProvider extends Provider {

    private MySqlClient client;

    @Override
    public void connect(LlamaBank instance) {
        CompletableFuture.runAsync(() -> {
            try {
                this.client = new MySqlClient(
                        instance.getConfig().getString("MySql.Host"),
                        instance.getConfig().getString("MySql.Port"),
                        instance.getConfig().getString("MySql.User"),
                        instance.getConfig().getString("MySql.Password"),
                        instance.getConfig().getString("MySql.Database")
                );

                this.client.createTable("bank_data", "id",
                        new SqlColumn("id", SqlColumn.Type.VARCHAR, 7)
                                .append("owner", SqlColumn.Type.VARCHAR, 20)
                                .append("password", SqlColumn.Type.VARCHAR, 5)
                                .append("balance", SqlColumn.Type.DOUBLE)
                                .append("log", SqlColumn.Type.LONGTEXT));

                instance.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                instance.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    @Override
    public void disconnect(LlamaBank instance) {
        instance.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public void createBankAccount(Player owner, Consumer<String> password) {
        CompletableFuture.runAsync(() -> {
            String id = LlamaBankAPI.getRandomIDCode(7);
            String passwordSet = LlamaBankAPI.getRandomIDCode(4);
            this.client.insert("bank_data", new SqlDocument("id", id)
                    .append("owner", owner.getName())
                    .append("password", passwordSet)
                    .append("balance", (double) 0)
                    .append("log", ""));
            LlamaBankAPI.giveBankCard(owner, id);
            password.accept(passwordSet);
        });
    }

    @Override
    public void getBankAccount(String account, Consumer<BankAccount> bankAccount) {
        CompletableFuture.runAsync(() -> {
            BankAccount returnAccount = null;
            SqlDocument document = this.client.find("bank_data", "id", account).first();
            if (document != null) {
                String owner = document.getString("owner");
                String password = document.getString("password");
                double balance = document.getDouble("balance");
                String rawLog = document.getString("log");
                List<BankLog> logs = new ArrayList<>();
                if (!rawLog.isEmpty()) {
                    for (String s : rawLog.split("#-#")) {
                        String[] data = s.split(":-:");
                        BankLog.Action action = BankLog.Action.valueOf(data[0]);
                        String comment = data[1];
                        logs.add(new BankLog(account, LlamaBankAPI.getDate(), action, comment));
                    }
                }
                returnAccount = new BankAccount(account, owner, password, balance, logs);
            }
            bankAccount.accept(returnAccount);
        });
    }

    @Override
    public void withdrawMoney(String account, Player player, double amount) {
        CompletableFuture.runAsync(() -> this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() - amount;
            this.client.update("bank_data", new SqlDocument("id", account), new SqlDocument("balance", amountSet));
            this.createBankLog(bankAccount, BankLog.Action.WITHDRAW, Language.getNP("log-withdraw", player.getName(), amount, amountSet, LlamaBankAPI.getDate()));
        }));
    }

    @Override
    public void depositMoney(String account, Player player, double amount) {
        CompletableFuture.runAsync(() -> this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() + amount;
            this.client.update("bank_data", new SqlDocument("id", account), new SqlDocument("balance", amountSet));
            this.createBankLog(bankAccount, BankLog.Action.DEPOSIT, Language.getNP("log-deposit", player.getName(), amount, amountSet, LlamaBankAPI.getDate()));
        }));
    }

    @Override
    public void createBankLog(BankAccount account, BankLog.Action action, String comment) {
        CompletableFuture.runAsync(() -> {
            SqlDocument document = this.client.find("bank_data", new SqlDocument("id", account.getAccount())).first();
            if (document != null) {
                this.client.update("bank_data", new SqlDocument("id", account.getAccount()), new SqlDocument("log", document.getString("log") + action.name().toUpperCase() + ":-:" + comment + "#-#"));
            }
        });
    }

    @Override
    public void deleteAccount(BankAccount account) {
        CompletableFuture.runAsync(() -> this.client.delete("bank_data", new SqlDocument("id", account.getAccount())));
    }

    @Override
    public void changePassword(BankAccount bankAccount, String password) {
        CompletableFuture.runAsync(() -> this.client.update("bank_data", new SqlDocument("id", bankAccount.getAccount()), new SqlDocument("password", password)));
    }

    @Override
    public String getProvider() {
        return "MySql";
    }

}
