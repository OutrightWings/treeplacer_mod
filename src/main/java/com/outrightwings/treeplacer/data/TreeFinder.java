package com.outrightwings.treeplacer.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class TreeFinder {
    private static TreeDataReloadListener.SaplingOverrides saplingOverrides;
    public static void init(TreeDataReloadListener.SaplingOverrides overrides){
        saplingOverrides = overrides;
    }
    public static void GetBiomeBasedTreeFeature(ServerLevel level, BlockState state, BlockPos pos){
        ResourceLocation saplingName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        ResourceLocation biomeName = getBiome(level.getBiome(pos));

        String featureID = saplingOverrides.getFeatureID(saplingName.toString(),biomeName.toString());


    }

    //Stole and modified DebugScreen's method
    private static ResourceLocation getBiome(Holder<Biome> biome) {
        return biome.unwrap().map((resourceKey) -> {
            return resourceKey.location();
        }, (biome1) -> {
            return null;
        });
    }
}
