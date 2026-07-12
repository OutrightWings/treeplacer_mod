package com.outrightwings.mixin;

import com.outrightwings.Constants;
import com.outrightwings.growth.TreeOverrideFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VegetationBlock.class)
public class BushBlockMixin extends Block{
    public BushBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(at = @At(value = "HEAD"), method = "mayPlaceOn",cancellable = true)
    void mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof Level real) {
            //Get tag
            BlockState thisBlock = this.defaultBlockState();
            String blockID = BuiltInRegistries.BLOCK.getKey(thisBlock.getBlock()).toString();
            Identifier blockTagLocation = Identifier.tryParse(Constants.MOD_ID + ":" + blockID.replace(":", "_"));
            if (blockTagLocation != null) {
                //Check if tag was defined
                TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, blockTagLocation);
                Registry<Block> registry = real.registryAccess().lookupOrThrow(Registries.BLOCK);
                if (registry.listTags().anyMatch(tag -> tag.key().equals(blockTag))) {
                    cir.setReturnValue(state.is(blockTag));
                }
            }
        }
    }
}
