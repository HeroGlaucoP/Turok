package com.oldturok.turok.module.modules.misc;

import com.oldturok.turok.module.Module;

@Module.Info(name = "NoPackKick", category = Module.Category.TUROK_MISC)
public class NoPackKick {
	private static NoPackKick INSTANCE;

	public NoPackKick() {
		INSTANCE = this;
	}

	public static boolean isEnabled() {
		return INSTANCE.isEnabled();
	}
}