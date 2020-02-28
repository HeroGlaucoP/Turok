package com.oldturok.turok.module.modules.render;

import com.oldturok.turok.module.Module;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.setting.Settings;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.Random;

/**
 * By RaindDrop.
 */
@Module.Info(name = "SkinFlicker", description = "Toggle your skin layers rapidly for a cool skin effect", category = Module.Category.TUROK_MISC)
public class SkinFlicker extends Module {
    private Setting<FlickerMode> mode = register(Settings.e("Mode", FlickerMode.HORIZONTAL));
    private Setting<Integer> slowness = register(Settings.integerBuilder().withName("Slowness").withValue(2).withMinimum(1).build());

    private final static EnumPlayerModelParts[] PARTS_HORIZONTAL = new EnumPlayerModelParts[]{
            EnumPlayerModelParts.LEFT_SLEEVE,
            EnumPlayerModelParts.JACKET,
            EnumPlayerModelParts.HAT,
            EnumPlayerModelParts.LEFT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_SLEEVE
    };

    private final static EnumPlayerModelParts[] PARTS_VERTICAL = new EnumPlayerModelParts[]{
            EnumPlayerModelParts.HAT,
            EnumPlayerModelParts.JACKET,
            EnumPlayerModelParts.LEFT_SLEEVE,
            EnumPlayerModelParts.RIGHT_SLEEVE,
            EnumPlayerModelParts.LEFT_PANTS_LEG,
            EnumPlayerModelParts.RIGHT_PANTS_LEG,
    };

    private Random r = new Random();
    private int len = EnumPlayerModelParts.values().length;

    @Override
    public void onUpdate() {
        switch (mode.getValue()) {
            case RANDOM:
                if (mc.player.ticksExisted % slowness.getValue() != 0) return;
                mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[r.nextInt(len)]);
                break;
            case VERTICAL:
            case HORIZONTAL:
                int i = (mc.player.ticksExisted / slowness.getValue()) % (PARTS_HORIZONTAL.length * 2); // 2 for // on/off
                boolean on = false;
                if (i >= PARTS_HORIZONTAL.length) {
                    on = true;
                    i -= PARTS_HORIZONTAL.length;
                }
                mc.gameSettings.setModelPartEnabled(mode.getValue() == FlickerMode.VERTICAL ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], on);
        }
    }

    public static enum FlickerMode {
        HORIZONTAL, VERTICAL, RANDOM
    }

}
