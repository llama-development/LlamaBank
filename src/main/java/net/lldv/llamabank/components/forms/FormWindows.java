package net.lldv.llamabank.components.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.level.Sound;
import net.lldv.llamabank.components.api.LlamaBankAPI;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
import net.lldv.llamabank.components.event.BankChangePasswordEvent;
import net.lldv.llamabank.components.forms.custom.CustomForm;
import net.lldv.llamabank.components.forms.modal.ModalForm;
import net.lldv.llamabank.components.forms.simple.SimpleForm;
import net.lldv.llamabank.components.language.Language;
import net.lldv.llamabank.components.provider.Provider;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.concurrent.CompletableFuture;

public class FormWindows {

    private final Provider provider;

    public FormWindows(Provider provider) {
        this.provider = provider;
    }

    public void openCreateBankAccount(Player player) {
        ModalForm form = new ModalForm.Builder(Language.getNP("bank-create-title"), Language.getNP("bank-create-content", LlamaBankAPI.getCreateBankCosts()),
                Language.getNP("bank-create-create"), Language.getNP("bank-create-cancel"))
                .onYes(e -> CompletableFuture.runAsync(() -> {
                    if (LlamaEconomy.getAPI().getMoney(player.getName()) >= LlamaBankAPI.getCreateBankCosts()) {
                        this.provider.createBankAccount(player, password -> player.sendMessage(Language.get("bank-account-created", password)));
                        LlamaBankAPI.playSound(player, Sound.RANDOM_LEVELUP);
                        LlamaEconomy.getAPI().reduceMoney(player.getName(), LlamaBankAPI.getCreateBankCosts());
                    } else {
                        player.sendMessage(Language.get("not-enough-money"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                    }
                }))
                .onNo(e -> {
                })
                .build();
        form.send(player);
    }

    public void openBankLogin(Player player, String account) {
        this.provider.getBankAccount(account, bankAccount -> {
            if (bankAccount == null) {
                player.sendMessage(Language.get("invalid-account"));
                LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                return;
            }
            CustomForm form = new CustomForm.Builder(Language.getNP("bank-login-title"))
                    .addElement(new ElementInput(Language.getNP("bank-login-password"), Language.getNP("bank-login-password-placeholder")))
                    .onSubmit((e, r) -> {
                        if (r.getInputResponse(0).isEmpty()) {
                            player.sendMessage(Language.get("invalid-input"));
                            LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                            return;
                        }
                        if (r.getInputResponse(0).equals(bankAccount.getPassword())) {
                            this.openBankDashboard(player, bankAccount.getAccount());
                            LlamaBankAPI.playSound(player, Sound.NOTE_PLING);
                            this.provider.createBankLog(bankAccount, BankLog.Action.LOGIN, Language.getNP("log-login", player.getName(), LlamaBankAPI.getDate()));
                        } else {
                            player.sendMessage(Language.get("invalid-password", bankAccount.getAccount()));
                            LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                            this.provider.createBankLog(bankAccount, BankLog.Action.LOGIN_FAIL, Language.getNP("log-loginfail", player.getName(), LlamaBankAPI.getDate()));
                        }
                    })
                    .build();
            form.send(player);
        });
    }

    public void openBankDashboard(Player player, String bankAccount) {
        this.provider.getBankAccount(bankAccount, account -> {
            SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("bank-dashboard-title"), Language.getNP("bank-dashboard-content", account.getAccount(), account.getOwner(), LlamaEconomy.getAPI().getMoneyFormat().format(account.getBalance())))
                    .addButton(new ElementButton(Language.getNP("bank-dashboard-deposit")), e -> this.openDepositMenu(player, account))
                    .addButton(new ElementButton(Language.getNP("bank-dashboard-withdraw")), e -> this.openWithdrawMenu(player, account))
                    .addButton(new ElementButton(Language.getNP("bank-dashboard-banklog")), e -> this.openLogMenu(player, account));
            if (player.getName().equals(account.getOwner())) form.addButton(new ElementButton(Language.getNP("bank-dashboard-settings")), e -> this.openBankSettings(player, account));
            SimpleForm finalForm = form.build();
            finalForm.send(player);
        });
    }

    public void openBankSettings(Player player, BankAccount bankAccount) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("bank-settings-title"), Language.getNP("bank-settings-content"))
                .addButton(new ElementButton(Language.getNP("bank-settings-newcard", LlamaBankAPI.getNewBankCardCosts())), e -> CompletableFuture.runAsync(() -> {
                    if (LlamaEconomy.getAPI().getMoney(player.getName()) >= LlamaBankAPI.getNewBankCardCosts()) {
                        LlamaEconomy.getAPI().reduceMoney(player.getName(), LlamaBankAPI.getNewBankCardCosts());
                        LlamaBankAPI.giveBankCard(player, bankAccount.getAccount());
                        LlamaBankAPI.playSound(player, Sound.RANDOM_LEVELUP);
                    } else {
                        player.sendMessage(Language.get("not-enough-money"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                    }
                }))
                .addButton(new ElementButton(Language.getNP("bank-settings-password")), e -> this.openPasswordMenu(player, bankAccount))
                .addButton(new ElementButton(Language.getNP("bank-settings-delete")), e -> this.openDeleteMenu(player, bankAccount))
                .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openBankDashboard(player, bankAccount.getAccount()))
                .build();
        form.send(player);
    }

    public void openDeleteMenu(Player player, BankAccount bankAccount) {
        ModalForm form = new ModalForm.Builder(Language.getNP("bank-delete-title"), Language.getNP("bank-delete-content"),
                Language.getNP("bank-delete-confirm"), Language.getNP("bank-delete-cancel"))
                .onYes(e -> {
                    this.provider.deleteAccount(bankAccount);
                    player.sendMessage(Language.get("account-deleted", bankAccount.getAccount()));
                    LlamaBankAPI.playSound(player, Sound.NOTE_PLING);
                })
                .onNo(e -> this.openBankSettings(player, bankAccount))
                .build();
        form.send(player);
    }

    public void openWithdrawMenu(Player player, BankAccount bankAccount) {
        CustomForm form = new CustomForm.Builder(Language.getNP("bank-withdraw-title"))
                .addElement(new ElementInput(Language.getNP("bank-withdraw-amount"), Language.getNP("bank-withdraw-amount-placeholder")))
                .addElement(new ElementToggle(Language.getNP("bank-withdraw-back"), false))
                .onSubmit((e, r) -> CompletableFuture.runAsync(() -> {
                    try {
                        this.provider.getBankAccount(bankAccount.getAccount(), account -> {
                            double amount = Double.parseDouble(r.getInputResponse(0));
                            if (amount <= 0) {
                                player.sendMessage(Language.get("invalid-input"));
                                LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                                return;
                            }
                            if (account.getBalance() < amount) {
                                player.sendMessage(Language.get("not-enough-bankmoney"));
                                LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                                return;
                            }
                            this.provider.withdrawMoney(account.getAccount(), player.getName(), amount);
                            LlamaBankAPI.playSound(player, Sound.NOTE_PLING);
                            LlamaEconomy.getAPI().addMoney(player.getName(), amount);
                            if (r.getToggleResponse(1)) {
                                this.openBankDashboard(player, account.getAccount());
                                return;
                            }
                            player.sendMessage(Language.get("deposit-money-success", amount));
                        });
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Language.get("invalid-input"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                    }
                }))
                .build();
        form.send(player);
    }

    public void openDepositMenu(Player player, BankAccount bankAccount) {
        CustomForm form = new CustomForm.Builder(Language.getNP("bank-deposit-title"))
                .addElement(new ElementInput(Language.getNP("bank-deposit-amount"), Language.getNP("bank-deposit-amount-placeholder")))
                .addElement(new ElementToggle(Language.getNP("bank-deposit-back"), false))
                .onSubmit((e, r) -> CompletableFuture.runAsync(() -> {
                    try {
                        this.provider.getBankAccount(bankAccount.getAccount(), account -> {
                            double amount = Double.parseDouble(r.getInputResponse(0));
                            if (amount <= 0) {
                                player.sendMessage(Language.get("invalid-input"));
                                LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                                return;
                            }
                            if (LlamaEconomy.getAPI().getMoney(player.getName()) < amount) {
                                player.sendMessage(Language.get("not-enough-money"));
                                LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                                return;
                            }
                            this.provider.depositMoney(account.getAccount(), player.getName(), amount);
                            LlamaBankAPI.playSound(player, Sound.NOTE_PLING);
                            LlamaEconomy.getAPI().reduceMoney(player.getName(), amount);
                            if (r.getToggleResponse(1)) {
                                this.openBankDashboard(player, account.getAccount());
                                return;
                            }
                            player.sendMessage(Language.get("withdraw-money-success", amount));
                        });
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Language.get("invalid-input"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                    }
                }))
                .build();
        form.send(player);
    }

    public void openLogMenu(Player player, BankAccount bankAccount) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("bank-log-select-title"), Language.getNP("bank-log-select-content"))
                .addButton(new ElementButton(Language.getNP("bank-log-login")), e -> this.openLogSectionMenu(player, bankAccount, BankLog.Action.LOGIN))
                .addButton(new ElementButton(Language.getNP("bank-log-loginfail")), e -> this.openLogSectionMenu(player, bankAccount, BankLog.Action.LOGIN_FAIL))
                .addButton(new ElementButton(Language.getNP("bank-log-deposit")), e -> this.openLogSectionMenu(player, bankAccount, BankLog.Action.DEPOSIT))
                .addButton(new ElementButton(Language.getNP("bank-log-withdraw")), e -> this.openLogSectionMenu(player, bankAccount, BankLog.Action.WITHDRAW))
                .addButton(new ElementButton(Language.getNP("bank-log-account")), e -> this.openLogSectionMenu(player, bankAccount, BankLog.Action.ACCOUNT))
                .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openBankDashboard(player, bankAccount.getAccount()))
                .build();
        form.send(player);
    }

    public void openLogSectionMenu(Player player, BankAccount bankAccount, BankLog.Action action) {
        SimpleForm.Builder form = new SimpleForm.Builder(Language.getNP("bank-logsection-title"), Language.getNP("bank-logsection-content"));
        bankAccount.getBankLogs().forEach(bankLog -> {
            if (bankLog.getAction() == action) {
                form.addButton(new ElementButton(Language.getNP("bank-logsection-entry", bankLog.getDate())), e -> {
                    SimpleForm logForm = new SimpleForm.Builder(action.name().toUpperCase(), Language.getNP("bank-logsection-details", bankLog.getDate(), bankLog.getComment()))
                            .addButton(new ElementButton(Language.getNP("back-button")), f -> this.openLogSectionMenu(player, bankAccount, action))
                            .build();
                    logForm.send(e);
                });
            }
        });
        form.addButton(new ElementButton(Language.getNP("back-button")), f -> this.openLogMenu(player, bankAccount));
        SimpleForm finalForm = form.build();
        finalForm.send(player);
    }

    public void openPasswordMenu(Player player, BankAccount bankAccount) {
        CustomForm form = new CustomForm.Builder(Language.getNP("bank-password-title"))
                .addElement(new ElementInput(Language.getNP("bank-password-current"), Language.getNP("bank-password-current-placeholder")))
                .addElement(new ElementInput(Language.getNP("bank-password-new"), Language.getNP("bank-password-new-placeholder")))
                .onSubmit((e, r) -> {
                    String currentPassword = r.getInputResponse(0);
                    String newPassword = r.getInputResponse(1);
                    if (currentPassword.isEmpty() || newPassword.isEmpty() || newPassword.length() > 4) {
                        player.sendMessage(Language.get("invalid-input"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                        return;
                    }
                    if (!currentPassword.equals(bankAccount.getPassword())) {
                        player.sendMessage(Language.get("invalid-password", bankAccount.getAccount()));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                        return;
                    }
                    try {
                        int password = Integer.parseInt(newPassword);
                        this.provider.changePassword(bankAccount, newPassword);
                        this.provider.createBankLog(bankAccount, BankLog.Action.ACCOUNT, Language.getNP("log-change-password", player.getName(), LlamaBankAPI.getDate(), password));
                        Server.getInstance().getPluginManager().callEvent(new BankChangePasswordEvent(player.getName(), newPassword, bankAccount));
                        player.sendMessage(Language.get("password-changed", newPassword));
                        LlamaBankAPI.playSound(player, Sound.RANDOM_LEVELUP);
                    } catch (NumberFormatException exception) {
                        player.sendMessage(Language.get("invalid-input"));
                        LlamaBankAPI.playSound(player, Sound.NOTE_BASS);
                    }
                })
                .build();
        form.send(player);
    }

}
