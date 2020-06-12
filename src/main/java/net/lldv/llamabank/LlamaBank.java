package net.lldv.llamabank;

import cn.nukkit.plugin.PluginBase;
import net.lldv.llamabank.components.managers.MongoDBProvider;
import net.lldv.llamabank.components.managers.MySqlProvider;
import net.lldv.llamabank.components.managers.YamlProvider;
import net.lldv.llamabank.components.managers.database.Provider;
import net.lldv.llamabank.components.tools.Language;

import java.util.HashMap;
import java.util.Map;

public class LlamaBank extends PluginBase {

    private static LlamaBank instance;
    public static Provider provider;
    private static final Map<String, Provider> providers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Language.initConfiguration();
        registerProvider(new MongoDBProvider());
        registerProvider(new MySqlProvider());
        registerProvider(new YamlProvider());
        if (!providers.containsKey(getConfig().getString("Provider"))) {
            getLogger().error("§4Please specify a valid provider: Yaml, MySql, MongoDB");
            return;
        }
        provider = providers.get(getConfig().getString("Provider"));
        provider.connect(this);
        getLogger().info("§aSuccessfully loaded " + provider.getProvider() + " provider.");
        //Api set provider
    }

    @Override
    public void onDisable() {
        provider.disconnect(this);
    }

    private void registerProvider(Provider provider) {
        providers.put(provider.getProvider(), provider);
    }

    public static LlamaBank getInstance() {
        return instance;
    }
}
