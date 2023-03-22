package com.outrightwings.treeplacer.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.awt.Point;


public class TreePlacer {
    public static int growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,AbstractTreeGrower treeGrower){
        Tuple<Boolean, Point> isMega = isTwobyTwo(level,pos,state,0);

        int attempt;
        if(isMega.getA()){
            //try mega
            attempt = attemptOverride(level,chunkGenerator,pos,state,random,isMega);
            if(treeGrower instanceof AbstractMegaTreeGrower || attempt != -1) return attempt;
            isMega.setA(false);
        }
        //Try single
        attempt = attemptOverride(level,chunkGenerator,pos,state,random,isMega);
        return attempt;
    }
    private static int attemptOverride(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,Tuple<Boolean, Point> isMega){
        Holder<? extends ConfiguredFeature<?, ?>> holder;
        holder = TreeOverrideFinder.GetSaplingOverride(level,state,pos,isMega);
        return placeTree(level,chunkGenerator,pos,state,random,holder,isMega);
    }
    private static int placeTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,Holder<? extends ConfiguredFeature<?, ?>> holder,Tuple<Boolean, Point> isMega){
        if(isMega.getA()) return placeMega(level,chunkGenerator,pos,state,random,isMega.getB(),holder);
        else return placeSingle(level,chunkGenerator,pos,state,random,holder);
    }
    private static int placeSingle(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,Holder<? extends ConfiguredFeature<?, ?>> holder){
        net.minecraftforge.event.level.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(level, random, pos, holder);
        if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || event.getFeature() == null) {
            return -1;
        } else {
            ConfiguredFeature<?, ?> configuredFeature = event.getFeature().value();
            BlockState blockstate = level.getFluidState(pos).createLegacyBlock();
            level.setBlock(pos, blockstate, 4);
            if (configuredFeature.place(level, chunkGenerator, random, pos)) {
                if (level.getBlockState(pos) == blockstate) {
                    level.sendBlockUpdated(pos, state, blockstate, 2);
                }
                return 1;
            } else {
                level.setBlock(pos, state, 4);
                return 0;
            }
        }
    }
    private static int placeMega(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource randomSource, Point point, Holder<? extends ConfiguredFeature<?, ?>> holder) {
        net.minecraftforge.event.level.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(level, randomSource, pos, holder);
        if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || event.getFeature() == null) {
            return -1;
        } else {
            ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();
            BlockState blockstate = Blocks.AIR.defaultBlockState();
            int x = point.x;
            int z = point.y;
            level.setBlock(pos.offset(x, 0, z), blockstate, 4);
            level.setBlock(pos.offset(x + 1, 0, z), blockstate, 4);
            level.setBlock(pos.offset(x, 0, z + 1), blockstate, 4);
            level.setBlock(pos.offset(x + 1, 0, z + 1), blockstate, 4);
            if (configuredfeature.place(level, chunkGenerator, randomSource, pos.offset(x, 0, z))) {
                return 1;
            } else {
                level.setBlock(pos.offset(x, 0, z), state, 4);
                level.setBlock(pos.offset(x + 1, 0, z), state, 4);
                level.setBlock(pos.offset(x, 0, z + 1), state, 4);
                level.setBlock(pos.offset(x + 1, 0, z + 1), state, 4);
                return 0;
            }
        }
    }
    //Took AbstractMegaTreeGrower's function and made it more readable + combined
    public static Tuple<Boolean, Point> isTwobyTwo(ServerLevel level, BlockPos pos, BlockState state, int yOffset){
        for(int i = 0; i >= -1; --i) {
            for(int j = 0; j >= -1; --j) {
                Block block = state.getBlock();
                BlockState cornerA = level.getBlockState(pos.offset(i, yOffset, j));
                BlockState cornerB = level.getBlockState(pos.offset(i + 1, yOffset, j));
                BlockState cornerC = level.getBlockState(pos.offset(i, yOffset, j + 1));
                BlockState cornerD = level.getBlockState(pos.offset(i + 1, yOffset, j + 1));
                boolean allSame = cornerA.is(block) && cornerB.is(block) &&  cornerC.is(block) && cornerD.is(block);
                if (allSame) {
                    return new Tuple<>(true,new Point(i,j));
                }
            }
        }
        return new Tuple<>(false,new Point(-1,-1));
    }
}
