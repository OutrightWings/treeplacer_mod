package com.outrightwings;

import com.outrightwings.data.FabricDataReloaders;
import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class FabricMain implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonMain.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricDataReloaders.FabricSingleTreeDataReloadListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricDataReloaders.FabricMegaTreeDataReloadListener());
    }
}
