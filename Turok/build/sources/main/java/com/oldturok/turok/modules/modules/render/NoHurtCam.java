package com.oldturok.turok.module.modules.render;

import com.oldturok.turok.module.Module;

// Update by Rina 09/03/20.
@Module.Info(name = "No Hurt Camera", category = Module.Category.TUROK_RENDER, description = "Disables the 'hurt' camera effect")
public class NoHurtCam extends Module {

    private static NoHurtCam INSTANCE;

    public NoHurtCam() {
        INSTANCE = this;
    }

    public static boolean shouldDisable() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

}