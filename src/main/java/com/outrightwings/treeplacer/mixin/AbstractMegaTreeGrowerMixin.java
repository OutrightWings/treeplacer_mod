package com.outrightwings.treeplacer.mixin;

import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractMegaTreeGrower.class)
public abstract class AbstractMegaTreeGrowerMixin extends AbstractTreeGrower {

}
