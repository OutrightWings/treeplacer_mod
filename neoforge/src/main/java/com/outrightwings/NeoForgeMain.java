package com.outrightwings;

import com.outrightwings.command.ListCommand;
import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class NeoForgeMain {
    
    public NeoForgeMain(IEventBus modEventBus)
    {
        CommonMain.init();
        NeoForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onResourceReload(final AddServerReloadListenersEvent event){
        event.addListener(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"single_tree_data"),new SingleTreeDataReloadListener());
        event.addListener(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID,"mega_tree_data"),new MegaTreeDataReloadListener());
    }
    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        ListCommand.register(event.getDispatcher());
    }
}