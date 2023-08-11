package me.almana.simplechatformat;

import me.almana.simplechatformat.utils.JsonUtils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SimpleChatFormat extends JavaPlugin {

    private LuckPerms luckPerms;
    private Map<String, String> groupFormatsMap = new HashMap<>();

    @Override
    public void onEnable() {

        luckPerms = luckpermSetup();
        try {
            groupFormatsMap = JsonUtils.readFormats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private LuckPerms luckpermSetup() {

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) return luckPerms = provider.getProvider();
        return null;
    }
}
