package grondag.darkness;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DarknessConfig
{

    public static Pair<DarknessConfig, ModConfigSpec> storedConfig;
    public final static ModConfigSpec modConfigSpec;
    public final ModConfigSpec.BooleanValue only_affect_block_light;
    public final ModConfigSpec.BooleanValue ignore_moon_phase;
    public final ModConfigSpec.BooleanValue gradual_moon_phase_darkness;
    public final ModConfigSpec.BooleanValue dark_overworld;
    public final ModConfigSpec.BooleanValue dark_default;
    public final ModConfigSpec.BooleanValue dark_nether;
    public final ModConfigSpec.DoubleValue dark_nether_fog;
    public final ModConfigSpec.BooleanValue dark_end;
    public final ModConfigSpec.DoubleValue dark_end_fog;
    public final ModConfigSpec.BooleanValue dark_skyless;
    public final ModConfigSpec.BooleanValue invert_biome_darkness;
    public final ModConfigSpec.DoubleValue nether_brightness_increase;
    public final ModConfigSpec.DoubleValue end_brightness_increase;
    public final ModConfigSpec.DoubleValue overworld_brightness_increase;
    public final ModConfigSpec.DoubleValue default_brightness_increase;
    public final ModConfigSpec.DoubleValue skyless_brightness_increase;



    //Main Config Files
    DarknessConfig(ModConfigSpec.Builder builder) {

        //Only Affect Block Light
        only_affect_block_light = builder.comment("Only Affect Block Light").translation("config.darkness.only_affect_block_light")
                .define("only_affect_block_light", false);

        ignore_moon_phase = builder.comment("Ignore Moon Phase").translation("config.darkness.ignore_moon_phase")
                .define("ignore_moon_phase", false);

        gradual_moon_phase_darkness = builder.comment("Gradual Moon Phase Darkness. Old default behavior was false and is a much more harsh change in darkness between moon phases").translation("config.darkness.gradual_moon_phase_darkness")
                .define("gradual_moon_phase_darkness", true);

        dark_overworld = builder.comment("Should the Overworld have True Darkness").translation("config.darkness.dark_overworld")
                .define("dark_overworld", true);

        dark_default = builder.comment("Should the default for worlds be True Darkness").translation("config.darkness.dark_default")
                .define("dark_default", true);

        dark_nether = builder.comment("Should the nether have True Darkness").translation("config.darkness.dark_nether")
                .define("dark_nether", false);

        dark_nether_fog = builder.comment("Nether Fog Darkness").translation("config.darkness.dark_nether_fog")
                .defineInRange("dark_nether_fog", 0.5, 0, 1);

        dark_end = builder.comment("Should the end have True Darkness").translation("config.darkness.dark_end")
                .define("dark_end", false);

        dark_end_fog = builder.comment("Nether Fog Darkness").translation("config.darkness.dark_end_fog")
                .defineInRange("dark_end_fog", 0.0, 0, 1);

        dark_skyless = builder.comment("Should skyless dimensions have True Darkness").translation("config.darkness.dark_skyless")
                .define("dark_skyless", true);

        invert_biome_darkness = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.invert_biome_darkness")
                .define("invert_biome_darkness", false);

        overworld_brightness_increase = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.increase_overworld_brightness")
                .defineInRange("increase_overworld_brightness", 0.0, 0, 1);

        nether_brightness_increase = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.increase_nether_brightness")
                .defineInRange("increase_nether_brightness", 0.0, 0, 1);

        end_brightness_increase = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.increase_end_brightness")
                .defineInRange("increase_end_brightness", 0.0, 0, 1);

        skyless_brightness_increase = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.increase_skyless_brightness")
                .defineInRange("increase_skyless_brightness", 0.0, 0, 1);

        default_brightness_increase = builder.comment("Should the Darkness Biome behavior be inverted? Aka should biomes in the json be the only dark ones instead of the only bright ones?").translation("config.darkness.increase_default_brightness")
                .defineInRange("increase_default_brightness", 0.0, 0, 1);


    }

    // Somewhere the constructor is accessible
    static {
        storedConfig = new ModConfigSpec.Builder()
                .configure(DarknessConfig::new);
        modConfigSpec = storedConfig.getRight();

    }
}
