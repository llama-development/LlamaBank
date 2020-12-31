package net.lldv.llamabank.components.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.llamabank.components.data.BankAccount;

@AllArgsConstructor
@Getter
public class BankChangePasswordEvent extends Event {

    private final String player;
    private final String password;
    private final BankAccount bankAccount;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

}
