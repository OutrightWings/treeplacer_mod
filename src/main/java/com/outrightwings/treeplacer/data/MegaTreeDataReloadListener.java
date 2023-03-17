package com.outrightwings.treeplacer.data;

import com.outrightwings.treeplacer.growth.TreeOverrideFinder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class MegaTreeDataReloadListener extends SingleTreeDataReloadListener{
    public MegaTreeDataReloadListener(){
        directory = "sapling_overrides/mega";
    }
    @Override
    protected void apply(SaplingOverrides data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        TreeOverrideFinder.initMega(data);
        System.out.println(data);
    }
}
