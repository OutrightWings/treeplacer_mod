package com.outrightwings.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.outrightwings.growth.TreeOverrideFinder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
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

            ResourceLocation saplingLocation = new ResourceLocation(namespace, saplingName);

            try {
                Reader reader = entry.getValue().openAsReader();

                try {
                    JsonObject json = GsonHelper.fromJson(this.gson,reader,JsonObject.class);
                    if (json != null) {
                        boolean replace = json.get("replace").getAsBoolean();

                        Map<String,FeatureData> biomeFeatureMap = new HashMap<>();
                        for(Map.Entry<String, JsonElement> jentry: json.get("values").getAsJsonObject().entrySet()){
                            ArrayList<String> features = new ArrayList<>();
                            ArrayList<String> blocks = new ArrayList<>();
                            ArrayList<Integer> weights = new ArrayList<>();
                            ArrayList<FeatureData.FeatureBounds> bounds = new ArrayList<>();

                            String biomeID = jentry.getKey();
                            JsonElement value = jentry.getValue();

                            //If multi entry
                            if(value.isJsonArray()){
                                JsonArray jsonArray = value.getAsJsonArray();
                                for(JsonElement element : jsonArray){
                                    JsonObject object = element.getAsJsonObject();
                                    readEntry(object, features, weights, bounds, blocks);
                                }
                            }
                            //If single entry with just name
                            else if(value.isJsonPrimitive()){
                                features.add(jentry.getValue().getAsString());
                                weights.add(1);
                                bounds.add(new FeatureData.FeatureBounds(new Integer[]{null,null,null,null,null,null},null));
                                blocks.add("");
                            }
                            //Complicated single entry
                            else{
                                readEntry(value.getAsJsonObject(), features, weights, bounds, blocks);
                            }
                            //Put into map
                            FeatureData data = new FeatureData(features,weights,bounds,blocks);
                            biomeFeatureMap.put(biomeID,data);
                        }
                        if(replace){
                            saplingOverrides.put(saplingLocation.toString(),biomeFeatureMap);
                        }else{
                            Map<String,FeatureData> previous = saplingOverrides.getBiomeFeaturesOfSapling(saplingLocation.toString());
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

    protected void readEntry(JsonObject object, ArrayList<String> features, ArrayList<Integer> weights, ArrayList<FeatureData.FeatureBounds> bounds, ArrayList<String> blocks){
        //Get feature
        features.add(object.get("feature").getAsString());//required
        //get weight
        if(object.has("weight")){
            weights.add(object.get("weight").getAsInt());
        }
        else{
            weights.add(1);
        }
        //get weirdness
        Boolean w = null;
        if(object.has("weird")){
            w = object.get("weird").getAsBoolean();
        }
        //get pos bounds
        Integer[] b = new Integer[6]; //xmin, xmax, ymin, ymax, zmin, zmax
        String[] axis = new String[]{"x", "y", "z"};
        for(int i = 0;  i < axis.length; i++){
            if(object.has(axis[i])){
                JsonObject a = object.get(axis[i]).getAsJsonObject();
                if(a.has("min")){
                    b[(2*i)] = a.get("min").getAsInt();
                }
                if(a.has("max")){
                    b[(2*i)+1] = a.get("max").getAsInt();
                }
            }
        }
        bounds.add(new FeatureData.FeatureBounds(b,w));
        //Get blocks
        StringBuilder block = new StringBuilder();
        if(object.has("block")){
            JsonArray array = object.getAsJsonArray("block");
            for(JsonElement element : array) {
                JsonObject ob = element.getAsJsonObject();
                block.append(ob.get("id").getAsString()).append(" ");
            }
        }
        blocks.add(block.toString());
    }
}
