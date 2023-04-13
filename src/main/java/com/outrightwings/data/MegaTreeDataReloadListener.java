package com.outrightwings.data;

import com.outrightwings.growth.TreeOverrideFinder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class MegaTreeDataReloadListener extends SingleTreeDataReloadListener{
    public MegaTreeDataReloadListener(){
        directory = "sapling_overrides/mega";
    }
    @Override
    protected void apply(SaplingOverrides data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        TreeOverrideFinder.initMega(data);
        //System.out.println(data);
    }
    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("treeplacer:mega_tree_data");
    }
}
