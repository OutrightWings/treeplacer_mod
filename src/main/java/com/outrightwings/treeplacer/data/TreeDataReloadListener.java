package com.outrightwings.treeplacer.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


public class TreeDataReloadListener extends SimplePreparableReloadListener<TreeDataReloadListener.SaplingOverrides> {
    private final String directory = "sapling_overrides";
    private final Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    protected SaplingOverrides prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SaplingOverrides saplingOverrides = new SaplingOverrides();

        int i = this.directory.length() + 1;

        for(Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(this.directory, (location) -> {
            return location.getPath().endsWith(".json");
        }).entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            String s = resourcelocation.getPath();

            int firstSlashIndex = s.indexOf('/');
            int secondSlashIndex = s.indexOf('/', firstSlashIndex + 1);
            String namespace = s.substring(firstSlashIndex + 1, secondSlashIndex);

            int lastSlashIndex = s.lastIndexOf('/');
            int dotIndex = s.lastIndexOf('.');
            String saplingName = s.substring(lastSlashIndex + 1, dotIndex);

            ResourceLocation saplingLocation = new ResourceLocation(namespace, saplingName);

            try {
                Reader reader = entry.getValue().openAsReader();

                try {
                    JsonObject json = GsonHelper.fromJson(this.gson,reader,JsonObject.class);
                    if (json != null) {
                        Map<String,String> biomeFeatureMap = new HashMap<>();
                        for(Map.Entry<String, JsonElement> jentry: json.entrySet()){
                            String biomeID = jentry.getKey();
                            String featureID = jentry.getValue().getAsString();

                            biomeFeatureMap.put(biomeID,featureID);
                        }

                        saplingOverrides.put(saplingLocation.toString(),biomeFeatureMap);


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
        System.out.println("Reload_Apply");
        System.out.println(data.toString());
        TreeFinder.init(data);
    }

    public class SaplingOverrides{
        private Map<String,Map<String,String>> overrides;

        public SaplingOverrides(){
            overrides = new HashMap<>();
        }

        public void put(String sapling, Map<String,String> biomeFeature){
            overrides.put(sapling,biomeFeature);
        }

        public String getFeatureID(String saplingID, String biomeID){
            if(overrides.containsKey(saplingID)){
                Map<String,String> biomeFeatureMap = overrides.get(saplingID);
                if(biomeFeatureMap.containsKey(biomeID)){
                    return biomeFeatureMap.get(biomeID);
                }
            }
            return null;
        }

        public String toString(){
            String str = "";
            for (Map.Entry<String, Map<String, String>> outerEntry : overrides.entrySet()) {
                String outerKey = outerEntry.getKey();
                str += outerKey + " : {\n";
                for (Map.Entry<String, String> innerEntry : outerEntry.getValue().entrySet()) {
                    String innerKey = innerEntry.getKey();
                    String innerValue = innerEntry.getValue();
                    str += "\t"+innerKey+" : "+innerValue+"\n";
                }
                str += "}\n";
            }
            return str;
        }

    }
}
