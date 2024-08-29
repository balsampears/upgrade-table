package com.balsam.upgradetable;

import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.itemAbility.BowItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.itemAbility.TieredItemAbility;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod.EventBusSubscriber
public class BusEventHandler {
    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) throws ClassNotFoundException {
        Item item = event.getObject().getItem();
        IItemAbility itemAbility = null;
        if (item instanceof TieredItem) {
            itemAbility = new TieredItemAbility();
        }
        else if (item instanceof BowItem) {
            itemAbility = new BowItemAbility();
        }

        if (itemAbility!=null){
            event.addCapability(new ResourceLocation(String.format("cap.%s.upgrade", Constants.MOD_ID)),
                    new ItemAbilityProvider(itemAbility));
        }
    }

}
