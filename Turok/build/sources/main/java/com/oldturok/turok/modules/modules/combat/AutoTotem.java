package com.oldturok.turok.module.modules.combat;

import com.oldturok.turok.module.ModuleManager;
import com.oldturok.turok.setting.Settings;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.module.Module;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;

// Rina.
@Module.Info(name = "AutoTotem", description = "Auto off hand Totem.", category = Module.Category.TUROK_COMBAT)
public class AutoTotem extends Module {
	int count;
	boolean item = false;
	boolean move = false;

	@Override
	public void onUpdate() {
		if (mc.currentScreen instanceof GuiContainer) return;

		if (item) {
			int _item = -1;
			for (int item_ = 0; item_ < 45; item_++) {
				if (mc.player.inventory.getStackInSlot(item_).isEmpty) {
					_item = item_;
					break;
				}
			}

			if (_item == -1) {
				return;
			}

			mc.playerController.windowClick(0, _item < 9 ? _item + 36 : _item, 0, ClickType.PICKUP, mc.player);
			item = false;
		}

		count = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

		if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
			if (move) {
				mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
				move = false;
				if (!mc.player.inventory.itemStack.isEmpty()) {
					item = true;
				}

				return;
			}

			if (mc.player.inventory.itemStack.isEmpty()) {
				if (count == 0) return;
				int _item = -1;
				for (int item_ = 0; item_ < 45; item_++) {
					if (mc.player.inventory.getStackInSlot(item_).getItem() == Items.TOTEM_OF_UNDYING) {
						_item = item_;
						break;
					}
				}

				if (_item == -1) {
					return;
				}

				mc.playerController.windowClick(0, _item < 9 ? _item + 36 : _item, 0, ClickType.PICKUP, mc.player);
				move = true;
			}
		}
	}
}