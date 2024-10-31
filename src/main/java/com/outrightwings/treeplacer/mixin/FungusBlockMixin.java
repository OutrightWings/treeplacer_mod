package com.outrightwings.treeplacer.mixin;

import com.outrightwings.treeplacer.growth.TreePlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FungusBlock.class)
public class FungusBlockMixin {
    @Inject(at = @At(value = "HEAD"), method = "performBonemeal(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",cancellable = true,remap = false)
    private void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo cir){
        int placed = TreePlacer.growTree(level,level.getChunkSource().getGenerator(),pos,state,random,false);
        if(placed != -1) cir.cancel();
    }
    @Inject(at = @At(value = "HEAD"), method = "isValidBonemealTarget(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",cancellable = true,remap = false)
    private void isValidBonemealTarget(LevelReader p_256655_, BlockPos p_256553_, BlockState p_256213_, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
}
