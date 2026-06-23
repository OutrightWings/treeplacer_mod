package com.outrightwings.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import java.awt.Point;

public class TreePlacer {
    public static int growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,boolean isAbstractMegaGrower){
        TreeOverrideFinder.Tuple isMega = isTwobyTwo(level,pos,state);
        int attempt;
        if(isMega.bool()){
            //try mega
            attempt = attemptOverride(level,chunkGenerator,pos,state,random,isMega);
            if(isAbstractMegaGrower|| attempt != -1) return attempt;
            isMega = new TreeOverrideFinder.Tuple(false,isMega.point());
        }
        //Try single
        attempt = attemptOverride(level,chunkGenerator,pos,state,random,isMega);
        return attempt;
    }
    private static int attemptOverride(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,TreeOverrideFinder.Tuple isMega){
        Holder<? extends ConfiguredFeature<?, ?>> holder;
        holder = TreeOverrideFinder.GetSaplingOverride(level,state,pos,isMega);
        return placeTree(level,chunkGenerator,pos,state,random,holder,isMega);
    }
    private static int placeTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,Holder<? extends ConfiguredFeature<?, ?>> holder,TreeOverrideFinder.Tuple isMega){
        if(isMega.bool()) return placeMega(level,chunkGenerator,pos,state,random,isMega.point(),holder);
        else return placeSingle(level,chunkGenerator,pos,state,random,holder);
    }
    private static int placeSingle(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,Holder<? extends ConfiguredFeature<?, ?>> holder){
        if(holder == null) return -1;
        ConfiguredFeature<?, ?> configuredFeature = holder.value();
        BlockState blockstate = level.getFluidState(pos).createLegacyBlock();
        level.setBlock(pos, blockstate, 4);
        if (configuredFeature.place(level, chunkGenerator, random, pos)) {
            if (level.getBlockState(pos) == blockstate) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
            return 1;
        } else {
            level.setBlock(pos, state, 4);
            return 0;
        }

    }
    private static int placeMega(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource randomSource, Point point, Holder<? extends ConfiguredFeature<?, ?>> holder) {
        if(holder == null) return -1;
        ConfiguredFeature<?, ?> configuredfeature = holder.value();
        BlockState blockstate = Blocks.AIR.defaultBlockState();
        int x = point.x;
        int z = point.y;
        level.setBlock(pos.offset(x, 0, z), blockstate, 2);
        level.setBlock(pos.offset(x + 1, 0, z), blockstate, 2);
        level.setBlock(pos.offset(x, 0, z + 1), blockstate, 2);
        level.setBlock(pos.offset(x + 1, 0, z + 1), blockstate, 2);
        if (configuredfeature.place(level, chunkGenerator, randomSource, pos.offset(x, 0, z))) {
            return 1;
        } else {
            level.setBlock(pos.offset(x, 0, z), state, 2);
            level.setBlock(pos.offset(x + 1, 0, z), state, 2);
            level.setBlock(pos.offset(x, 0, z + 1), state, 2);
            level.setBlock(pos.offset(x + 1, 0, z + 1), state, 2);
            return 0;
        }

    }
    //Took AbstractMegaTreeGrower's function and made it more readable + combined
    public static TreeOverrideFinder.Tuple isTwobyTwo(ServerLevel level, BlockPos pos, BlockState state){
        for(int i = 0; i >= -1; --i) {
            for(int j = 0; j >= -1; --j) {
                boolean allSame = isAllSame(level,pos,state,new Point(i,j));
                if (allSame) {
                    return new TreeOverrideFinder.Tuple(true,new Point(i,j));
                }
            }
        }
        return new TreeOverrideFinder.Tuple(false,null);
    }
    public static boolean isAllSame(ServerLevel level, BlockPos pos, BlockState state,Point point){
        int x = point.x;
        int z = point.y;
        Block block = state.getBlock();
        BlockState cornerA = level.getBlockState(pos.offset(x, 0, z));
        BlockState cornerB = level.getBlockState(pos.offset(x + 1, 0, z));
        BlockState cornerC = level.getBlockState(pos.offset(x, 0, z + 1));
        BlockState cornerD = level.getBlockState(pos.offset(x + 1, 0, z + 1));

        return cornerA.is(block) && cornerB.is(block) &&  cornerC.is(block) && cornerD.is(block);
    }
}
