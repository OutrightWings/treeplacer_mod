package com.outrightwings.treeplacer.data;

import java.util.ArrayList;
import java.util.Random;

public record FeatureData(ArrayList<String> features, ArrayList<Integer> weights) {
    String getFeature(){
        int totalWeight = 0;
        for(int i : weights){
            totalWeight += i;
        }
        int random = new Random().nextInt(totalWeight);
        int cumulative = 0;
        for(int i = 0; i < weights.size(); i++){
            cumulative += weights.get(i);
            if(cumulative > random){
                return features.get(i);
            }
        }
        return features.get(0);
    }
}
