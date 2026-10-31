package dev.sypex08.visorholster.core.client.render;

import java.util.List;

public record HolsterPosition(
        double sideDistance,
        double forwardDistance,
        double height
) {

    public static final List<HolsterPosition> POSITIONS = List.of(
            new HolsterPosition(-0.17, 0.33, 0.65),
            new HolsterPosition( 0.17, 0.33, 0.65),
            new HolsterPosition(-0.37, 0.18, 0.65),
            new HolsterPosition( 0.37, 0.18, 0.65)
    );


}
