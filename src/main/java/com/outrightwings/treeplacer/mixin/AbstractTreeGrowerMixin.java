package com.outrightwings.treeplacer.mixin;

import com.outrightwings.treeplacer.growth.TreePlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractTreeGrower.class)
public abstract class AbstractTreeGrowerMixin {
  @Inject(at = @At(value = "HEAD"), method = "growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z",cancellable = true)
    void growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        int placed = TreePlacer.growTree(level,chunkGenerator,pos,state,random,(AbstractTreeGrower)(Object)this);
        if(placed != -1) cir.setReturnValue(placed == 1);
    }
}
