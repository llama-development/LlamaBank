package net.lldv.llamabank.components.managers;

import cn.nukkit.player.Player;
import cn.nukkit.utils.Config;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.EventLog;
import net.lldv.llamabank.components.data.LogType;
import net.lldv.llamabank.components.managers.database.Provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MySqlProvider extends Provider {

    Connection connection;

    @Override
    public void connect(LlamaBank server) {
        CompletableFuture.runAsync(() -> {
            try {
                Config config = server.getConfig();
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("MySql.Host") + ":" + config.getString("MySql.Port") + "/" + config.getString("MySql.Database") + "?autoReconnect=true", config.getString("MySql.User"), config.getString("MySql.Password"));
                update("CREATE TABLE IF NOT EXISTS bank_accounts(account VARCHAR(30), owner VARCHAR(30), password VARCHAR(100), balance DOUBLE(30), PRIMARY KEY (account));");
                update("CREATE TABLE IF NOT EXISTS bank_log(account VARCHAR(30), logtype VARCHAR(30), target VARCHAR(30), amount DOUBLE(100), date VARCHAR(30));");
                server.getLogger().info("[MySqlClient] Connection opened.");
            } catch (Exception e) {
                e.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to connect to database.");
            }
        });
    }

    public Connection getConnection() {
        return connection;
    }

    public void update(String query) {
        CompletableFuture.runAsync(() -> {
            if (connection != null) {
                try {
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.executeUpdate();
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disconnect(LlamaBank server) {
        if (connection != null) {
            try {
                connection.close();
                server.getLogger().info("[MySqlClient] Connection closed.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                server.getLogger().info("[MySqlClient] Failed to close connection.");
            }
        }
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
        return "MySql";
    }

}
