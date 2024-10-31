package com.outrightwings.treeplacer.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.outrightwings.treeplacer.growth.TreeOverrideFinder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


public class SingleTreeDataReloadListener extends SimplePreparableReloadListener<SaplingOverrides> {
    protected String directory;
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    public SingleTreeDataReloadListener(){
        directory = "sapling_overrides/single";
    }
    @Override
    //Stole and modified SimpleJsonResourceReloadListener method
    protected SaplingOverrides prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SaplingOverrides saplingOverrides = new SaplingOverrides();

        for(Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(this.directory, (location) -> location.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            String s = resourcelocation.getPath();
            String[] parts = s.split("/");
            String namespace = parts[2];
            String saplingName = parts[3].replace(".json","");

            ResourceLocation saplingLocation = ResourceLocation.fromNamespaceAndPath(namespace, saplingName);

            try {
                Reader reader = entry.getValue().openAsReader();

                try {
                    JsonObject json = GsonHelper.fromJson(this.gson,reader,JsonObject.class);
                    if (json != null) {
                        boolean replace = json.get("replace").getAsBoolean();

                        Map<String,String> biomeFeatureMap = new HashMap<>();
                        for(Map.Entry<String, JsonElement> jentry: json.get("values").getAsJsonObject().entrySet()){
                            String biomeID = jentry.getKey();
                            String featureID = jentry.getValue().getAsString();

                            biomeFeatureMap.put(biomeID,featureID);
                        }
                        if(replace){
                            saplingOverrides.put(saplingLocation.toString(),biomeFeatureMap);
                        }else{
                            Map<String,String> previous = saplingOverrides.getBiomeFeaturesOfSapling(saplingLocation.toString());
                            if(previous!=null){
                                previous.putAll(biomeFeatureMap);
                            }else{
                                saplingOverrides.put(saplingLocation.toString(),biomeFeatureMap);
                            }
                        }



                    } else {
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", saplingLocation, resourcelocation);
                    }
                } catch (Throwable throwable1) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable throwable) {
                            throwable1.addSuppressed(throwable);
                        }
                    }

                    throw throwable1;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                LOGGER.error("Couldn't parse data file {} from {}", saplingLocation, resourcelocation, jsonparseexception);
            }
        }
        return saplingOverrides;
    }
    @Override
    protected void apply(SaplingOverrides data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        TreeOverrideFinder.initSingle(data);
        //System.out.println(data);
    }
}
