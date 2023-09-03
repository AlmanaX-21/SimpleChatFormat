package me.almana.simplechatformat.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.almana.simplechatformat.SimpleChatFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.util.Objects.requireNonNullElse;

public class ChatListener implements Listener {

    private final Plugin plugin;
    private final Map<String, String> groupFormatsMap;
    private final LuckPerms luckPerms;
    private final Cache<String, String> groupFormatCache;
    private final Cache<UUID, User> userCache;

    public ChatListener(Plugin plugin, Map<String, String> groupFormatsMap, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.groupFormatsMap = groupFormatsMap;
        this.luckPerms = luckPerms;
        this.groupFormatCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        this.userCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();
        User user;
        try {
            user = userCache.get(player.getUniqueId(), () -> luckPerms.getUserManager().getUser(player.getUniqueId()));
        } catch (ExecutionException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not retrieve luckperms  user", e);
            return;
        }
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        Component message = event.originalMessage();
        String group = user.getPrimaryGroup();
        String prefix = requireNonNullElse(user.getCachedData().getMetaData().getPrefix(), "");
        String suffix = requireNonNullElse(user.getCachedData().getMetaData().getSuffix(), "");
        if (groupFormatsMap.containsKey(player.getUniqueId().toString())) {

            event.renderer(handleFormatting(player, player.getUniqueId().toString(), serializer.serialize(message), prefix, suffix));
            return;
        }
        if (!groupFormatsMap.containsKey(group)) {

            event.setCancelled(true);
            player.sendRichMessage("<red>Something went wrong with chat formatting...");
            plugin.getLogger().severe("Group information not found.");
        }
        event.renderer(handleFormatting(player, group, serializer.serialize(message), prefix, suffix));
    }


    private ChatRenderer handleFormatting(Player player, String groupOrUUID, String msg, String prefix, String suffix) {

        String format = groupFormatCache.getIfPresent(groupOrUUID);
        if (format == null) {
            format = groupFormatsMap.get(groupOrUUID);
            if (format == null) throw new NullPointerException("Format for supplied group or player not found...");
            groupFormatCache.put(groupOrUUID, format);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) format = PlaceholderAPI.setPlaceholders(player, format);

        String finalFormat = format;
        return new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {

                return SimpleChatFormat.MINI_MESSAGE.deserialize(finalFormat,
                        Placeholder.parsed("prefix", prefix),
                        Placeholder.parsed("suffix", suffix),
                        Placeholder.parsed("displayname", player.getDisplayName()),
                        Placeholder.unparsed("message", msg));
            }
        };
    }
}

