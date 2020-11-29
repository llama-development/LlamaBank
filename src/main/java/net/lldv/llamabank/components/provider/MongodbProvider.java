package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.api.LlamaBankAPI;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
import net.lldv.llamabank.components.event.BankCreateEvent;
import net.lldv.llamabank.components.event.BankDeleteEvent;
import net.lldv.llamabank.components.event.BankDepositEvent;
import net.lldv.llamabank.components.event.BankWithdrawEvent;
import net.lldv.llamabank.components.language.Language;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MongodbProvider extends Provider {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> bankData;

    @Override
    public void connect(LlamaBank instance) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(instance.getConfig().getString("MongoDB.Uri"));
            this.mongoClient = new MongoClient(uri);
            this.mongoDatabase = this.mongoClient.getDatabase(instance.getConfig().getString("MongoDB.Database"));
            this.bankData = this.mongoDatabase.getCollection("bank_data");
            instance.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(LlamaBank instance) {
        this.mongoClient.close();
        instance.getLogger().info("[MongoClient] Connection closed.");
    }

    @Override
    public void createBankAccount(Player owner, Consumer<String> password) {
        CompletableFuture.runAsync(() -> {
            String id = LlamaBankAPI.getRandomIDCode(7);
            String passwordSet = LlamaBankAPI.getRandomIDCode(4);
            List<String> log = new ArrayList<>();
            Document document = new Document("id", id)
                    .append("owner", owner.getName())
                    .append("password", passwordSet)
                    .append("balance", (double) 0)
                    .append("log", log);
            this.bankData.insertOne(document);
            LlamaBankAPI.giveBankCard(owner, id);
            Server.getInstance().getPluginManager().callEvent(new BankCreateEvent(owner));
            password.accept(passwordSet);
        });
    }

    @Override
    public void getBankAccount(String account, Consumer<BankAccount> bankAccount) {
        CompletableFuture.runAsync(() -> {
            BankAccount returnAccount = null;
            Document document = this.bankData.find(new Document("id", account)).first();
            if (document != null) {
                String owner = document.getString("owner");
                String password = document.getString("password");
                double balance = document.getDouble("balance");
                List<BankLog> logs = new ArrayList<>();
                document.getList("log", String.class).forEach(log -> {
                    String[] data = log.split(":-:");
                    BankLog.Action action = BankLog.Action.valueOf(data[0]);
                    String comment = data[1];
                    logs.add(new BankLog(account, LlamaBankAPI.getDate(), action, comment));
                });
                returnAccount = new BankAccount(account, owner, password, balance, logs);
            }
            bankAccount.accept(returnAccount);
        });
    }

    @Override
    public void withdrawMoney(String account, String player, double amount) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() - amount;
            Document document = this.bankData.find(new Document("id", account)).first();
            assert document != null;
            this.bankData.updateOne(document, new Document("$set", new Document("balance", amountSet)));
            this.createBankLog(bankAccount, BankLog.Action.WITHDRAW, Language.getNP("log-withdraw", player, amount, amountSet, LlamaBankAPI.getDate()));
            Server.getInstance().getPluginManager().callEvent(new BankWithdrawEvent(player, amount, bankAccount));
        });
    }

    @Override
    public void depositMoney(String account, String player, double amount) {
        this.getBankAccount(account, bankAccount -> {
            double amountSet = bankAccount.getBalance() + amount;
            Document document = this.bankData.find(new Document("id", account)).first();
            assert document != null;
            this.bankData.updateOne(document, new Document("$set", new Document("balance", amountSet)));
            this.createBankLog(bankAccount, BankLog.Action.DEPOSIT, Language.getNP("log-deposit", player, amount, amountSet, LlamaBankAPI.getDate()));
            Server.getInstance().getPluginManager().callEvent(new BankDepositEvent(player, amount, bankAccount));
        });
    }

    @Override
    public void createBankLog(BankAccount account, BankLog.Action action, String comment) {
        CompletableFuture.runAsync(() -> {
            Document document = this.bankData.find(new Document("id", account.getAccount())).first();
            if (document != null) {
                List<String> list = document.getList("log", String.class);
                list.add(action.name().toUpperCase() + ":-:" + comment);
                this.bankData.updateOne(document, new Document("$set", new Document("log", list)));
            }
        });
    }

    @Override
    public void deleteAccount(BankAccount account) {
        CompletableFuture.runAsync(() -> {
            this.bankData.findOneAndDelete(new Document("id", account.getAccount()));
            Server.getInstance().getPluginManager().callEvent(new BankDeleteEvent(account));
        });
    }

    @Override
    public void changePassword(BankAccount bankAccount, String password) {
        CompletableFuture.runAsync(() -> this.bankData.updateOne(new Document("id", bankAccount.getAccount()), new Document("$set", new Document("password", password))));
    }

    @Override
    public String getProvider() {
        return "MongoDB";
    }

}
