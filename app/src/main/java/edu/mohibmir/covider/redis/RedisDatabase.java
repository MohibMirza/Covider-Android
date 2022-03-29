package edu.mohibmir.covider.redis;

import android.icu.number.NumberFormatter;

import org.redisson.api.RLiveObjectService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RClass.Status;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RClass.Visit;

public class RedisDatabase {

    public static String userId;

    public static List<String> buildingNames = Arrays.asList(new String[] { "SAL", "SSL", "RTH", "JFF", "HSH" });

    public static RLiveObjectService rlo = RedisClient.getInstance().redisson.getLiveObjectService();

    public static Building getOrCreateBuilding(String buildingId) {
        Building building = new Building(buildingId);

        return building;
    }

    public static User getOrCreateUser(String userId) {
        User user = new User(userId);

        return user;
    }

    public static void deleteBuilding(String buildingId) {
        Building building = new Building(buildingId);
        building.delete();
    }

    public static void deleteUser(String userId)  {
        User user = new User(userId);
        user.delete();
    }
}
