package net.lldv.llamabank;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.lldv.llamabank.commands.BankCommand;
import net.lldv.llamabank.components.api.LlamaBankAPI;
import net.lldv.llamabank.components.forms.FormListener;
import net.lldv.llamabank.components.forms.FormWindows;
import net.lldv.llamabank.components.language.Language;
import net.lldv.llamabank.components.provider.MongodbProvider;
import net.lldv.llamabank.components.provider.MySqlProvider;
import net.lldv.llamabank.components.provider.Provider;
import net.lldv.llamabank.components.provider.YamlProvider;

import java.util.HashMap;
import java.util.Map;

public class LlamaBank extends PluginBase {

    private final Map<String, Provider> providers = new HashMap<>();
    public Provider provider;

    @Getter
    private static LlamaBank instance;

    @Getter
    private FormWindows formWindows;

    @Override
    public void onEnable() {
        try {
            instance = this;
            saveDefaultConfig();
            this.providers.put("MongoDB", new MongodbProvider());
            this.providers.put("MySql", new MySqlProvider());
            this.providers.put("Yaml", new YamlProvider());
            if (!this.providers.containsKey(this.getConfig().getString("Provider"))) {
                this.getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                return;
            }
            this.provider = this.providers.get(getConfig().getString("Provider"));
            this.provider.connect(this);
            this.getLogger().info("§aSuccessfully loaded " + this.provider.getProvider() + " provider.");
            LlamaBankAPI.setProvider(this.provider);
            LlamaBankAPI.setCreateBankCosts(this.getConfig().getDouble("Settings.CreateBankCosts"));
            LlamaBankAPI.setNewBankCardCosts(this.getConfig().getDouble("Settings.NewBankCardCosts"));
            Language.init();
            this.formWindows = new FormWindows(this.provider);
            this.loadPlugin();
            this.getLogger().info("§aLlamaBank successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load LlamaBank.");
        }
    }

    private void loadPlugin() {
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getCommandMap().register("llamabank", new BankCommand(this));
    }

    @Override
    public void onDisable() {
        this.provider.disconnect(this);
    }

}
