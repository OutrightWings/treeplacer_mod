package com.outrightwings.growth;

import com.outrightwings.data.SaplingOverrides;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.awt.Point;

public class TreeOverrideFinder {
    private static final Identifier allBiomes = Identifier.fromNamespaceAndPath("treeplacer","all_biomes");
    public static SaplingOverrides singleSaplingOverrides;
    public static SaplingOverrides megaSaplingOverrides;
    public static void initSingle(SaplingOverrides overrides){singleSaplingOverrides=overrides;}
    public static void initMega(SaplingOverrides overrides){megaSaplingOverrides=overrides;}

    public static Holder<? extends ConfiguredFeature<?, ?>> GetSaplingOverride(ServerLevel level, BlockState state, BlockPos pos, Tuple isMega){
        Identifier sapling = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        Holder<Biome> biomeHolder = level.getBiome(pos);
        Identifier biome = getResourceLocationFromHolder(biomeHolder);
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);
        Identifier groundBlock = BuiltInRegistries.BLOCK.getKey(groundState.getBlock());
        Boolean weird = getWeirdness(level,pos);

        String featureID;
        featureID = GetBlockOverride(isMega,sapling,pos,groundState,weird,level);
        if(featureID == null) featureID = GetBlockTagOverride(isMega,sapling,pos,groundState,weird,level);
        if(featureID == null) featureID = GetSimpleOverride(isMega,sapling,biome,pos,weird,groundState);
        if(featureID == null) featureID = GetBiomeTagOverride(isMega,sapling,biomeHolder,pos,weird,groundState);
        if(featureID == null) featureID = GetSimpleOverride(isMega,sapling,allBiomes,pos,weird,groundState);

        return getConfiguredFeature(level,featureID);
    }
    private static String GetSimpleOverride(Tuple isMega, Identifier sapling, Identifier key, BlockPos pos, Boolean weird, BlockState groundState){
        return isMega.bool() ? megaSaplingOverrides.getFeatureID(sapling,key, pos, weird, groundState) :
                singleSaplingOverrides.getFeatureID(sapling,key, pos, weird, groundState) ;
    }
    private static String GetBiomeTagOverride(Tuple isMega, Identifier sapling, Holder<Biome> biome, BlockPos pos, Boolean weird, BlockState groundState){
        return isMega.bool() ? megaSaplingOverrides.getFeatureIDFromMatchingBiomeTag(sapling, biome, pos, weird, groundState) :
                singleSaplingOverrides.getFeatureIDFromMatchingBiomeTag(sapling, biome, pos, weird, groundState) ;
    }
    private static String GetBlockOverride(Tuple isMega, Identifier sapling, BlockPos pos, BlockState groundState, boolean weird, ServerLevel level){
        if(isMega.bool()){
            boolean groundAllSame = TreePlacer.isAllSame(level,pos,groundState,isMega.point());
            if(!groundAllSame) return null;
        }
        Identifier groundBlock = BuiltInRegistries.BLOCK.getKey(groundState.getBlock());
        return GetSimpleOverride(isMega,sapling,groundBlock,pos,weird,groundState);
    }
    private static String GetBlockTagOverride(Tuple isMega, Identifier sapling, BlockPos pos, BlockState groundState, boolean weird, ServerLevel level){
        if(isMega.bool()){
            boolean groundAllSame = TreePlacer.isAllSame(level,pos,groundState,isMega.point());
            if(!groundAllSame) return null;
        }
        return isMega.bool() ? megaSaplingOverrides.getFeatureIDFromMatchingBlockTag(sapling,groundState,pos,weird) :
                singleSaplingOverrides.getFeatureIDFromMatchingBlockTag(sapling,groundState,pos,weird);
    }


    //Stole and modified DebugScreen's method
    private static Identifier getResourceLocationFromHolder(Holder<?> holder) {
        return holder.unwrap().map(ResourceKey::identifier, (empty) -> null);
    }

    private static Boolean getWeirdness(ServerLevel level, BlockPos pos) {
        ChunkGenerator gen = level.getChunkSource().getGenerator();
        RandomState randomstate = level.getChunkSource().randomState();
        NoiseRouter noiserouter = randomstate.router();
        DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
        double weirdness = noiserouter.ridges().compute(densityfunction$singlepointcontext);
        return weirdness > 0;
    }

    private static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, String feature) {
        if (feature == null) return null;
        //System.out.println(feature);
        ResourceKey<ConfiguredFeature<?, ?>> key = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.parse(feature));
        return level.registryAccess().getOrThrow(key);
    }

    public record Tuple(Boolean bool, Point point) {
    }
}
