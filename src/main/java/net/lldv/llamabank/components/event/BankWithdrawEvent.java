package net.lldv.llamabank.components.event;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.llamabank.components.data.BankAccount;

@AllArgsConstructor
@Getter
public class BankWithdrawEvent extends Event {

    private final String player;
    private final double amount;
    private final BankAccount bankAccount;
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

}
