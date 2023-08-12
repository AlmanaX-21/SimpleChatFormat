package me.almana.simplechatformat;

import me.almana.simplechatformat.commands.FormatsCommand;
import me.almana.simplechatformat.listeners.ChatListener;
import me.almana.simplechatformat.utils.JsonUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class SimpleChatFormat extends JavaPlugin {

    private LuckPerms luckPerms;
    private static Logger logger;
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private Map<String, String> groupFormatsMap = new HashMap<>();

    @Override
    public void onEnable() {

        luckPerms = luckpermSetup();
        logger = this.getLogger();
        if (!new File(this.getDataFolder().getAbsolutePath() + "/chatmodels.json").exists()) saveResource("chatmodels.json", false);
        try {
            groupFormatsMap = JsonUtils.readFormats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(new ChatListener(groupFormatsMap, luckPerms), this);
        getServer().getPluginCommand("formats").setExecutor(new FormatsCommand(groupFormatsMap, luckPerms));

        logger.info("Plugin enabled successfully.");
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

    @NotNull
    public static Logger getPluginLogger() {
        return logger;
    }
}
