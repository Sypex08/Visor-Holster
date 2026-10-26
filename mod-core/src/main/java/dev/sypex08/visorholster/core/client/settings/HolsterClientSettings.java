package dev.sypex08.visorholster.core.client.settings;

import com.google.gson.*;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public final class HolsterClientSettings {

    private static final int MIN_COLOR_VALUE = 0;
    private static final int MAX_COLOR_VALUE = 255;

    private static final int DEFAULT_RED = 80;
    private static final int DEFAULT_GREEN = 180;
    private static final int DEFAULT_BLUE = 255;
    private static final int DEFAULT_ALPHA = 130;

    private static boolean enabled = true;

    private static int distance = 80;

    private static int red = DEFAULT_RED;
    private static int green = DEFAULT_GREEN;
    private static int blue = DEFAULT_BLUE;
    private static int alpha = DEFAULT_ALPHA;

    private static boolean preview = false;

    private HolsterClientSettings(){
    }

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    public static Path getConfigPath(){
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config/visor_holster.json");
    }

    public static void load() {
        Path path = getConfigPath();

        if (!Files.exists(path)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            setRed(getInt(json, "red", DEFAULT_RED));
            setGreen(getInt(json, "green", DEFAULT_GREEN));
            setBlue(getInt(json, "blue", DEFAULT_BLUE));
            setAlpha(getInt(json, "alpha", DEFAULT_ALPHA));

            setDistance(getInt(json, "distance", 80));
            setPreview(getBoolean(json, "preview", false));
            setEnabled(getBoolean(json, "enabled", true));

        } catch (IOException | RuntimeException exception) {
            System.err.println("[VisorHolster] Could not load client settings");
            exception.printStackTrace();
        }
    }

    public static void save() {
        Path path = getConfigPath();

        JsonObject json = new JsonObject();
        json.addProperty("red", red);
        json.addProperty("green", green);
        json.addProperty("blue", blue);
        json.addProperty("alpha", alpha);
        json.addProperty("distance", distance);
        json.addProperty("preview", preview);
        json.addProperty("enabled", enabled);

        try {
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8
            )) {
                GSON.toJson(json, writer);
            }

        } catch (IOException exception) {
            System.err.println("[VisorHolster] Could not save client settings");
            exception.printStackTrace();
        }
    }



    private static int getInt(
            JsonObject json,
            String key,
            int fallback
    ) {
        JsonElement value = json.get(key);

        return value != null && value.isJsonPrimitive()
                ? value.getAsInt()
                : fallback;
    }

    private static boolean getBoolean(
            JsonObject json,
            String key,
            boolean fallback
    ) {
        JsonElement value = json.get(key);

        return value != null && value.isJsonPrimitive()
                ? value.getAsBoolean()
                : fallback;
    }

    public static boolean isEnabled(){
        return enabled;
    }

    public static void setEnabled(boolean value){
        enabled = value;
    }

    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int value) {
        distance = value;
    }

    public static boolean isPreview(){
        return preview;
    }

    public static int getRed() {
        return red;
    }

    public static int getGreen() {
        return green;
    }

    public static int getBlue() {
        return blue;
    }
    public static int getAlpha() {
        return alpha;
    }

    public static void setPreview(boolean value){
        preview = value;
    }

    public static void setRed(int value) {
        red = clampColor(value);
    }

    public static void setGreen(int value) {
        green = clampColor(value);
    }

    public static void setBlue(int value) {
        blue = clampColor(value);
    }
    public static void setAlpha(int value) {
        alpha = clampColor(value);
    }

    public static void resetColors() {
        red = DEFAULT_RED;
        green = DEFAULT_GREEN;
        blue = DEFAULT_BLUE;
        alpha = DEFAULT_ALPHA;
    }

    private static int clampColor(int value) {
        return Math.max(MIN_COLOR_VALUE, Math.min(MAX_COLOR_VALUE, value));
    }
}

