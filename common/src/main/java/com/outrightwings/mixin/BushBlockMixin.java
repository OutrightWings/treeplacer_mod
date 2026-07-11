package com.outrightwings.mixin;

import com.outrightwings.Constants;
import com.outrightwings.growth.TreeOverrideFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BushBlock.class)
public class BushBlockMixin extends Block{
    public BushBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At(value = "HEAD"), method = "mayPlaceOn(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z",cancellable = true)
    void mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof Level real) {
            //Get tag
            BlockState thisBlock = this.defaultBlockState();
            String blockID = TreeOverrideFinder.getResourceLocationFromHolder(thisBlock.getBlockHolder()).toString();
            ResourceLocation blockTagLocation = ResourceLocation.tryParse(Constants.MOD_ID + ":" + blockID.replace(":", "_"));
            if (blockTagLocation != null) {
                //Check if tag was defined
                TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, blockTagLocation);
                Registry<Block> registry = real.registryAccess().registryOrThrow(Registries.BLOCK);
                if (registry.getTag(blockTag).isPresent()) {
                    cir.setReturnValue(state.is(blockTag));
                }
            }
        }
    }
}
