package me.almana.simplechatformat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FormatsCommand implements TabExecutor {

    private Map<String, String> groupFormatsMap;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {

            sender.sendRichMessage("<red>Incorrect usage!!");
            sender.sendRichMessage("<gray>Correct Usage: <white>/formats <add|remove> <group|player> <format>");
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {


        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
