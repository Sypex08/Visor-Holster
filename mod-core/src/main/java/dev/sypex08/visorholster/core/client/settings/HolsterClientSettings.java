package dev.sypex08.visorholster.core.client.settings;

import com.google.gson.*;
import dev.sypex08.visorholster.core.client.render.HolsterPosition;
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

    private static float height = 0;
    private static final int maxHeight = 100;
    private static final int minHeight = -100;

    private static int holsterCount = 4;


    private static int red = DEFAULT_RED;
    private static int green = DEFAULT_GREEN;
    private static int blue = DEFAULT_BLUE;
    private static int alpha = DEFAULT_ALPHA;

    private static boolean preview = false;
    private static boolean editMode = false;

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

            setHeight(getInt(json, "height", 0));
            resetHolsterPositions();
            loadHolsterPositions(json);

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
        json.addProperty("height", height);
        JsonArray positionArray = new JsonArray();
        for (HolsterPosition position : holsterPositions) {
            JsonObject positionJson = new JsonObject();
            positionJson.addProperty("sideDistance", position.sideDistance());
            positionJson.addProperty("forwardDistance", position.forwardDistance());
            positionJson.addProperty("height", position.height());
            positionArray.add(positionJson);
        }
        json.add("holsterPositions", positionArray);

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

    private static double getDouble(JsonObject json, String key, double fallback) {
        JsonElement value = json.get(key);
        if (value == null || !value.isJsonPrimitive()) {
            return fallback;
        }
        try {
            double parsed = value.getAsDouble();
            return Double.isFinite(parsed) ? parsed : fallback;
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private static void loadHolsterPositions(JsonObject json) {
        JsonElement value = json.get("holsterPositions");
        if (value == null || !value.isJsonArray()) {
            return;
        }

        JsonArray positionArray = value.getAsJsonArray();
        int count = Math.min(positionArray.size(), holsterPositions.length);
        for (int slot = 0; slot < count; slot++) {
            JsonElement element = positionArray.get(slot);
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject positionJson = element.getAsJsonObject();
            HolsterPosition fallback = DEFAULT_HOLSTER_POSITIONS[slot];
            holsterPositions[slot] = new HolsterPosition(
                    getDouble(positionJson, "sideDistance", fallback.sideDistance()),
                    getDouble(positionJson, "forwardDistance", fallback.forwardDistance()),
                    getDouble(positionJson, "height", fallback.height())
            );
        }
    }

    private static final HolsterPosition[] DEFAULT_HOLSTER_POSITIONS = {
            new HolsterPosition(-0.17, 0.33, 0.65),
            new HolsterPosition( 0.17, 0.33, 0.65),
            new HolsterPosition(-0.37, 0.18, 0.65),
            new HolsterPosition( 0.37, 0.18, 0.65)
    };

    private static final HolsterPosition[] holsterPositions =
            DEFAULT_HOLSTER_POSITIONS.clone();


    public static int getHolsterCount() {
        return holsterCount;
    }

    public static void setHolsterCount(int value){
        holsterCount = value;
    }

    public static int getHolsterPositionCount() {
        return holsterPositions.length;
    }

    public static HolsterPosition getHolsterPosition(int slot) {
        return holsterPositions[slot];
    }

    public static void setHolsterPosition(int slot, HolsterPosition position) {
        holsterPositions[slot] = position;
    }

    public static void resetHolsterPositions() {
        System.arraycopy(
                DEFAULT_HOLSTER_POSITIONS, 0,
                holsterPositions, 0,
                holsterPositions.length
        );
    }



    public static boolean isEditMode() {
        return editMode;
    }

    public static void setEditMode(boolean value) {
        editMode = value;
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

    public static void setHeight(int value){
        height = value;
    }

    public static float getHeight(){return height;}

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

