package com.outrightwings.data;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SaplingOverrides{
    private final Map<String, Map<String,String>> overrides;

    public SaplingOverrides(){
        overrides = new HashMap<>();
    }

    public void put(String sapling, Map<String,String> biomeFeature){
        overrides.put(sapling,biomeFeature);
    }

    public String getFeatureID(ResourceLocation saplingID, ResourceLocation biomeID){
        if(overrides.containsKey(saplingID.toString())){
            Map<String,String> biomeFeatureMap = overrides.get(saplingID.toString());
            if(biomeFeatureMap.containsKey(biomeID.toString())){
                return biomeFeatureMap.get(biomeID.toString());
            }
        }
        return null;
    }
    public Map<String,String> getBiomeFeaturesOfSapling(String sapling){
        return overrides.get(sapling);
    }
    public String toString(){
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> outerEntry : overrides.entrySet()) {
            String outerKey = outerEntry.getKey();
            str.append(outerKey).append(" : {\n");
            for (Map.Entry<String, String> innerEntry : outerEntry.getValue().entrySet()) {
                String innerKey = innerEntry.getKey();
                String innerValue = innerEntry.getValue();
                str.append("\t").append(innerKey).append(" : ").append(innerValue).append("\n");
            }
            str.append("}\n");
        }
        return str.toString();
    }

}
