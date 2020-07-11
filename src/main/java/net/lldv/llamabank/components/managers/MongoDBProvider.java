package net.lldv.llamabank.components.managers;

import cn.nukkit.player.Player;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.EventLog;
import net.lldv.llamabank.components.data.LogType;
import net.lldv.llamabank.components.managers.database.Provider;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDBProvider extends Provider {

    Config config = LlamaBank.getInstance().getConfig();

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> bankAccounts;

    @Override
    public void connect(LlamaBank server) {
        CompletableFuture.runAsync(() -> {
            MongoClientURI uri = new MongoClientURI(config.getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(config.getString("MongoDB.Database"));
            bankAccounts = mongoDatabase.getCollection("bank_accounts");
            server.getLogger().info("[MongoClient] Connection opened.");
        });
    }

    @Override
    public void disconnect(LlamaBank server) {
        mongoClient.close();
        server.getLogger().info("[MongoClient] Connection closed.");
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
        return "MongoDB";
    }
}
