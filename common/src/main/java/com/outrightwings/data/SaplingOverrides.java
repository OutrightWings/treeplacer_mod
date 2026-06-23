package com.outrightwings.data;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SaplingOverrides{
    private final Map<String, Map<String,FeatureData>> overrides;

    public SaplingOverrides(){
        overrides = new HashMap<>();
    }

    public void put(String sapling, Map<String,FeatureData> biomeFeature){
        overrides.put(sapling,biomeFeature);
    }

    public String getFeatureID(Identifier saplingID, Identifier biomeID, BlockPos pos, boolean weird, String block){
        if(overrides.containsKey(saplingID.toString())){
            Map<String,FeatureData> biomeFeatureMap = overrides.get(saplingID.toString());
            if(biomeFeatureMap.containsKey(biomeID.toString())){
                return biomeFeatureMap.get(biomeID.toString()).getFeature(pos,weird,block);
            }
        }
        return null;
    }
    public Map<String,FeatureData> getBiomeFeaturesOfSapling(String sapling){
        return overrides.get(sapling);
    }
    public String toString(){
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Map<String, FeatureData>> outerEntry : overrides.entrySet()) {
            String outerKey = outerEntry.getKey();
            str.append(outerKey).append(" : {\n");
            for (Map.Entry<String, FeatureData> innerEntry : outerEntry.getValue().entrySet()) {
                String innerKey = innerEntry.getKey();
                String innerValue = innerEntry.getValue().toString();
                str.append("     ").append(innerKey).append(" : ").append(innerValue).append("\n");
            }
            str.append("}\n");
        }
        return str.toString();
    }

}
