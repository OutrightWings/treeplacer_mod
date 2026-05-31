package com.outrightwings;

import com.outrightwings.command.ListCommand;
import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class NeoForgeMain {
    
    public NeoForgeMain(IEventBus modEventBus)
    {
        CommonMain.init();
        NeoForge.EVENT_BUS.register(this);
        //modEventBus.addListener(this::registerCommands);
    }
    @SubscribeEvent
    public void onResourceReload(final AddReloadListenerEvent event){
        event.addListener(new SingleTreeDataReloadListener());
        event.addListener(new MegaTreeDataReloadListener());
    }
    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        ListCommand.register(event.getDispatcher());
    }
}