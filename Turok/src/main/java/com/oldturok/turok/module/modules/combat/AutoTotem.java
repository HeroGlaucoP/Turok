package com.oldturok.turok.module.modules.combat;

import com.oldturok.turok.module.Module;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

// Modify by Rina 05/03/20.
@Module.Info(name = "AutoTotem", category = Module.Category.TUROK_COMBAT)
public class AutoTotem extends Module {
    private Setting<Boolean> soft = register(Settings.b("Soft mode", false));
    int totems;
    boolean moving = false;
    boolean return_ = false;    

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) return;
        if (return_) {
            int t = -1;
            for (int i = 0; i < 45; i++)
                if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                    t = i;
                    break;
                }
            if (t == -1) return;
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            return_ = false;
        }

        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems++; 
        } else {
            if (soft.getValue() && !mc.player.getHeldItemOffhand().isEmpty) return;
            if (moving) {
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                moving = false;
                if (!mc.player.inventory.itemStack.isEmpty()) return_ = true;
                return;
            }

            if (mc.player.inventory.itemStack.isEmpty()) {
                if (totems == 0) return;
                int t = -1;
                for (int i = 0; i < 45; i++)
                    if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        t = i;
                        break;
                    }
                if (t == -1) return;
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
                moving = true;
            } else if (!soft.getValue()) {
                int t = -1;
                for (int i = 0; i < 45; i++)
                    if (mc.player.inventory.getStackInSlot(i).isEmpty) {
                        t = i;
                        break;
                    }

                if (t == -1) return;
                mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(totems);
    }
}