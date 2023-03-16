package com.outrightwings.treeplacer.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;


public class TreeDataReloadListener extends SimplePreparableReloadListener<String> {
    static Gson GSON = new Gson();
    Path dataDirectory = FMLPaths.CONFIGDIR.get();
    Path overridesDirectory = dataDirectory.resolve("treeplacer/sapling_overrides");


    @Override
    protected String prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        System.out.println("Reload_Prepare");
        return null;
    }

    @Override
    protected void apply(String data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        System.out.println("Reload_Apply");
    }
}
