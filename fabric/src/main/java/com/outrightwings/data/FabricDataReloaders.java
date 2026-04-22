package com.outrightwings.data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.Identifier;
import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;

public class FabricDataReloaders {
    public static class FabricSingleTreeDataReloadListener extends SingleTreeDataReloadListener implements IdentifiableResourceReloadListener{
        @Override
        public Identifier getFabricId() {
            return Identifier.fromNamespaceAndPath("treeplacer","single_tree_data");
        }
    }

    public static class FabricMegaTreeDataReloadListener extends MegaTreeDataReloadListener implements IdentifiableResourceReloadListener{
        @Override
        public Identifier getFabricId() {
            return Identifier.fromNamespaceAndPath("treeplacer","mega_tree_data");
        }
    }
}
