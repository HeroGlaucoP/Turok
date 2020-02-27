package com.oldturok.turok.module.modules.render;

import com.oldturok.turok.module.Module;

/**
 * By Soulbond.
 */
@Module.Info(name = "AntiWeather", description = "Removes rain from your world", category = Module.Category.TUROK_RENDER)
public class AntiWeather extends Module {

    @Override
    public void onUpdate() {
        if (isDisabled()) return;
        if (mc.world.isRaining())
            mc.world.setRainStrength(0);
    }
}