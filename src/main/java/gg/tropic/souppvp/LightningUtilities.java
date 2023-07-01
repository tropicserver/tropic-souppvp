package gg.tropic.souppvp;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author GrowlyX
 * @since 6/30/2023
 */
public class LightningUtilities {

    /**
     * Lightning through Protocol Lib cuz we care about the
     * environment
     *
     * @param location {@link Location} where the lightning should spawn
     */
    public static void spawnLightning(@NotNull Player player, @NotNull Location location) {
        PacketContainer lightningPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_WEATHER);

        lightningPacket.getModifier().writeDefaults();
        lightningPacket.getIntegers().write(0, 128);
        lightningPacket.getIntegers().write(4, 1);
        lightningPacket.getIntegers().write(1, (int) (location.getX() * 32.0));
        lightningPacket.getIntegers().write(2, (int) (location.getY() * 32.0));
        lightningPacket.getIntegers().write(3, (int) (location.getZ() * 32.0));

        try {
            ProtocolLibrary.getProtocolManager()
                .sendServerPacket(player, lightningPacket);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        float thunderSoundPitch = 0.8f + ThreadLocalRandom.current().nextFloat() * 0.2f;
        float explodeSoundPitch = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f;

        player.playSound(location, Sound.AMBIENCE_THUNDER, 10000.0f, thunderSoundPitch);
        player.playSound(location, Sound.EXPLODE, 2.0f, explodeSoundPitch);
    }
}
