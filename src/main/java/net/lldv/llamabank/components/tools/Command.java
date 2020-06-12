package net.lldv.llamabank.components.tools;

import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParameter;

public class Command {

    public static CommandData create(String name, String description) {
        return CommandData.builder(name).setDescription(description).setUsageMessage("").build();
    }

    public static CommandData create(String name, String description, String[] permission) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setUsageMessage("").build();
    }

    public static CommandData create(String name, String description, String[] permission, String[] aliases) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setAliases(aliases).setUsageMessage("").build();
    }

    public static CommandData create(String name, String description, String[] permission, String[] aliases, CommandParameter[] commandParameters) {
        return CommandData.builder(name).setDescription(description).setPermissions(permission).setAliases(aliases).setParameters(commandParameters).setUsageMessage("").build();
    }

}
