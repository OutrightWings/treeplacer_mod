package com.outrightwings.treeplacer.growth;

import com.outrightwings.treeplacer.data.SaplingOverrides;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.Optional;

public class TreeOverrideFinder {
    private static ResourceLocation allBiomes = new ResourceLocation("treeplacer:all_biomes");
    private static SaplingOverrides singleSaplingOverrides;
    private static SaplingOverrides megaSaplingOverrides;
    public static void initSingle(SaplingOverrides overrides){singleSaplingOverrides=overrides;}
    public static void initMega(SaplingOverrides overrides){megaSaplingOverrides=overrides;}

    public static Holder<? extends ConfiguredFeature<?, ?>> GetSaplingOverride(ServerLevel level, BlockState state, BlockPos pos, Tuple<Boolean, Point> isMega){
        ResourceLocation sapling = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        ResourceLocation biome = getResourceLocationFromHolder(level.getBiome(pos));

        String featureID;
        featureID = GetBlockOverride(isMega,sapling,pos,level);
        if(featureID == null) featureID = GetSimpleOverride(isMega,sapling,biome);
        if(featureID == null) featureID = GetDefaultOverride(isMega,sapling);

        return getConfiguredFeature(level,featureID);
    }
    private static String GetSimpleOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, ResourceLocation key){
        return isMega.getA() ? megaSaplingOverrides.getFeatureID(sapling,key) :
                      singleSaplingOverrides.getFeatureID(sapling,key) ;
    }
    private static String GetBlockOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, BlockPos pos, ServerLevel level){
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);
        ResourceLocation groundBlock = getResourceLocationFromHolder(groundState.getBlockHolder());
        if(isMega.getA()){
            boolean groundAllSame = TreePlacer.isAllSame(level,groundPos,groundState,isMega.getB());
            if(!groundAllSame) return null;
        }
        return GetSimpleOverride(isMega,sapling,groundBlock);
    }
    private static String GetDefaultOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling){
        return GetSimpleOverride(isMega,sapling,allBiomes);
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
    private static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, String featureName){
        if(featureName != null){
            ResourceLocation feature = new ResourceLocation(featureName);
            return getRegistryKeyType(level, feature,Registry.CONFIGURED_FEATURE_REGISTRY);
        }
        return null;
    }
}
