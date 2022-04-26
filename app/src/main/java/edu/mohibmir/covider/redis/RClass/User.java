package edu.mohibmir.covider.redis.RClass;

import org.redisson.RedissonList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.mohibmir.covider.redis.RClass.Enums.Status;
import edu.mohibmir.covider.redis.RedisClient;

public class User implements Serializable {

    String userId;

    private String name;

    private List<String> visitedBuildings;

    private CovidStatus covidStatus;

    private static RedissonClient redisson;

    public User(String userId) {
        this.userId = userId.toLowerCase();
        if (redisson == null) { redisson = RedisClient.getInstance().redisson; }

        this.name = "user." + userId.toLowerCase();

        if(!redisson.getBucket(name + ".password").isExists()) {
            redisson.getBucket(name + ".password").set("");
            redisson.getBucket(name + ".firstName").set("John");
            redisson.getBucket(name + ".lastName").set("Doe");
            redisson.getBucket(name + ".isInstructor").set("false");
            redisson.getBucket(name + ".covidStatus").set("healthy");

            // change to yyyy-MM-dd
            redisson.getBucket(name + ".lastUpdatedCovidStatus").set(System.currentTimeMillis());

            redisson.getBucket(name + ".class1").set("");
            redisson.getBucket(name + ".class2").set("");
            redisson.getBucket(name + ".class3").set("");
            redisson.getBucket(name + ".class4").set("");
            redisson.getBucket(name + ".class5").set("");

            // populate it with class names
            redisson.getMap(name + ".visitedBuildingCount");
            redisson.getList(name + ".notifications");

        }
        this.visitedBuildings = new ArrayList<String>();
        this.covidStatus = new CovidStatus(Status.healthy, new Date());
    }

    public String getUserId(){
        return userId;
    }

    public String getPassword() {
        return (String) redisson.getBucket(name + ".password").get();
    }

    public String getFirstName() {
        return (String) redisson.getBucket(name + ".firstName").get();
    }

    public String getLastName() {
        return (String) redisson.getBucket(name + ".lastName").get();
    }

    public String getClass1() {
        return (String) redisson.getBucket(name + ".class1").get();
    }

    public String getClass2() {
        return (String) redisson.getBucket(name + ".class2").get();
    }

    public String getClass3() {
        return (String) redisson.getBucket(name + ".class3").get();
    }

    public String getClass4() {
        return (String) redisson.getBucket(name + ".class4").get();
    }

    public String getClass5() {
        return (String) redisson.getBucket(name + ".class5").get();
    }

    public String getCovidStatus() {
        return (String) redisson.getBucket(name + ".covidStatus").get();
    }

    public boolean getIsInstructor() {
        String isInstructor = (String) redisson.getBucket(name + ".isInstructor").get();

        if(isInstructor.compareToIgnoreCase("true") == 0) {
            return true;
        }else if(isInstructor.compareToIgnoreCase("false") == 0) {
            return false;
        }

        return false;
    }

    public int getBuildingVisitCount(String buildingId) {
        buildingId = buildingId.toLowerCase();
        System.out.println(redisson.getMap("SIZE:" + name  + ".visitedBuildingCount").size());
        RMap<String, Integer> rmap = redisson.getMap(name + ".visitedBuildingCount");
        if(rmap.containsKey(buildingId)) {
            return rmap.get(buildingId);
        }
        return 0;
        // return (int) .get(buildingId);
    }

    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        Object[] list = redisson.getList(name + ".notifications", StringCodec.INSTANCE).toArray();
        for(Object o : list) {
            String s = (String) o;
            notifications.add(s);
        }

        return notifications;
    }

    public void setPassword(String password) {
        redisson.getBucket(name + ".password").set(password);
    }

    public void setFirstName(String firstName) {
        redisson.getBucket(name + ".firstName").set(firstName);
    }

    public void setLastName(String lastName) {
        redisson.getBucket(name + ".lastName").set(lastName);
    }

    public void setClass1(String className) {
        redisson.getBucket(name + ".class1").set(className);
    }

    public void setClass2(String className) {
        redisson.getBucket(name + ".class2").set(className);
    }

    public void setClass3(String className) {
        redisson.getBucket(name + ".class3").set(className);
    }

    public void setClass4(String className) {
        redisson.getBucket(name + ".class4").set(className);
    }

    public void setClass5(String className) {
        redisson.getBucket(name + ".class5").set(className);
    }

    public void setAllClassesRemote() {
        Vector<String> classes = new Vector<>();
        classes.add(getClass1());
        classes.add(getClass2());
        classes.add(getClass3());
        classes.add(getClass4());
        classes.add(getClass5());

        for(String className : classes) {
            if(className.compareTo("") != 0) {
                Class clas = new Class(className);
                clas.setInPerson(false);
            }
        }
    }

    public void setAllClassesLive() {
        Vector<String> classes = new Vector<>();
        classes.add(getClass1());
        classes.add(getClass2());
        classes.add(getClass3());
        classes.add(getClass4());
        classes.add(getClass5());

        for(String className : classes) {
            if(className.compareTo("") != 0) {
                Class clas = new Class(className);
                clas.setInPerson(true);
            }
        }
    }

    public void setIsInstructor(boolean isInstructor) {
        if(isInstructor == true) {
            redisson.getBucket(name + ".isInstructor").set("true");
        }else {
            redisson.getBucket(name + ".isInstructor").set("false");
        }
    }

    public void setCovidStatus(Status covidStatus) {
        if(covidStatus == Status.healthy) {
            redisson.getBucket(name + ".covidStatus").set("healthy");
            return;
        }

        Set<Object> visitedBuildings = redisson.getMap(name + ".visitedBuildingCount").keySet();
        int penalty = 0;
        if(covidStatus == Status.symptomatic) {
            if(getCovidStatus().compareTo("symptomatic") == 0) {
                return;
            }
            redisson.getBucket(name + ".covidStatus").set("symptomatic");
            penalty = 2;

            sendNotification(Status.symptomatic);
        }else if(covidStatus == Status.infected) {
            if(getCovidStatus().compareTo("infected") == 0) {
                return;
            }
            redisson.getBucket(name + ".covidStatus").set("infected");
            penalty = 5;

            sendNotification(Status.infected);
        }



        for(Object obj : visitedBuildings) {
            String buildingId = (String) obj;
            buildingId = buildingId.toLowerCase();
            Building building = new Building(buildingId);
            if(building.checkIfVisitedWithin10Days(userId)){
                building.decrementRiskScore(penalty);
            }
        }

        if(getCovidStatus().compareTo("infected") == 0 && getIsInstructor()) {
            Vector<String> classes = new Vector<>();
            classes.add(getClass1());
            classes.add(getClass2());
            classes.add(getClass3());
            classes.add(getClass4());
            classes.add(getClass5());

            for(String className : classes) {
                if(className.compareTo("") != 0) {
                    Class clas = new Class(className);
                    clas.setInPerson(false);
                }
            }
        }
    }


    private void sendNotification(Status status) {
        Set<String> exposed = new HashSet<>();
        for(Object obj : redisson.getMap(name + ".visitedBuildingCount").keySet()) {
            String buildingId = (String) obj;
            buildingId = buildingId.toLowerCase();
            Building building = new Building(buildingId);
            List<String> visitors = building.getAllVisitors();
            exposed.addAll(visitors);
        }
        exposed.remove(this.getUserId().toLowerCase());

        List<String> students = new ArrayList<>();
        for (String exposee : exposed)
            students.add(exposee);

        System.out.println("STUDENTS LIST "+ students.toString());
        System.out.println("CLASSES LIST" + getClassesArray().toString());

        String message1 = "", message2 = "";
        if(status == Status.infected) {
            message1 = "You may have been exposed to COVID. Please get checked out.";
            message2 = userId + " contracted COVID-19.";
        }else if(status == Status.symptomatic) {
            message1 = "You may have been exposed to someone who is ill (not comfirmed COVID). Please get checked out.";
            message2 = userId + " contracted COVID-19.";
        }
        Notification notification1 = new Notification(message1, students, new ArrayList<String>());
        notification1.send();
        Notification notification2 = new Notification(message2, new ArrayList<String>(), getClassesArray());
        notification2.send();
    }

    private List<String> getClassesArray() {
        List<String> list = new ArrayList<String>();

        String class1 = this.getClass1();
        String class2 = this.getClass2();
        String class3 = this.getClass3();
        String class4 = this.getClass4();
        String class5 = this.getClass5();

        if(class1.compareTo("") != 0) list.add(class1);


        if(class2.compareTo("") != 0) list.add(class2);

        if(class3.compareTo("") != 0) list.add(class3);


        if(class4.compareTo("") != 0) list.add(class4);


        if(class5.compareTo("") != 0) list.add(class5);


        return list;
    }

    private List<String> getStudentsOfClasses() {
        Set<String> students = new HashSet<>();
        List<String> classes = this.getClassesArray();
        for(String className : classes) {
            Class class_ = new Class(className);
            List<String> classMembers = class_.getStudents();
            for(String classMember : classMembers) {
                students.add(classMember);
            }
        }

        List<String> classStudents = new ArrayList<>();
        for(String student : students) {
            students.add(student);
        }

        return classStudents;

    }

    public void addVisit(String buildingId) {
        buildingId = buildingId.toLowerCase();
        RMap<String, Integer> visitedBuildingCount = redisson.getMap(name + ".visitedBuildingCount");
        Integer count = visitedBuildingCount.get(buildingId);
        if(count == null) {
            visitedBuildingCount.put(buildingId, 1);
            return;
        }
        visitedBuildingCount.put(buildingId, count+1);
    }

    public void addVisits(String buildingId, int times) {
        buildingId = buildingId.toLowerCase();
        RMap<String, Integer> visitedBuildingCount = redisson.getMap(name + ".visitedBuildingCount");
        Integer count = visitedBuildingCount.get(buildingId);
        if(count == null) {
            visitedBuildingCount.put(buildingId, times);
            return;
        }
        visitedBuildingCount.put(buildingId, count+times);
    }

    public void addNotification(String message) {
        redisson.getList(name + ".notifications").add(message);
    }

    public void clearNotifications() {
        redisson.getList(name + ".notifications").clear();
    }

    public void delete() {
        redisson.getBucket(name + ".password").delete();
        redisson.getBucket(name + ".firstName").delete();
        redisson.getBucket(name + ".lastName").delete();
        redisson.getBucket(name + ".isInstructor").delete();
        redisson.getBucket(name + ".covidStatus").delete();
        redisson.getBucket(name + ".lastUpdatedCovidStatus").delete();

        redisson.getBucket(name + ".class1").delete();
        redisson.getBucket(name + ".class2").delete();
        redisson.getBucket(name + ".class3").delete();
        redisson.getBucket(name + ".class4").delete();
        redisson.getBucket(name + ".class5").delete();

        redisson.getMap(name + ".visitedBuildingCount").clear();
        redisson.getMap(name + ".visitedBuildingCount").delete();
        redisson.getList(name + ".notifications").delete();

    }

}

