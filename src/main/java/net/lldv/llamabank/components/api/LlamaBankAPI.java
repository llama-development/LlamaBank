package net.lldv.llamabank.components.api;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import lombok.Getter;
import lombok.Setter;
import net.lldv.llamabank.components.language.Language;
import net.lldv.llamabank.components.provider.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class LlamaBankAPI {

    @Getter
    @Setter
    public static Provider provider;

    @Getter
    @Setter
    private static double createBankCosts, newBankCardCosts;

    public static void giveBankCard(Player player, String bankAccount) {
        Item item = Item.get(ItemID.PAPER, 0, 1);
        item.setNamedTag(new CompoundTag().putString("llama_bank_account", bankAccount));
        item.setCustomName(Language.getNP("bank-card-name", bankAccount));
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
            player.sendMessage(Language.get("got-bank-card", bankAccount));
        } else player.sendMessage(Language.get("inventory-full"));
    }

    @Deprecated
    public static String getRandomIDCode(int i) {
        String chars = "1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < i) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    @Deprecated
    public static String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

    @Deprecated
    public static void playSound(Player player, Sound sound) {
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
