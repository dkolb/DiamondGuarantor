package com.krinchan.minecraft.diamondguarantor;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by david on 2/6/16.
 */
@RunWith(JUnit4.class)
public class Scratch {
    @Test
    public void testScoreJson() {
        Map<UUID, Integer> score = new ConcurrentHashMap<>();
        score.put(UUID.randomUUID(), 10);
        score.put(UUID.randomUUID(), 20);

        Gson g = new Gson();
        String json = g.toJson(score);

        System.out.println(json);

        Map<UUID, Integer> otherScore = g.fromJson(json, Map.class);

        System.out.println(otherScore.toString());
    }
}
