package com.outrightwings.treeplacer.mixin;

import com.outrightwings.treeplacer.data.TreeFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractTreeGrower.class)
public class AbstractTreeGrowerMixin {
    //Copied over the function because it wasn't passing holder through :(
  @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/grower/AbstractTreeGrower;getConfiguredFeature(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Z)Lnet/minecraft/core/Holder;", shift = At.Shift.AFTER), method = "growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z",locals = LocalCapture.CAPTURE_FAILSOFT,cancellable = true)
    void growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random, CallbackInfoReturnable<Boolean> cir,Holder<? extends ConfiguredFeature<?, ?>> holder){
      holder = TreeFinder.GetBiomeBasedTreeFeature(level,state,pos,holder);
      net.minecraftforge.event.level.SaplingGrowTreeEvent event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(level, random, pos, holder);
      if (event.getResult().equals(net.minecraftforge.eventbus.api.Event.Result.DENY) || event.getFeature() == null) {
          cir.setReturnValue(false);
      } else {
          ConfiguredFeature<?, ?> configuredfeature = event.getFeature().value();
          BlockState blockstate = level.getFluidState(pos).createLegacyBlock();
          level.setBlock(pos, blockstate, 4);
          if (configuredfeature.place(level, chunkGenerator, random, pos)) {
              if (level.getBlockState(pos) == blockstate) {
                  level.sendBlockUpdated(pos, state, blockstate, 2);
              }

              cir.setReturnValue(true);
          } else {
              level.setBlock(pos, state, 4);
              cir.setReturnValue(false);
          }
      }
    }
}
