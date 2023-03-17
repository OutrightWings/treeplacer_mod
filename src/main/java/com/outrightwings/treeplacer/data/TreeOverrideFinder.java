package com.outrightwings.treeplacer.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

public class TreeOverrideFinder {
    private static SaplingOverrides singleSaplingOverrides;
    private static SaplingOverrides megaSaplingOverrides;
    public static void initSingle(SaplingOverrides overrides){singleSaplingOverrides=overrides;}
    public static void initMega(SaplingOverrides overrides){megaSaplingOverrides=overrides;}

    public static Holder<? extends ConfiguredFeature<?, ?>> GetBiomeBasedTreeFeature(ServerLevel level, BlockState state, BlockPos pos, boolean isMega){
        Holder<ConfiguredFeature<?, ?>> holder = null;

        ResourceLocation saplingName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        ResourceLocation biomeName = getResourceLocationFromHolder(level.getBiome(pos));

        String featureID;
        if(isMega){
            featureID = megaSaplingOverrides.getFeatureID(saplingName.toString(),biomeName.toString());
        }else{
            featureID = singleSaplingOverrides.getFeatureID(saplingName.toString(),biomeName.toString());
        }
        System.out.println((featureID != null ? "Override found: " + featureID : "no override found") + " for " + saplingName + " in " + biomeName);

        //Biome Specific
        if(featureID != null) {
            ResourceLocation featureName = new ResourceLocation(featureID);
            holder = getConfiguredFeature(level,featureName);
        } else {
            //Check if general override

        }
        return holder;
    }

    //Stole and modified DebugScreen's method
    private static ResourceLocation getResourceLocationFromHolder(Holder<?> holder) {
        return holder.unwrap().map(ResourceKey::location, (empty) -> null);
    }

    //Stole and modified from ResourceKeyArgument thats used by placeCommand
    private static <T> Registry<T> getRegistry(ServerLevel level, ResourceKey<? extends Registry<T>> key){
        return level.getServer().registryAccess().registryOrThrow(key);
    }
    private static <T> Holder<T> getRegistryKeyType(ServerLevel level, ResourceLocation featureName, ResourceKey<Registry<T>> registryResourceKey){
        ResourceKey<T> key = ResourceKey.create(registryResourceKey, featureName);
        return getRegistry(level, registryResourceKey).getHolder(key).orElseThrow();
    }
    private static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, ResourceLocation featureName){
        return getRegistryKeyType(level, featureName,Registry.CONFIGURED_FEATURE_REGISTRY);
    }
}
