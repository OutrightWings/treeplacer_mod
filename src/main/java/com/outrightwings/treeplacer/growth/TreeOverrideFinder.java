package com.outrightwings.treeplacer.growth;

import com.outrightwings.treeplacer.data.SaplingOverrides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

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
        if(featureID == null){
            if(isMega){
                featureID = megaSaplingOverrides.getFeatureID(saplingName.toString(),"treeplacer:all_biomes");
            }else{
                featureID = singleSaplingOverrides.getFeatureID(saplingName.toString(),"treeplacer:all_biomes");
            }
        }


        if(featureID != null) {
            ResourceLocation featureName = new ResourceLocation(featureID);
            holder = getConfiguredFeature(level,featureName);
            if(holder == null) System.out.println("Feature: " + featureID + " does not exist. On sapling: " + saplingName + " in biome: " + biomeName + ", typo?");
        }
        return holder;
    }

    //Stole and modified DebugScreen's method
    private static ResourceLocation getResourceLocationFromHolder(Holder<?> holder) {
        return holder.unwrap().map(ResourceKey::location, (empty) -> null);
    }

    //Stole and modified from ResourceKeyArgument thats used by placeCommand
    private static <T> Registry<T> getRegistry(ServerLevel level, ResourceKey<? extends Registry<T>> key){
        Optional<? extends Registry<T>> registry = level.getServer().registryAccess().registry(key);
        return registry.orElse(null);
    }
    private static <T> Holder<T> getRegistryKeyType(ServerLevel level, ResourceLocation featureName, ResourceKey<Registry<T>> registryResourceKey){
        ResourceKey<T> key = ResourceKey.create(registryResourceKey, featureName);
        Registry<T> registry = getRegistry(level, registryResourceKey);
        if(registry == null) return null;
        Optional<Holder<T>> holder = registry.getHolder(key);
        return holder.orElse(null);
    }
    private static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, ResourceLocation featureName){
        return getRegistryKeyType(level, featureName,Registry.CONFIGURED_FEATURE_REGISTRY);
    }
}
