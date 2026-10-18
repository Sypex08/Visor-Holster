package dev.sypex08.visorholster.core.common.holster;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolsterManager {

    private final Map<UUID, HolsterState> states = new HashMap<>();

    public HolsterState get(UUID id) { return states.computeIfAbsent(id, k -> new HolsterState());}
    public void onPlayerJoin(UUID id, HolsterState state){ states.put(id, state);}
    public void onPlayerLeave(UUID id) { states.remove(id);}

    //server side

    public void interact(Player player){}





}
