package com.oldturok.turok.module.modules.movement;

import com.oldturok.turok.setting.Settings;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.module.Module;

// Rina.
@Module.Info(name = "Timer", description = "For modify the timer.", category = Module.Category.TUROK_MOVEMENT)
public class Timer extends Module {
	private Setting<Integer> ticks_speed_value = register(Settings.integerBuilder("Time Value").withMinimum(1).withValue(4).withMaximum(20));

	@Override
	public void onDisable() {
		mc.timer.tickLength = 50;
	}

	@Override
	public void onUpdate() {
		mc.timer.tickLength = (50.0f / ticks_speed_value.getValue());
	}

	@Override
	public String getHudInfo() {
		return String.valueOf(ticks_speed_value.getValue());
	}
}