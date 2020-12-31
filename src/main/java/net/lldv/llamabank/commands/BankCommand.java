package net.lldv.llamabank.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import net.lldv.llamabank.LlamaBank;

public class BankCommand extends PluginCommand<LlamaBank> {

    public BankCommand(LlamaBank owner) {
        super(owner.getConfig().getString("Commands.Bank.Name"), owner);
        this.setDescription(owner.getConfig().getString("Commands.Bank.Description"));
        this.setAliases(owner.getConfig().getStringList("Commands.Bank.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Item item = player.getInventory().getItemInHand();
            if (item.getId() == ItemID.PAPER) {
                if (item.getNamedTag().getString("llama_bank_account") != null) {
                    String account = item.getNamedTag().getString("llama_bank_account");
                    LlamaBank.getApi().getFormWindows().openBankLogin(player, account);
                } else LlamaBank.getApi().getFormWindows().openCreateBankAccount(player);
            } else LlamaBank.getApi().getFormWindows().openCreateBankAccount(player);
        }
        return true;
    }

}
