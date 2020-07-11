package net.lldv.llamabank.components.api;

import cn.nukkit.level.Sound;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.PlaySoundPacket;
import lombok.Getter;
import lombok.Setter;
import net.lldv.llamabank.components.managers.database.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class LlamaBankAPI {

    @Getter
    @Setter
    private static Provider provider;

    public static String getRandomIDCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public static String getDate() {
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

    public static void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.setSound(sound.getSound());
        packet.setPosition(Vector3f.from(new Double(player.getLocation().getX()).intValue(), new Double(player.getLocation().getY()).intValue(), new Double(player.getLocation().getZ()).intValue()));
        packet.setVolume(1.0F);
        packet.setPitch(1.0F);
        player.sendPacket(packet);
    }

}
