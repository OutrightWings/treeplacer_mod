package com.outrightwings.treeplacer.data;

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
