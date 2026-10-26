package dev.sypex08.visorholster.core.client.gui.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.IntConsumer;

public class IntSlider extends AbstractSliderButton {

    private final int min;
    private final int max;
    private final String valueName;
    private final IntConsumer valueSetter;

    public IntSlider(
            int x,
            int y,
            int width,
            int height,
            int min,
            int max,
            int initialValue,
            String valueName,
            IntConsumer valueSetter
    ) {
        super(
                x,
                y,
                width,
                height,
                Component.empty(),
                normalizedValue(min, max, initialValue)
        );

        this.min = min;
        this.max = max;
        this.valueName = Objects.requireNonNull(valueName, "valueName");
        this.valueSetter = Objects.requireNonNull(valueSetter, "valueSetter");

        updateMessage();
    }

    private static double normalizedValue(int min, int max, int value) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        int clampedValue = Math.max(min, Math.min(max, value));
        return (clampedValue - min) / (double) (max - min);
    }

    private int getIntegerValue() {
        return min + (int) Math.round(value * (max - min));
    }

    @Override
    protected void updateMessage() {
        setMessage(
                Component.literal(
                        valueName + getIntegerValue()
                )
        );
    }

    @Override
    protected void applyValue() {
        int selectedValue = getIntegerValue();

        valueSetter.accept(selectedValue);
    }
}
