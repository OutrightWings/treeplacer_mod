package com.outrightwings.treeplacer.data;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

public class TreePlacer {
    public static int growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random,AbstractTreeGrower treeGrower){
        Holder<? extends ConfiguredFeature<?, ?>> holder;

        Tuple<Boolean, Point> isMega = isTwoByTwoSapling(level,pos,state);
        //Mega tree grow
        if(isMega.getA()){
            holder = TreeOverrideFinder.GetBiomeBasedTreeFeature(level,state,pos,true);
            if(holder != null){
                return placeMega(level,chunkGenerator,pos,state,random,isMega.getB().x,isMega.getB().y,holder);
            }
            //Vanilla option
            if(treeGrower instanceof AbstractMegaTreeGrower){
                return -1;
            }
        }
        //Single tree grow
        holder = TreeOverrideFinder.GetBiomeBasedTreeFeature(level,state,pos,false);
        if(holder != null){
            return placeSingle(level,chunkGenerator,pos,state,random,holder);
        }
        //vanilla option


        return -1;
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
    private static int placeMega(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource randomSource, int i, int j, Holder<? extends ConfiguredFeature<?, ?>> holder) {
        net.minecraftforge.event.level.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(level, randomSource, pos, holder);
        if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || event.getFeature() == null) {
            return -1;
        } else {
            ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();
            BlockState blockstate = Blocks.AIR.defaultBlockState();
            level.setBlock(pos.offset(i, 0, j), blockstate, 4);
            level.setBlock(pos.offset(i + 1, 0, j), blockstate, 4);
            level.setBlock(pos.offset(i, 0, j + 1), blockstate, 4);
            level.setBlock(pos.offset(i + 1, 0, j + 1), blockstate, 4);
            if (configuredfeature.place(level, chunkGenerator, randomSource, pos.offset(i, 0, j))) {
                return 1;
            } else {
                level.setBlock(pos.offset(i, 0, j), state, 4);
                level.setBlock(pos.offset(i + 1, 0, j), state, 4);
                level.setBlock(pos.offset(i, 0, j + 1), state, 4);
                level.setBlock(pos.offset(i + 1, 0, j + 1), state, 4);
                return 0;
            }
        }
    }
    //Took AbstractMegaTreeGrower's function and made it more readable + combined
    public static Tuple<Boolean, Point> isTwoByTwoSapling(ServerLevel level, BlockPos pos, BlockState state){
        for(int i = 0; i >= -1; --i) {
            for(int j = 0; j >= -1; --j) {
                Block block = state.getBlock();
                BlockState cornerA = level.getBlockState(pos.offset(i, 0, j));
                BlockState cornerB = level.getBlockState(pos.offset(i + 1, 0, j));
                BlockState cornerC = level.getBlockState(pos.offset(i, 0, j + 1));
                BlockState cornerD = level.getBlockState(pos.offset(i + 1, 0, j + 1));
                boolean allSame = cornerA.is(block) && cornerB.is(block) &&  cornerC.is(block) && cornerD.is(block);
                if (allSame) {
                    return new Tuple<>(true,new Point(i,j));
                }
            }
        }
        return new Tuple<>(false,null);
    }
}
