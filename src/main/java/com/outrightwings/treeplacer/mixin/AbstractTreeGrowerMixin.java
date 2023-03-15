package com.outrightwings.treeplacer.mixin;

import com.outrightwings.treeplacer.data.TreeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractTreeGrower.class)
public class AbstractTreeGrowerMixin {

    @Inject(at = @At("HEAD"), method = "growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z")
    void growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir){
        TreeFinder.GetBiomeBasedTreeFeature(level,state,pos);
    }
}
