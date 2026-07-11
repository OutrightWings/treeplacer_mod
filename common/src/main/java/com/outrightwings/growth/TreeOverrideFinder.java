package com.outrightwings.growth;

import com.mojang.serialization.Codec;
import com.outrightwings.data.SaplingOverrides;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.awt.Point;
import java.util.Optional;

public class TreeOverrideFinder {
    private static final ResourceLocation allBiomes = new ResourceLocation("treeplacer:all_biomes");
    private static SaplingOverrides singleSaplingOverrides;
    private static SaplingOverrides megaSaplingOverrides;
    public static void initSingle(SaplingOverrides overrides){singleSaplingOverrides=overrides;}
    public static void initMega(SaplingOverrides overrides){megaSaplingOverrides=overrides;}

    public static Holder<? extends ConfiguredFeature<?, ?>> GetSaplingOverride(ServerLevel level, BlockState state, BlockPos pos, Tuple<Boolean, Point> isMega){
        ResourceLocation sapling = getResourceLocationFromHolder(state.getBlockHolder());
        Holder<Biome> biomeHolder = level.getBiome(pos);
        ResourceLocation biome = getResourceLocationFromHolder(biomeHolder);
        BlockPos groundPos = pos.below();
        BlockState groundState = level.getBlockState(groundPos);
        Boolean weird = getWeirdness(level,pos);

        String featureID;
        featureID = GetBlockOverride(isMega,sapling,pos,groundState,weird,level);
        if(featureID == null) featureID = GetBlockTagOverride(isMega,sapling,pos,groundState,weird,level);
        if(featureID == null) featureID = GetSimpleOverride(isMega,sapling,biome,pos,weird,groundState);
        if(featureID == null) featureID = GetBiomeTagOverride(isMega,sapling,biomeHolder,pos,weird,groundState);
        if(featureID == null) featureID = GetSimpleOverride(isMega,sapling,allBiomes,pos,weird,groundState);

        return getConfiguredFeature(level,featureID);
    }
    private static String GetSimpleOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, ResourceLocation key, BlockPos pos, Boolean weird, BlockState groundState){
        return isMega.getA() ? megaSaplingOverrides.getFeatureID(sapling,key, pos, weird, groundState) :
                singleSaplingOverrides.getFeatureID(sapling,key, pos, weird, groundState) ;
    }
    private static String GetBiomeTagOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, Holder<Biome> biome, BlockPos pos, Boolean weird, BlockState groundState){
        return isMega.getA() ? megaSaplingOverrides.getFeatureIDFromMatchingBiomeTag(sapling, biome, pos, weird, groundState) :
                singleSaplingOverrides.getFeatureIDFromMatchingBiomeTag(sapling, biome, pos, weird, groundState) ;
    }
    private static String GetBlockOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, BlockPos pos, BlockState groundState, boolean weird, ServerLevel level){
        if(isMega.getA()){
            boolean groundAllSame = TreePlacer.isAllSame(level,pos,groundState,isMega.getB());
            if(!groundAllSame) return null;
        }
        ResourceLocation groundBlock = getResourceLocationFromHolder(groundState.getBlockHolder());
        return GetSimpleOverride(isMega,sapling,groundBlock,pos,weird,groundState);
    }
    private static String GetBlockTagOverride(Tuple<Boolean, Point> isMega, ResourceLocation sapling, BlockPos pos, BlockState groundState, boolean weird, ServerLevel level){
        if(isMega.getA()){
            boolean groundAllSame = TreePlacer.isAllSame(level,pos,groundState,isMega.getB());
            if(!groundAllSame) return null;
        }
        return isMega.getA() ? megaSaplingOverrides.getFeatureIDFromMatchingBlockTag(sapling,groundState,pos,weird) :
                singleSaplingOverrides.getFeatureIDFromMatchingBlockTag(sapling,groundState,pos,weird);
    }

    //Stole and modified DebugScreen's method
    public static ResourceLocation getResourceLocationFromHolder(Holder<?> holder) {
        return holder.unwrap().map(ResourceKey::location, (empty) -> null);
    }
    private static Boolean getWeirdness(ServerLevel level, BlockPos pos){
        ChunkGenerator gen = level.getChunkSource().getGenerator();
        RandomState randomstate = level.getChunkSource().randomState();
        NoiseRouter noiserouter = randomstate.router();
        DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
        double weirdness = noiserouter.ridges().compute(densityfunction$singlepointcontext);
        return weirdness > 0;
    }
    private static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(ServerLevel level, String feature){
        if(feature == null) return null;
        ResourceKey<ConfiguredFeature<?, ?>> key = FeatureUtils.createKey(feature);
        return level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(key).orElse(null);
    }
}
