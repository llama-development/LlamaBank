package net.lldv.llamabank.components.provider;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import net.lldv.llamabank.LlamaBank;
import net.lldv.llamabank.components.data.BankAccount;
import net.lldv.llamabank.components.data.BankLog;
import net.lldv.llamabank.components.language.Language;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;

public class Provider {

    public void connect(LlamaBank instance) {

    }

    public void disconnect(LlamaBank instance) {

    }

    public void createBankAccount(Player owner, Consumer<String> password) {

    }

    public void getBankAccount(String account, Consumer<BankAccount> bankAccount) {

    }

    public void withdrawMoney(String account, String player, double amount, Consumer<Double> d) {

    }

    public void depositMoney(String account, String player, double amount, Consumer<Double> d) {

    }

    public void createBankLog(BankAccount account, BankLog.Action action, String comment) {

    }

    public void deleteAccount(BankAccount account) {

    }

    public void changePassword(BankAccount bankAccount, String password) {

    }

    public String getProvider() {
        return null;
    }

    public void giveBankCard(Player player, String bankAccount) {
        Item item = Item.get(ItemID.PAPER, 0, 1);
        item.setNamedTag(new CompoundTag().putString("llama_bank_account", bankAccount));
        item.setCustomName(Language.getNP("bank-card-name", bankAccount));
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
            player.sendMessage(Language.get("got-bank-card", bankAccount));
        } else player.sendMessage(Language.get("inventory-full"));
    }

    public String getRandomIDCode(int i) {
        String chars = "1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < i) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

    public void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.x = new Double(player.getLocation().getX()).intValue();
        packet.y = (new Double(player.getLocation().getY())).intValue();
        packet.z = (new Double(player.getLocation().getZ())).intValue();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        player.dataPacket(packet);
    }

}
