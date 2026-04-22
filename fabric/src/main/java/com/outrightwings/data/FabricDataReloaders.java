package com.outrightwings.data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class FabricDataReloaders {
    public static class FabricSingleTreeDataReloadListener extends SingleTreeDataReloadListener implements IdentifiableResourceReloadListener{
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath("treeplacer","single_tree_data");
        }
    }

    public static class FabricMegaTreeDataReloadListener extends MegaTreeDataReloadListener implements IdentifiableResourceReloadListener{
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath("treeplacer","mega_tree_data");
        }
    }
}
