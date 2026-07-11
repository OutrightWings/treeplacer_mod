package com.outrightwings.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

    public String getFeatureID(ResourceLocation saplingID, ResourceLocation biomeID, BlockPos pos, boolean weird, BlockState groundState){
        if(overrides.containsKey(saplingID.toString())){
            Map<String,FeatureData> biomeFeatureMap = overrides.get(saplingID.toString());
            if(biomeFeatureMap.containsKey(biomeID.toString())){
                return biomeFeatureMap.get(biomeID.toString()).getFeature(pos,weird,groundState);
            }
        }
        return null;
    }

    public String getFeatureIDFromMatchingBiomeTag(ResourceLocation saplingID, Holder<Biome> biomeHolder, BlockPos pos, boolean weird, BlockState groundState){
        if(overrides.containsKey(saplingID.toString())){
            Map<String,FeatureData> biomeFeatureMap = overrides.get(saplingID.toString());
            for(Map.Entry<String,FeatureData> entry : biomeFeatureMap.entrySet()){
                String biomeTagID = entry.getKey();
                if(!biomeTagID.startsWith("#")) continue;

                ResourceLocation biomeTagLocation = ResourceLocation.tryParse(biomeTagID.substring(1));
                if(biomeTagLocation == null) continue;

                TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, biomeTagLocation);
                if(biomeHolder.is(biomeTag)){
                    return entry.getValue().getFeature(pos,weird,groundState);
                }
            }
        }
        return null;
    }
    public String getFeatureIDFromMatchingBlockTag(ResourceLocation saplingID, BlockState blockState, BlockPos pos, boolean weird){
        if(overrides.containsKey(saplingID.toString())){
            Map<String,FeatureData> biomeFeatureMap = overrides.get(saplingID.toString());
            for(Map.Entry<String,FeatureData> entry : biomeFeatureMap.entrySet()){
                String blockTagID = entry.getKey();
                if(!blockTagID.startsWith("#")) continue;

                ResourceLocation blockTagLocation = ResourceLocation.tryParse(blockTagID.substring(1));
                if(blockTagLocation == null) continue;

                TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, blockTagLocation);
                if(blockState.is(blockTag)){
                    return entry.getValue().getFeature(pos,weird,blockState);
                }
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
