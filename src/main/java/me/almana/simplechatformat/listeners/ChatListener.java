package me.almana.simplechatformat.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.almana.simplechatformat.SimpleChatFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private final Map<String, String> groupFormatsMap;
    private final LuckPerms luckPerms;
    private final Cache<String, String> groupFormatCache;
    private final Cache<UUID, User> userCache;

    public ChatListener(Map<String, String> groupFormatsMap, LuckPerms luckPerms) {
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
        User user = userCache.getIfPresent(player.getUniqueId());
        if (user == null) {
            user = luckPerms.getUserManager().getUser(player.getUniqueId());
            userCache.put(player.getUniqueId(), user);
        }
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        Component message = event.originalMessage();
        String group = user.getPrimaryGroup();
        String prefix = user.getCachedData().getMetaData().getPrefix() == null ? "": user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix() == null ? "": user.getCachedData().getMetaData().getSuffix();
        if (groupFormatsMap.containsKey(player.getUniqueId().toString())) {

            event.renderer(handleFormatting(player, player.getUniqueId().toString(), serializer.serialize(message), prefix, suffix));
            return;
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

