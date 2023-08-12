package me.almana.simplechatformat.utils;

import com.google.gson.Gson;
import com.mojang.serialization.Decoder;
import me.almana.simplechatformat.SimpleChatFormat;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static Map<String, String> readFormats() throws IOException {

        Plugin plugin = SimpleChatFormat.getPlugin(SimpleChatFormat.class);
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/chatmodels.json");
        if (!file.exists()) {

            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            ChatModel[] models = gson.fromJson(reader, ChatModel[].class);
            map = Arrays.stream(models)
                    .collect(Collectors.toMap(ChatModel::getLuckPermsGroup, ChatModel::getMessageFormat));
        }
        return map;
    }

    public static void writeFormats(Map<String, String> map) throws IOException {

        Plugin plugin = SimpleChatFormat.getPlugin(SimpleChatFormat.class);
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/chatmodels.json");

        List<ChatModel> chatList = map.entrySet().stream()
                .map(entry -> new ChatModel(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(chatList, writer);
            writer.flush();
            plugin.getLogger().info("Formats saved to file.");
        }
    }
}
