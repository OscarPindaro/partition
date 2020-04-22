package com.openfaas.function.partition;

import java.util.HashMap;
import java.util.Map;

public class CommunityBuilder {

    private final Map<String, Community> map;

    public CommunityBuilder(){ this.map = new HashMap<>(); }

    // If a community with that label already exists, return that community. Otherwise create a new community.
    public Community getCommunity(String label) {

        if (map.containsKey(label)) {
            return map.get(label);
        }
        else {
            Community community = new Community("community" + map.size());
            map.put(label, community);
            return community;
        }

    }
}
