package com.outrightwings.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.outrightwings.growth.TreeOverrideFinder.getResourceLocationFromHolder;

public record FeatureData(ArrayList<String> features, ArrayList<Integer> weights, ArrayList<FeatureBounds> bounds, ArrayList<String> blocks) {
    String getFeature(BlockPos pos, boolean weird, BlockState groundState){
        //Get indexes of features in bounds
        ArrayList<Integer> valid = new ArrayList<>();
        String groundBlock = getResourceLocationFromHolder(groundState.getBlockHolder()).toString();

        for(int i = 0; i < bounds.size(); i++){
            //Check for blocktag
            String blockString = blocks.get(i);
            String[] blockEntries = blockString.split(" ");

            boolean has = false;
            for(String e : blockEntries){
                if(e.startsWith("#")){
                    ResourceLocation blockTagLocation = ResourceLocation.tryParse(e.substring(1));
                    if(blockTagLocation == null) continue;
                    TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, blockTagLocation);
                    has = groundState.is(blockTag);
                }
                else{
                    has = e.contains(groundBlock);
                }
                if(has) break;
            }

            //Skip if not right block
            if(!blockString.isEmpty() && !has){
                continue;
            }
            //Add indexes with matching bounds
            if(bounds.get(i).inBounds(pos.getX(),pos.getY(),pos.getZ(),weird)){
                valid.add(i);
            }
        }
        //get total weight of in bound features
        int totalWeight = 0;
        for(int i : valid){
            totalWeight += weights.get(i);
        }
        //Find feature that goes up to that weight
        if(totalWeight > 0){
            int random = new Random().nextInt(totalWeight)+1;
            int cumulative = 0;
            for(int i : valid){
                cumulative += weights.get(i);
                if(cumulative >= random){
                    return features.get(i);
                }
            }
        }
        return null;
    }
    public record FeatureBounds(Integer[] bounds, Boolean weird){ //xmin, xmax, ymin, ymax, zmin, zmax
        boolean inBounds(int x, int y, int z, boolean w){
            int[] pos = new int[]{x,y,z};
            for(int i = 0; i < pos.length; i++){
                int j = 2*i;
                if(bounds[j] != null && pos[i] < bounds[j]) // pos < min
                {
                    return false;
                }
                if(bounds[j+1] != null && pos[i] > bounds[j+1]) // pos > max
                {
                    return false;
                }
            }
            return (weird == null || weird == w);
        }
    }
}
