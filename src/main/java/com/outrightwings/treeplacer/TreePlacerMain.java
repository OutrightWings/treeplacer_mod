package com.outrightwings.treeplacer;

import com.mojang.logging.LogUtils;
import com.outrightwings.treeplacer.data.MegaTreeDataReloadListener;
import com.outrightwings.treeplacer.data.SingleTreeDataReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;

@Mod(TreePlacerMain.MODID)
public class TreePlacerMain
{
    public static final String MODID = "treeplacer";
    private static final Logger LOGGER = LogUtils.getLogger();
    public TreePlacerMain(IEventBus modEventBus, ModContainer modContainer)
    {

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onResourceReload(final AddReloadListenerEvent event){
        event.addListener(new SingleTreeDataReloadListener());
        event.addListener(new MegaTreeDataReloadListener());
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }
}
