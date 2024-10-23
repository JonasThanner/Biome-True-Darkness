/*
 * This file is part of True Darkness and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.darkness;

import java.awt.*;
import java.io.*;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.neoforged.fml.loading.FMLConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class Darkness {

	public static final String MODID = "darkness";
	public static Logger LOG = LogManager.getLogger("Darkness");

	static boolean darkOverworld;
	static boolean darkDefault;
	static boolean darkNether;
	static double darkNetherFogEffective;
	static double darkNetherFogConfigured;
	static boolean darkEnd;
	static double darkEndFogEffective;
	static double darkEndFogConfigured;
	static boolean darkSkyless;
	static boolean blockLightOnly;
	static boolean ignoreMoonPhase;
	static boolean gradualMoonPhaseDarkness;
	static boolean invertBiomeDarkness;
	static float endBrightnessIncrease;
	static float netherBrightnessIncrease;
	static float skylessBrightnessIncrease;
	static float defaultBrightnessIncrease;
	static float overworldBrightnessIncrease;
	public static JsonObject darknessBiomes;

	//Because we dont want instant turning off/on of the true darkness we slowly fade it in
	//The Values range from 0-100
	//The Values get changed independently from if the darkness calculations are done or not
	static float biomeFadeInAlpha = 0;

	static {

		//Try to load Biome Config File
		Gson gson = new Gson();
		final File biomesFile = getBiomesFile();
		try (FileInputStream inputStream = new FileInputStream(biomesFile);
			 InputStreamReader reader = new InputStreamReader(inputStream)) {
			darknessBiomes = gson.fromJson(reader, JsonObject.class);
		}
		catch (final IOException e) {
			LOG.warn("[Darkness] Could not read property file '" + biomesFile.getAbsolutePath() + "'", e);
			darknessBiomes = new JsonObject();
		}

		//Try to read Biomes JSON
		if(darknessBiomes.has("Biomes"))
		{

		}
		//If theres no Biomes Key => Create one (and Save)
		else
		{
			darknessBiomes.add("Biomes", new JsonArray());
		}

		//Load Normal Config Values from Neoforge Config
		DarknessConfig darknessConfig = DarknessConfig.storedConfig.getLeft();
		ignoreMoonPhase = darknessConfig.ignore_moon_phase.getAsBoolean();
		gradualMoonPhaseDarkness = darknessConfig.gradual_moon_phase_darkness.getAsBoolean();
		blockLightOnly = darknessConfig.only_affect_block_light.getAsBoolean();
		darkOverworld = darknessConfig.dark_overworld.getAsBoolean();
		darkDefault = darknessConfig.dark_default.getAsBoolean();
		darkNether = darknessConfig.dark_nether.getAsBoolean();
		darkEnd = darknessConfig.dark_end.getAsBoolean();
		darkSkyless = darknessConfig.dark_skyless.getAsBoolean();
		invertBiomeDarkness = darknessConfig.invert_biome_darkness.getAsBoolean();
		darkNetherFogConfigured = darknessConfig.dark_nether_fog.getAsDouble();
		darkEndFogConfigured = darknessConfig.dark_end_fog.getAsDouble();

		//Brightness increases
		overworldBrightnessIncrease = (float) darknessConfig.overworld_brightness_increase.getAsDouble();
		defaultBrightnessIncrease = (float) darknessConfig.default_brightness_increase.getAsDouble();
		netherBrightnessIncrease = (float) darknessConfig.nether_brightness_increase.getAsDouble();
		endBrightnessIncrease = (float) darknessConfig.end_brightness_increase.getAsDouble();
		skylessBrightnessIncrease = (float) darknessConfig.skyless_brightness_increase.getAsDouble();

		computeConfigValues();

		saveConfig();
	}

	private static void computeConfigValues() {
		darkNetherFogEffective = darkNether ? darkNetherFogConfigured : 1.0;
		darkEndFogEffective = darkEnd ? darkEndFogConfigured : 1.0;
	}


	private static File getBiomesFile()
	{
		final File configDir = Platform.configDirectory().toFile();

		if (!configDir.exists()) {
			LOG.warn("[Darkness] Could not access configuration directory: " + configDir.getAbsolutePath());
		}

		return new File(configDir, "darkness-biomes.json");
	}

	public static void saveConfig() {
		//Save Biomes List
		Gson gson = new Gson();
		final File biomesFile = getBiomesFile();
		try (FileWriter writer = new FileWriter(biomesFile)) {
			gson.toJson(darknessBiomes, writer);
		}
		catch (final IOException e)
		{
			LOG.warn("[Darkness] Could not store property file '" + biomesFile.getAbsolutePath() + "'", e);
		}
	}

	public static boolean blockLightOnly() {
		return blockLightOnly;
	}

	public static double darkNetherFog() {
		return darkNetherFogEffective;
	}

	public static double darkEndFog() {
		return darkEndFogEffective;
	}

	private static boolean isDark(Level world) {
		final ResourceKey<Level> dimType = world.dimension();

		if (dimType == Level.OVERWORLD) {
			return darkOverworld;
		} else if (dimType == Level.NETHER) {
			return darkNether;
		} else if (dimType == Level.END) {
			return darkEnd;
		} else if (world.dimensionType().hasSkyLight()) {
			return darkDefault;
		} else {
			return darkSkyless;
		}
	}

	private static float skyFactor(Level world) {
		if (!blockLightOnly && isDark(world)) {
			if (world.dimensionType().hasSkyLight()) {
				final float angle = world.getTimeOfDay(0);

				if (angle > 0.25f && angle < 0.75f) {
					final float oldWeight = Math.max(0, (Math.abs(angle - 0.5f) - 0.2f)) * 20;
					final float moon = ignoreMoonPhase ? 0 : world.getMoonBrightness();
					final float moonBrightness = gradualMoonPhaseDarkness ? moon : moon * moon;
					return Mth.lerp(oldWeight * oldWeight * oldWeight, moonBrightness, 1f);
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		} else {
			return 1;
		}
	}

	public static boolean enabled = false;
	private static final float[][] LUMINANCE = new float[16][16];

	public static int darken(int c, int blockIndex, int skyIndex) {
		final float lTarget = LUMINANCE[blockIndex][skyIndex];
		final float r = (c & 0xFF) / 255f;
		final float g = ((c >> 8) & 0xFF) / 255f;
		final float b = ((c >> 16) & 0xFF) / 255f;
		final float l = luminance(r, g, b);
		final float f = l > 0 ? Math.min(1, lTarget / l) : 0;

		return f == 1f ? c : 0xFF000000 | Math.round(f * r * 255) | (Math.round(f * g * 255) << 8) | (Math.round(f * b * 255) << 16);
	}

	public static float luminance(float r, float g, float b) {
		return r * 0.2126f + g * 0.7152f + b * 0.0722f;
	}

	public static void updateLuminance(float tickDelta, Minecraft client, GameRenderer worldRenderer, float prevFlicker) {
		final ClientLevel world = client.level;


		//Calculate the Biome Fade In Alpha
		//Because Alpha goes from 0-100 and tickDelta should be delta/ASecond => TickDelta always adds up to 1 in 1 Second Runtime we need to multiply by 100/Seconds we want
		float fadeInSeconds = 100 / 2;
		biomeFadeInAlpha += (BiomeChecker.ShouldBiomeBeDark(world) ? tickDelta : -tickDelta) * fadeInSeconds;

		//Clamp FadeInAlpha
		biomeFadeInAlpha = Math.max(0, Math.min(100, biomeFadeInAlpha));

		//Exit when the Alpha is too low anyways
		if(biomeFadeInAlpha <= 0)
		{
			enabled = false;
			return;
		}


		if (world != null) {
			if (!isDark(world) || client.player.hasEffect(MobEffects.NIGHT_VISION) || (client.player.hasEffect(MobEffects.CONDUIT_POWER) && client.player.getWaterVision() > 0) || world.getSkyFlashTime() > 0) {
				enabled = false;
				return;
			} else {
				enabled = true;
			}

			float originalDimSkyFactor = Darkness.skyFactor(world);
			//final float dimSkyFactor = originalDimSkyFactor + (1 - (100 / 100)) * (1 - originalDimSkyFactor);
			final float dimSkyFactor = originalDimSkyFactor;
			final float ambient = world.getSkyDarken(1.0F);
			final DimensionType dim = world.dimensionType();
			final boolean blockAmbient = !Darkness.isDark(world);

			for (int skyIndex = 0; skyIndex < 16; ++skyIndex) {
				float skyFactor = 1f - skyIndex / 15f;
				skyFactor = 1 - skyFactor * skyFactor * skyFactor * skyFactor;
				skyFactor *= dimSkyFactor;

				float min = skyFactor * 0.05f;
				final float rawAmbient = ambient * skyFactor;
				final float minAmbient = rawAmbient * (1 - min) + min;
				final float skyBase = LightTexture.getBrightness(dim, skyIndex) * minAmbient;

				min = 0.35f * skyFactor;
				float skyRed = skyBase * (rawAmbient * (1 - min) + min);
				float skyGreen = skyBase * (rawAmbient * (1 - min) + min);
				float skyBlue = skyBase;

				if (worldRenderer.getDarkenWorldAmount(tickDelta) > 0.0F) {
					final float skyDarkness = worldRenderer.getDarkenWorldAmount(tickDelta);
					skyRed = skyRed * (1.0F - skyDarkness) + skyRed * 0.7F * skyDarkness;
					skyGreen = skyGreen * (1.0F - skyDarkness) + skyGreen * 0.6F * skyDarkness;
					skyBlue = skyBlue * (1.0F - skyDarkness) + skyBlue * 0.6F * skyDarkness;
				}

				for (int blockIndex = 0; blockIndex < 16; ++blockIndex) {
					float blockFactor = 1f;

					if (!blockAmbient) {
						blockFactor = 1f - blockIndex / 15f;
						blockFactor = 1 - blockFactor * blockFactor * blockFactor * blockFactor;
					}

					float blockIncreaseFac = 0.0f;
					if(world.dimension() == Level.NETHER)
					{
						blockIncreaseFac = netherBrightnessIncrease;
					} else if (world.dimension() == Level.END)
					{
						blockIncreaseFac = endBrightnessIncrease;
					} else if (!dim.hasSkyLight())
					{
						blockIncreaseFac = skylessBrightnessIncrease;
					} else if (world.dimension() == Level.OVERWORLD)
					{
						blockIncreaseFac = overworldBrightnessIncrease;
					}
					else
					{
						blockIncreaseFac = defaultBrightnessIncrease;
					}

					blockFactor = blockFactor + (1 - (biomeFadeInAlpha / 100)) * (1 - blockFactor) + (blockIncreaseFac * (1 - blockFactor));
					blockFactor = (float) Math.max(0.0f, Math.min(1.0, blockFactor));
					final float blockBase = blockFactor * LightTexture.getBrightness(dim, blockIndex) * (prevFlicker * 0.1F + 1.5F);
					min = 0.4f * blockFactor;
					final float blockGreen = blockBase * ((blockBase * (1 - min) + min) * (1 - min) + min);
					final float blockBlue = blockBase * (blockBase * blockBase * (1 - min) + min);

					float red = skyRed + blockBase;
					float green = skyGreen + blockGreen;
					float blue = skyBlue + blockBlue;

					final float f = Math.max(skyFactor, blockFactor);
					min = 0.03f * f;
					red = red * (0.99F - min) + min;
					green = green * (0.99F - min) + min;
					blue = blue * (0.99F - min) + min;

					if (world.dimension() == Level.END) {
						red = skyFactor * 0.22F + blockBase * 0.75f;
						green = skyFactor * 0.28F + blockGreen * 0.75f;
						blue = skyFactor * 0.25F + blockBlue * 0.75f;
					}

					if (red > 1.0F) {
						red = 1.0F;
					}

					if (green > 1.0F) {
						green = 1.0F;
					}

					if (blue > 1.0F) {
						blue = 1.0F;
					}

					final float gamma = client.options.gamma().get().floatValue() * f;
					float invRed = 1.0F - red;
					float invGreen = 1.0F - green;
					float invBlue = 1.0F - blue;
					invRed = 1.0F - invRed * invRed * invRed * invRed;
					invGreen = 1.0F - invGreen * invGreen * invGreen * invGreen;
					invBlue = 1.0F - invBlue * invBlue * invBlue * invBlue;
					red = red * (1.0F - gamma) + invRed * gamma;
					green = green * (1.0F - gamma) + invGreen * gamma;
					blue = blue * (1.0F - gamma) + invBlue * gamma;

					min = 0.03f * f;
					red = red * (0.99F - min) + min;
					green = green * (0.99F - min) + min;
					blue = blue * (0.99F - min) + min;

					if (red > 1.0F) {
						red = 1.0F;
					}

					if (green > 1.0F) {
						green = 1.0F;
					}

					if (blue > 1.0F) {
						blue = 1.0F;
					}

					if (red < 0.0F) {
						red = 0.0F;
					}

					if (green < 0.0F) {
						green = 0.0F;
					}

					if (blue < 0.0F) {
						blue = 0.0F;
					}

					LUMINANCE[blockIndex][skyIndex] = Darkness.luminance(red, green, blue);
				}
			}
		}
	}
}
