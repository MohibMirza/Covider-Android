package edu.mohibmir.covider.redis.RClass;

import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Vector;

import edu.mohibmir.covider.redis.RedisClient;

public class Class {

    String className;
    String name;

    private static RedissonClient redisson;

    public Class(String name) {
        this.className = name;
        this.name = "class." + name.toLowerCase();

        if (redisson == null) { redisson = RedisClient.getInstance().redisson; }

        if(!redisson.getBucket(this.name + ".isInPerson").isExists()) {
            redisson.getBucket(this.name + ".isInPerson").set("true");
            redisson.getSet(this.name + ".students");
        }
    }

    public String getClassName() {
        return className;
    }

    public boolean getInPerson() {
        String bool = (String) redisson.getBucket(name + ".isInPerson").get();
        if(bool.compareTo("true") == 0) {
            return true;
        }else{
            return false;
        }
    }

    public List<String> getStudents() {
        RSet<String> rList = redisson.getSet(name + ".students");
        List<String> strings = new Vector<String>();
        for(String s : rList) {
            strings.add(s);
        }
        return strings;
    }

    public void addStudent(String userId) {
        userId = userId.toLowerCase();
        redisson.getSet(name + ".students").add(userId);
    }

    public void setInPerson(boolean willBeInPerson) {
        if (willBeInPerson == true)
            redisson.getBucket(name + ".isInPerson").set("true");
        else
            redisson.getBucket(name + ".isInPerson").set("false");

    }

    public void delete() {

        redisson.getBucket(name + ".isInPerson").delete();
        redisson.getList(name + ".students").clear();
        redisson.getList(name + ".students").delete();
    }




}