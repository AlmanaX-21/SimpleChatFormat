package me.almana.simplechatformat.commands;

import me.almana.simplechatformat.SimpleChatFormat;
import me.almana.simplechatformat.utils.JsonUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.logging.Level;

public class FormatsCommand implements TabExecutor {

    private final Plugin plugin;
    private final Map<String, String> groupFormatsMap;
    private final LuckPerms luckPerms;

    public FormatsCommand(Plugin plugin, Map<String, String> groupFormatsMap, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.groupFormatsMap = groupFormatsMap;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 3) {

            sender.sendRichMessage("<red>Incorrect usage!!");
            sender.sendRichMessage("<gray>Correct Usage: <white>/formats <add|remove> <group|player> <group-name|player-name> [<format>]");
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {

            String groupOrPlayer = args[2];
            StringJoiner builder = new StringJoiner(" ");
            for (int i = 3; i < args.length; i++) {

                builder.add(args[i]);
            }
            String format = builder.toString();
            if (args[1].equalsIgnoreCase("group") && checkIfGroup(groupOrPlayer)) {

                formatAdd(groupOrPlayer, format, sender);
            } else if (args[1].equalsIgnoreCase("player") && checkIfPlayer(groupOrPlayer)) {

                Player player = Bukkit.getPlayerExact(groupOrPlayer);
                formatAdd(player.getUniqueId().toString(), format, sender);
            } else {

                sender.sendRichMessage("<red>Something went wrong, command done incorrectly!");
                sender.sendRichMessage("<red>Incorrect usage!!");
                sender.sendRichMessage("<gray>Correct Usage: <white>/formats <add|remove> <group|player> <group-name|player-name> <format>");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("remove")) {

            String groupOrPlayer = args[2];
            if ((args[1].equalsIgnoreCase("player") && !checkIfPlayer(groupOrPlayer)) || (args[1].equalsIgnoreCase("group") &&!checkIfGroup(groupOrPlayer))) {

                sender.sendRichMessage("<red>Please input a player or group");
                sender.sendRichMessage("<red>Incorrect usage!!");
                sender.sendRichMessage("<gray>Correct Usage: <white>/formats remove <group|player> <group-name|player-name>");
                return true;
            }
            groupFormatsMap.remove(groupOrPlayer);
            if (groupFormatsMap.isEmpty()) {

                formatAdd("default", "<prefix> <displayname><suffix><gray>:<white><message>", sender);
                sender.sendRichMessage("<gold>All formats were removed, reverting to defaults");
            }
            sender.sendRichMessage("<green>Successfully removed chat format for " + groupOrPlayer);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1 -> completions.addAll(filterCompletions(List.of("add", "remove"), args[0]));
            case 2 -> completions.addAll(filterCompletions(List.of("group", "player"), args[1]));
            case 3 -> {
                switch (args[1].toLowerCase()) {
                    case "group" -> luckPerms.getGroupManager().getLoadedGroups().stream()
                            .map(Group::getName)
                            .filter(completionPredicate(args[2]))
                            .forEach(completions::add);
                    case "player" -> sender.getServer().getOnlinePlayers().forEach(p -> completions.add(p.getName()));
                }
            }
        }
        return completions;
    }

    private void formatAdd(String groupOrPlayer, String format, CommandSender sender) {

        groupFormatsMap.put(groupOrPlayer, format);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                JsonUtils.writeFormats(groupFormatsMap);
            } catch (IOException e) {
                sender.sendRichMessage("<red>Could not save format.");
                plugin.getLogger().log(Level.SEVERE, "Could not save format.", e);
                return;
            }
            sender.sendRichMessage("<green>Added new format for " + groupOrPlayer);
            sender.sendMessage("Format: " + format);
        });
    }

    private boolean checkIfGroup(String group) {

        for (Group grp: luckPerms.getGroupManager().getLoadedGroups()) {

            if (grp.getName().equalsIgnoreCase(group)) return true;
        }
        return false;
    }

    private boolean checkIfPlayer(String playerName) {

        return Bukkit.getPlayerExact(playerName) != null;
    }

    private List<String> filterCompletions(List<String> completions, String input) {
        return completions.stream()
                .filter(completionPredicate(input))
                .toList();
    }

    private Predicate<String> completionPredicate(String input){
        String lowerInput = input.toLowerCase();
        return completion -> completion.toLowerCase().startsWith(lowerInput);
    }
}
