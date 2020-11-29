package net.lldv.llamabank.components.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BankCreateEvent extends Event {

    private final Player player;
    private static final HandlerList handlers = new HandlerList();

}
