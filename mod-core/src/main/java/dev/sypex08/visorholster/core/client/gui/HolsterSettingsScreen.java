package dev.sypex08.visorholster.core.client.gui;

import dev.sypex08.visorholster.core.client.gui.widgets.IntSlider;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.vmstudio.visor.api.compatibility.mcversion.McVersionClientUtils;

public class HolsterSettingsScreen extends Screen {

    private final Screen parent;

    public HolsterSettingsScreen(Screen parent) {
        super(Component.translatable("visor_holster.settings.title"));

        this.parent = parent;
    }

    @Override
    protected void init() {
        int widgetWidth = Math.min(220, this.width - 40);
        int left = (this.width - widgetWidth) / 2;
        int rowHeight = 24;
        int top = Math.max(20, this.height / 2 - 90);

        addRenderableWidget(new IntSlider(
                left, top -rowHeight, widgetWidth, 20,
                -100, 100, (int) HolsterClientSettings.getHeight(),
                "Height: ", HolsterClientSettings::setHeight
        ));

        addRenderableWidget(new IntSlider(
                left, top, widgetWidth, 20,
                0, 255, HolsterClientSettings.getRed(),
                "R: ", HolsterClientSettings::setRed
        ));

        addRenderableWidget(new IntSlider(
                left, top + rowHeight, widgetWidth, 20,
                0, 255, HolsterClientSettings.getGreen(),
                "G: ", HolsterClientSettings::setGreen
        ));

        addRenderableWidget(new IntSlider(
                left, top + rowHeight * 2, widgetWidth, 20,
                0, 255, HolsterClientSettings.getBlue(),
                "B: ", HolsterClientSettings::setBlue
        ));

        addRenderableWidget(new IntSlider(
                left, top + rowHeight * 3, widgetWidth, 20,
                0, 255, HolsterClientSettings.getAlpha(),
                "A: ", HolsterClientSettings::setAlpha
        ));

        final Button previewButton = Button.builder(
                        previewMessage(),
                        button -> {
                            HolsterClientSettings.setPreview(
                                    !HolsterClientSettings.isPreview()
                            );
                            button.setMessage(previewMessage());
                        }
                )
                .bounds(left, top + rowHeight * 4 + 12, widgetWidth, 20)
                .build();
        addRenderableWidget(previewButton);

        addRenderableWidget(Button.builder(
                        Component.translatable("visor_holster.settings.reset_colors"),
                        button -> {
                            HolsterClientSettings.resetColors();
                            rebuildWidgets();
                        }
                )
                .bounds(left, top + rowHeight * 5 + 16, widgetWidth, 20)
                .build());

        addRenderableWidget(Button.builder(
                        editModeMessage(),
                        button -> {
                            HolsterClientSettings.setEditMode(
                                    !HolsterClientSettings.isEditMode()
                            );
                            button.setMessage(editModeMessage());
                        }
                )
                .bounds(left, top + rowHeight * 6 + 16, widgetWidth, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.translatable("visor_holster.settings.reset_positions"),
                        button -> {
                            HolsterClientSettings.resetHolsterPositions();
                            HolsterClientSettings.save();
                        }
                )
                .bounds(left, top + rowHeight * 7 + 16, widgetWidth, 20)
                .build());
    }

    private static Component editModeMessage() {
        return Component.translatable(
                HolsterClientSettings.isEditMode()
                        ? "visor_holster.settings.edit_mode_on"
                        : "visor_holster.settings.edit_mode_off"
        );
    }

    private static Component previewMessage() {
        return Component.translatable(
                HolsterClientSettings.isPreview() ? "visor_holster.settings.preview_on" : "visor_holster.settings.preview_off"
        );
    }

    @Override
    public void onClose() {
        McVersionClientUtils.setScreen(parent);
        HolsterClientSettings.save();

    }
}
