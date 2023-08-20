package me.almana.simplechatformat.utils;

import com.google.gson.Gson;
import me.almana.simplechatformat.SimpleChatFormat;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtils {

    private static final Gson GSON = new Gson();
    private static final String FILE_NAME = "chatmodels.json";

    public static Map<String, String> readFormats() throws IOException {
        Plugin plugin = SimpleChatFormat.getPlugin(SimpleChatFormat.class);
        Path path = plugin.getDataFolder().toPath().resolve(FILE_NAME);
        Files.createDirectories(path.getParent());
        return Arrays.stream(GSON.fromJson(Files.readString(path), ChatModel[].class))
                .collect(Collectors.toMap(ChatModel::getLuckPermsGroup, ChatModel::getMessageFormat));
    }

    public static void writeFormats(Map<String, String> map) throws IOException {

        Plugin plugin = SimpleChatFormat.getPlugin(SimpleChatFormat.class);
        Path path = plugin.getDataFolder().toPath().resolve(FILE_NAME);

        List<ChatModel> chatList = map.entrySet().stream()
                .map(entry -> new ChatModel(entry.getKey(), entry.getValue()))
                .toList();

        Files.writeString(path, GSON.toJson(chatList));
        plugin.getLogger().info("Formats saved to file.");
    }
}
