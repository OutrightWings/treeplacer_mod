package com.outrightwings;

import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ForgeMain {
    
    public ForgeMain() {
        CommonMain.init();
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onResourceReload(final AddReloadListenerEvent event){
        event.addListener(new SingleTreeDataReloadListener());
        event.addListener(new MegaTreeDataReloadListener());
    }
}