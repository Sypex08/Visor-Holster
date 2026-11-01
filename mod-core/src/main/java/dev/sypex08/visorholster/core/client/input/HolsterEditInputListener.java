package dev.sypex08.visorholster.core.client.input;

import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import org.vmstudio.visor.api.client.events.input.ActionButtonVREvent;
import org.vmstudio.visor.api.common.eventbus.listener.VREventHandler;
import org.vmstudio.visor.api.common.eventbus.listener.VREventListener;
import org.vmstudio.visor.api.compatibility.mcversion.McVersionClientUtils;

public final class HolsterEditInputListener implements VREventListener {

    @VREventHandler
    public void onActionButton(ActionButtonVREvent event) {
        if (!HolsterClientSettings.isEditMode()
                || McVersionClientUtils.screen() != null) {
            return;
        }

        String actionId = event.getActionButton().getId();
        if ("mouse_right_main".equals(actionId)
                || "mouse_right_offhand".equals(actionId)) {
            event.setCanceled(true);
        }
    }
}