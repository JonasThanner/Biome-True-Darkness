package grondag.darkness;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Properties;

public class BiomeChecker {
    public static Biome GetCurrentBiome() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            Level world = minecraft.player.level();
            BlockPos playerPos = minecraft.player.blockPosition();
            Biome biome = world.getBiome(playerPos).value();
            return biome;
        }

        return null;
    }

    public static boolean ShouldBiomeBeDark(ClientLevel world)
    {
        //Check if the Biome Should be dark
        //Get Biome string
        ResourceLocation biomeWeAreIn = world.registryAccess().registryOrThrow(Registries.BIOME).getKey(BiomeChecker.GetCurrentBiome());
        ResourceLocation forgeBiomeWeAreIn = ForgeRegistries.BIOMES.getKey(BiomeChecker.GetCurrentBiome()); //undermod registers it's biomes in the forge BIOME registry rather than the vanilla biome registry, as such we must check there as well
        JsonArray array = Darkness.darknessBiomes.getAsJsonArray("Biomes");
        boolean insideSpecifiedBiome = false;

        for (JsonElement element : array) {
            if (biomeWeAreIn != null) {
                if (element.getAsString().equals(biomeWeAreIn.toString())) {//check vanilla's biome registry for the current biome
                    insideSpecifiedBiome = true;
                    break;
                }
            }
            if (forgeBiomeWeAreIn != null) { //check forge's biome registry for the current biome
                if (element.getAsString().equals(forgeBiomeWeAreIn.toString())) {
                    insideSpecifiedBiome = true;
                    break;
                }
            }
        }

        return Darkness.invertBiomeDarkness ? insideSpecifiedBiome : !insideSpecifiedBiome;
    }
}
