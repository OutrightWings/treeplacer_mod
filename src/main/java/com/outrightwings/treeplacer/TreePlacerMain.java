package com.outrightwings.treeplacer;

import com.mojang.logging.LogUtils;
import com.outrightwings.treeplacer.data.MegaTreeDataReloadListener;
import com.outrightwings.treeplacer.data.SingleTreeDataReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TreePlacerMain.MODID)
public class TreePlacerMain
{
    public static final String MODID = "treeplacer";
    private static final Logger LOGGER = LogUtils.getLogger();
    public TreePlacerMain()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
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
