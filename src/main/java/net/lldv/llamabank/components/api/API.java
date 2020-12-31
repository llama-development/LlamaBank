package net.lldv.llamabank.components.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.llamabank.components.forms.FormWindows;
import net.lldv.llamabank.components.provider.Provider;

@AllArgsConstructor
@Getter
public class API {

    private final Provider provider;
    private final FormWindows formWindows;
    private final double createBankCosts, newBankCardCosts;

}
