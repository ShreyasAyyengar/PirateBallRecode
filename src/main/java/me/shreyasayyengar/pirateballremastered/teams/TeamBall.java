package me.shreyasayyengar.pirateballremastered.teams;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.shreyasayyengar.pirateballremastered.utils.Utility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TeamBall {

    private final ItemStack ball;

    public TeamBall(TeamBallInfo info) {
        ItemStack ball = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta ballSkullMeta = (SkullMeta) ball.getItemMeta();
        GameProfile gameProfile = info.getProfile();
        List<String> lore = Arrays.asList(Utility.colourise("&7This ball is 1 of 4 balls that "), info.getLore());

        gameProfile.getProperties().put("textures", new Property("textures", info.getTexture()));

        Field field;
        try {
            field = ballSkullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(ballSkullMeta, gameProfile);
        } catch (ReflectiveOperationException x) {
            x.printStackTrace();
        }

        ballSkullMeta.setDisplayName(info.getDisplayName());
        ballSkullMeta.setLore(lore);
        ball.setItemMeta(ballSkullMeta);

        this.ball = ball;
    }


    public ItemStack getBall() {
        return ball;
    }
}
