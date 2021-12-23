package me.shreyasayyengar.pirateballremastered.teams;

import com.mojang.authlib.GameProfile;
import me.shreyasayyengar.pirateballremastered.utils.Utility;

import java.util.UUID;

public enum TeamBallInfo {

    RED(
            Utility.colourise("&cRed Ball"),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzYTViZmM4YTJhM2ExNTJkNjQ2YTViZWE2OTRhNDI1YWI3OWRiNjk0YjIxNGYxNTZjMzdjNzE4M2FhIn19fQ",
            new GameProfile(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), null),
            Utility.colourise("&7belongs to the &cred &7team.")
    ),

    BLUE(
            Utility.colourise("&9Blue Ball"),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDExMzdiOWJmNDM1YzRiNmI4OGZhZWFmMmU0MWQ4ZmQwNGUxZDk2NjNkNmY2M2VkM2M2OGNjMTZmYzcyNCJ9fX0",
            new GameProfile(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), null),
            Utility.colourise("&7belongs to the &9blue &7team.")
    ),

    YELLOW(
            Utility.colourise("&eYellow Ball"),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExMzliM2VmMmU0YzQ0YTRjOTgzZjExNGNiZTk0OGQ4YWI1ZDRmODc5YTVjNjY1YmI4MjBlNzM4NmFjMmYifX19",
            new GameProfile(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), null),
            Utility.colourise("&7belongs to the &eyellow &7team.")
    ),

    GREEN(
            Utility.colourise("&2Green Ball"),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU0ODRmNGI2MzY3Yjk1YmIxNjI4ODM5OGYxYzhkZDZjNjFkZTk4OGYzYTgzNTZkNGMzYWU3M2VhMzhhNDIifX19",
            new GameProfile(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"), null),
            Utility.colourise("&7belongs to the &2green &7team.")
    );

    private final String displayName;
    private final String texture;
    private final GameProfile profile;
    private final String lore;

    TeamBallInfo(String displayName, String texture, GameProfile profile, String lore) {
        this.displayName = displayName;
        this.texture = texture;
        this.profile = profile;
        this.lore = lore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTexture() {
        return texture;
    }

    public GameProfile getProfile() {
        return profile;
    }

    public String getLore() {
        return lore;
    }
}
