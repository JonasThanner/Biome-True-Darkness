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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DarknessConfigScreen extends Screen {
	protected final Screen parent;

	protected Checkbox blockLightOnlyWidget;
	protected Checkbox ignoreMoonPhaseWidget;
	protected Checkbox gradualMoonPhaseDarkness;
	protected Checkbox darkOverworldWidget;
	protected Checkbox darkNetherWidget;
	protected Checkbox darkEndWidget;
	protected Checkbox darkDefaultWidget;
	protected Checkbox darkSkylessWidget;

	public DarknessConfigScreen(Screen parent) {
		super(Component.literal("config.darkness.title"));
		this.parent = parent;
	}

	@Override
	public void removed() {
		Darkness.saveConfig();
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	protected void init() {
		int i = 23;
		blockLightOnlyWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.block_light_only"), Darkness.blockLightOnly) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHoveredOrFocused()) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.block_light_only"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		ignoreMoonPhaseWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.ignore_moon_phase"), Darkness.ignoreMoonPhase) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.ignore_moon_phase"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		gradualMoonPhaseDarkness = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.gradual_moon_phase_darkness"), Darkness.gradualMoonPhaseDarkness) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.gradual_moon_phase_darkness"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		darkOverworldWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.dark_overworld"), Darkness.darkOverworld) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.dark_overworld"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		darkNetherWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.dark_nether"), Darkness.darkNether) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.dark_nether"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		darkEndWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.dark_end"), Darkness.darkEnd) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.dark_end"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		darkDefaultWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.dark_default"), Darkness.darkDefault) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.dark_default"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		darkSkylessWidget = new Checkbox(width / 2 - 100, i, 200, 20, Component.literal("config.darkness.label.dark_skyless"), Darkness.darkSkyless) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
				super.renderWidget(guiGraphics, mouseX, mouseY, delta);

				if (isHovered) {
					guiGraphics.renderTooltip(DarknessConfigScreen.this.minecraft.font, Component.literal("config.darkness.help.dark_skyless"), mouseX, mouseY);
				}
			}
		};

		i += 23;

		addRenderableWidget(blockLightOnlyWidget);
		addRenderableWidget(ignoreMoonPhaseWidget);
		addRenderableWidget(gradualMoonPhaseDarkness);
		addRenderableWidget(darkOverworldWidget);
		addRenderableWidget(darkNetherWidget);
		addRenderableWidget(darkEndWidget);
		addRenderableWidget(darkDefaultWidget);
		addRenderableWidget(darkSkylessWidget);

		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (buttonWidget) -> {
			Darkness.blockLightOnly = blockLightOnlyWidget.selected();
			Darkness.ignoreMoonPhase = ignoreMoonPhaseWidget.selected();
			Darkness.gradualMoonPhaseDarkness = gradualMoonPhaseDarkness.selected();
			Darkness.darkOverworld = darkOverworldWidget.selected();
			Darkness.darkNether = darkNetherWidget.selected();
			Darkness.darkEnd = darkEndWidget.selected();
			Darkness.darkDefault = darkDefaultWidget.selected();
			Darkness.darkSkyless = darkSkylessWidget.selected();
			Darkness.saveConfig();
			minecraft.setScreen(parent);
		}).bounds(width / 2 - 100, height - 23, 200, 20).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		renderBackground(guiGraphics, i, j, f);
		guiGraphics.drawCenteredString(this.minecraft.font, this.title, this.width / 2, 5, 16777215);

		super.render(guiGraphics, i, j, f);
	}
}
