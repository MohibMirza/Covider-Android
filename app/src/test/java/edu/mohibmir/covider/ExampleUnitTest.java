package edu.mohibmir.covider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.redisson.api.RKeys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import edu.mohibmir.covider.redis.RClass.Building;
import edu.mohibmir.covider.redis.RClass.Class;
import edu.mohibmir.covider.redis.RClass.Enums.Status;
import edu.mohibmir.covider.redis.RClass.Notification;
import edu.mohibmir.covider.redis.RClass.User;
import edu.mohibmir.covider.redis.RedisClient;

import static edu.mohibmir.covider.redis.RedisDatabase.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public static RedisClient redis;

    public static long keyCount;

    @BeforeClass
    public static void init() {
        redis = new RedisClient("redis://127.0.0.1:6379");
        redis.start();
    }

    @Before
    public void before() {
        RKeys rKeys = redis.redisson.getKeys();
        keyCount = rKeys.count();
    }

    @After
    public void after() {
        RKeys rKeys = redis.redisson.getKeys();
        assertEquals(keyCount, rKeys.count());
    }

    @Test
    public void addNewUserToDatabaseThenDeleteTest() {
        User user = new User("Mark");

        assertEquals("", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(false, user.getIsInstructor());
        assertEquals("healthy", user.getCovidStatus());
        assertEquals("", user.getClass1());
        assertEquals("", user.getClass2());
        assertEquals("", user.getClass3());
        assertEquals("", user.getClass4());
        assertEquals("", user.getClass5());

        user.delete();
    }

    @Test
    public void retrieveUserValuesInDatabaseTest() {
        User user = new User("Mark");
        user.setPassword("password123");
        user.setFirstName("Joe");
        user.setLastName("Carr");
        user.setIsInstructor(true);
        user.setCovidStatus(Status.infected);
        user.setClass1("CSCI-103");
        user.setClass2("CSCI-104");
        user.setClass3("CSCI-109");
        user.setClass4("EE-109");
        user.setClass5("PHYS-151");

        // This will retrieve the object from within the database
        User user1 = new User("Mark");
        assertEquals("password123", user1.getPassword());
        assertEquals("Joe", user1.getFirstName());
        assertEquals("Carr", user1.getLastName());
        assertEquals(true, user1.getIsInstructor());
        assertEquals("infected", user1.getCovidStatus());
        assertEquals("CSCI-103", user1.getClass1());
        assertEquals("CSCI-104", user1.getClass2());
        assertEquals("CSCI-109", user1.getClass3());
        assertEquals("EE-109", user1.getClass4());
        assertEquals("PHYS-151", user1.getClass5());

        user.delete();


    }

    @Test
    public void performLoginTest() {

        User user = new User("Adam434");
        user.setFirstName("Adamo");
        user.setLastName("Facundo");
        user.setPassword("stringcheese");
        user.setIsInstructor(false);

        // successful login assertion
        assertEquals(0.0, user.getPassword().compareTo("stringcheese"), 0.0);
        assertFalse(user.getIsInstructor());

        user.delete();

    }

    @Test
    public void addNewBuildingToDatabaseThenDeleteTest() {
        Building building = new Building("RTH");
        assertEquals(1000.0, building.getRiskScore(), 0.0);
        assertEquals(0.0, building.getLatitude(), 0.0);
        assertEquals(0.0, building.getLongitude(), 0.0);
        assertEquals("No special instructions.", building.getInstructions());

        building.delete();
    }

    @Test
    public void retrieveBuildingValuesInDatabaseTest() {
        Building building = new Building("RTH");
        assertEquals(1000.0, building.getRiskScore(), 0.0);
        building.setRiskScore(990.0);
        building.setLatitude(52.0);
        building.setLongitude(45.0);

        // This will retrieve the created object from within the database
        Building building1 = new Building("RTH");
        assertEquals(990.0, building1.getRiskScore(), 0.0);
        building1.decrementRiskScore(4);
        building1.decrementRiskScore();
        assertEquals(985.0, building1.getRiskScore(), 0.0);
        assertEquals(52.0, building.getLatitude(), 0.0);
        assertEquals(45.0, building.getLongitude(), 0.0);

        building.delete();
    }

    @Test
    public void addNewClassToDatabaseThenDeleteTest() {
        Class csci310 = new Class("csci310");
        assertEquals(0, csci310.getStudents().size());
        assertEquals(true, csci310.getInPerson());
        assertEquals("csci310", csci310.getClassName());

        csci310.delete();
    }

    @Test
    public void retrieveClassValuesInDatabaseTest() {
        Class csci310 = new Class("csci310");
        csci310.addStudent("Mark");
        csci310.addStudent("Jacob");
        csci310.setInPerson(false);

        // This will retrieve the created object from the database
        Class csci310_ = new Class("csci310");
        assertEquals("mark", csci310_.getStudents().get(0));
        assertEquals("jacob", csci310_.getStudents().get(1));
        assertEquals(2, csci310_.getStudents().size());
        assertEquals(false, csci310_.getInPerson());

        csci310_.delete();
    }

    @Test
    public void instructorGetsCovidTest() {
        User user = new User("Adam343");
        user.setFirstName("Adamo");
        user.setLastName("Facundo");
        user.setIsInstructor(true);
        user.setCovidStatus(Status.healthy);

        String[] classes = {"csci310", "csci360", "psyc100", "rel115", "pe65"};

        user.setClass1(classes[0]);
        user.setClass2(classes[1]);
        user.setClass3(classes[2]);
        user.setClass4(classes[3]);
        user.setClass5(classes[4]);

        // generate classes into the database
        for(int i = 0; i < classes.length; i++) {
            Class temp = new Class(classes[i]);
            temp.setInPerson(true);
        }

        // now instructor of class gets covid so
        // all classes should be automatically
        // put into online mode
        user.setCovidStatus(Status.infected);

        // now check if all user classes are online
        for(int i = 0; i < classes.length; i++) {
            Class temp = new Class(classes[i]);
            assertEquals(false, temp.getInPerson());
            temp.delete();
        }

        user.delete();
    }

    @Test
    public void buildingRecentVisitorsReportSymptomsTest() {
        // generate building to database
        Building building = new Building("SAL");
        assertEquals(1000.0, building.getRiskScore(), 0.0);

        String[] visitors = {"john", "jake", "ronny", "tom", "jerry"};

        // create users to database
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.setCovidStatus(Status.healthy);
        }

        DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        // generates a date 9 days past today
        int rollbackTime = 9;
        for(int i = 0; i < rollbackTime; i++) {
            cal.roll(Calendar.DATE, false);
        }
        Date pastDate = cal.getTime();
        for(int i = 0; i < rollbackTime; i++) {
            cal.roll(Calendar.DATE, true);
        }

        // set all of the users to have visited the building at that date
        String date = dtf.format(pastDate);
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.addVisit("SAL");
            building.addLastVisit(user.getUserId(), date);
        }

        // set all users to be symptomatic
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.setCovidStatus(Status.symptomatic);
        }

        // risk score of building has been updated since past visitors reported symptoms
        assertEquals(990.0, building.getRiskScore(), 0.0);
        building.delete();

        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.delete();
        }
    }

    @Test
    public void buildingRecentVisitorsReportCovidTest() {
        // generate building to database
        Building building = new Building("SAL");
        assertEquals(1000.0, building.getRiskScore(), 0.0);

        String[] visitors = {"john", "jake", "ronny", "tom", "jerry"};

        // create users to database
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.setCovidStatus(Status.healthy);
        }

        DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        // generates a date 9 days past today
        int rollbackTime = 9;
        for(int i = 0; i < rollbackTime; i++) {
            cal.roll(Calendar.DATE, false);
        }
        Date pastDate = cal.getTime();
        for(int i = 0; i < rollbackTime; i++) {
            cal.roll(Calendar.DATE, true);
        }

        // set all of the users to have visited the building at that date
        String date = dtf.format(pastDate);
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.addVisits("SAL", 1);
            building.addLastVisit(user.getUserId(), date);
        }

        building.addVisit(visitors[0]);

        // set all users to be symptomatic
        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.setCovidStatus(Status.infected);
        }

        // risk score of building has been updated since past visitors reported symptoms
        assertEquals(975.0, building.getRiskScore(), 0.0);
        building.delete();

        for(int i = 0; i < visitors.length; i++) {
            User user = new User(visitors[i]);
            user.delete();
        }
    }

    @Test
    public void simulateUsersEnteringBuildingTest() {
        Building building1 = new Building("SALLL");
        Building building2 = new Building("RTHHH");
        Building building3 = new Building("JFFF");

        String user1Id = "john";
        String user2Id = "mark";
        String user3Id = "jacob";

        User user1 = new User("johnny");
        User user2 = new User("marky");
        User user3 = new User("jacoby");

        user1.addVisit("SAL");
        user1.addVisit("RTH");
        user1.addVisit("SAL");

        user2.addVisit("RTH");
        user2.addVisit("JFF");
        user2.addVisit("JFF");
        user2.addVisit("JFF");

        user3.addVisit("SAL");
        user3.addVisit("SAL");
        user3.addVisit("SAL");
        user3.addVisit("SAL");
        user3.addVisit("RTH");
        user3.addVisit("RTH");
        user3.addVisit("RTH");
        user3.addVisit("RTH");
        user3.addVisit("JFF");

        assertEquals(2, user1.getBuildingVisitCount("SAL"));
        assertEquals(1, user1.getBuildingVisitCount("RTH"));
        assertEquals(0, user1.getBuildingVisitCount("JFF")); // caught bug on this

        assertEquals(0, user2.getBuildingVisitCount("SAL")); // caught bug on this
        assertEquals(1, user2.getBuildingVisitCount("RTH"));
        assertEquals(3, user2.getBuildingVisitCount("JFF"));

        assertEquals(4, user3.getBuildingVisitCount("SAL"));
        assertEquals(4, user3.getBuildingVisitCount("RTH"));
        assertEquals(1, user3.getBuildingVisitCount("JFF"));

        user1.delete();
        user2.delete();
        user3.delete();

        building1.delete();
        building2.delete();
        building3.delete();
    }

    @Test
    public void cleanDeleteTest() {
        RKeys rKeys = redis.redisson.getKeys();
        keyCount = rKeys.count();

        Building building1 = new Building("SAL");
        Building building2 = new Building("RTH");
        Building building3 = new Building("JFF");
        building1.setLongitude(52.0);
        building2.setLongitude(1000.0);
        building3.setLongitude(214215.0);

        building1.setLatitude(42.0);
        building2.setLatitude(52.0);
        building3.setLatitude(12.0);

        building1.setInstructions("Wear yellow only");
        building2.setInstructions("Do something");
        building3.setInstructions("No instructions");

        Class class1 = new Class("psyc-100");
        Class class2 = new Class("rel-115");
        Class class3 = new Class("csci-310");

        class1.setInPerson(true);
        class2.setInPerson(false);
        class3.setInPerson(false);

        class1.addStudent("Jake");
        class1.addStudent("Mark");
        class1.addStudent("Yo");
        class2.addStudent("akflj");
        class3.addStudent("a");

        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");

        user1.setCovidStatus(Status.symptomatic);
        user3.setCovidStatus(Status.healthy);
        user2.setCovidStatus(Status.infected);

        user1.addVisit("Jake");
        user1.addVisit("Mark");
        user1.addVisit("Yoseph");

        user2.addVisit("Jake");
        user2.addVisit("Mark");
        user2.addVisit("Yoseph");

        user3.addVisit("yo");
        user3.addVisit("Maasfark");

        building1.delete();
        building2.delete();
        building3.delete();

        user1.delete();
        user2.delete();
        user3.delete();

        class1.delete();
        class2.delete();
        class3.delete();

        rKeys = redis.redisson.getKeys();
        assertEquals(keyCount, rKeys.count());
    }

    @Test
    public void instructorSetsClassesToRemoteTest() {
        Class csci310 = new Class("csci310");
        Class phys100 = new Class("phys100");

        User user = new User("Adam434");
        user.setFirstName("Adamo");
        user.setLastName("Facundo");
        user.setPassword("stringcheese");
        user.setIsInstructor(false);
        user.setClass1("csci310");
        user.setClass2("phys100");

        // successful logged in
        assertEquals(0.0, user.getPassword().compareTo("stringcheese"), 0.0);
        assertFalse(user.getIsInstructor());

        user.setAllClassesRemote();

        assertEquals(false, csci310.getInPerson());
        assertEquals(false, phys100.getInPerson());

        user.delete();
        csci310.delete();
        phys100.delete();
    }

    @Test
    public void instructorSetsClassesToLiveTest() {
        Class csci310 = new Class("csci310");
        Class phys100 = new Class("phys100");

        User user = new User("Adam434");
        user.setFirstName("Adamo");
        user.setLastName("Facundo");
        user.setPassword("stringcheese");
        user.setIsInstructor(false);
        user.setClass1("csci310");
        user.setClass2("phys100");

        // successful logged in
        assertEquals(0.0, user.getPassword().compareTo("stringcheese"), 0.0);
        assertFalse(user.getIsInstructor());

        user.setAllClassesRemote();

        assertEquals(false, csci310.getInPerson());
        assertEquals(false, phys100.getInPerson());

        user.setAllClassesLive();

        assertEquals(true, csci310.getInPerson());
        assertEquals(true, phys100.getInPerson());

        user.delete();
        csci310.delete();
        phys100.delete();
    }

    @Test
    public void getOrCreateObjectTest() {
        Building building = getOrCreateBuilding("McDonalds");
        assertNotNull(building.getLatitude());
        assertNotNull(building.getLongitude());
        assertNotNull(building.getRiskScore());

        User user = getOrCreateUser("seesaw");
        assertNotNull(user.getUserId());
        assertNotNull(user.getFirstName());
        assertNotNull(user.getLastName());

        deleteUser("seesaw");
        deleteBuilding("McDonalds");
    }



    // @Test
    public void userAddThenDeleteTest() {
        User user = new User("Mark");
        assertEquals("", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(false, user.getIsInstructor());
        assertEquals("healthy", user.getCovidStatus());
        assertEquals("", user.getClass1());
        assertEquals("", user.getClass2());
        assertEquals("", user.getClass3());
        assertEquals("", user.getClass4());
        assertEquals("", user.getClass5());

        user.setPassword("password123");
        user.setFirstName("Joe");
        user.setLastName("Carr");
        user.setIsInstructor(true);
        user.setCovidStatus(Status.infected);
        user.setClass1("CSCI-103");
        user.setClass2("CSCI-104");
        user.setClass3("CSCI-109");
        user.setClass4("EE-109");
        user.setClass5("PHYS-151");

        // This will retrieve the created object from the database
        User user1 = new User("Mark");

        assertEquals("password123", user1.getPassword());
        assertEquals("Joe", user1.getFirstName());
        assertEquals("Carr", user1.getLastName());
        assertEquals(true, user1.getIsInstructor());
        assertEquals("infected", user1.getCovidStatus());
        assertEquals("CSCI-103", user1.getClass1());
        assertEquals("CSCI-104", user1.getClass2());
        assertEquals("CSCI-109", user1.getClass3());
        assertEquals("EE-109", user1.getClass4());
        assertEquals("PHYS-151", user1.getClass5());

        user.delete();


    }

    @Test
    public void userSendNotificationTest() {
        User user1 = new User("Jacko");
        User user2 = new User("James");
        Class class1 = new Class("psyc-100");
        List<String> userIds = new ArrayList<>();
        userIds.add("Jacko");
        userIds.add("James");
        List<String> classIds = new ArrayList<>();
        classIds.add("psyc-100");

        Notification notification = new Notification("Hello", userIds, classIds);
        notification.send();
        assertEquals(1, user1.getNotifications().size());
        assertEquals(1, user2.getNotifications().size());
        assertEquals(1, class1.getNotifications().size());
        assertEquals("Hello", user2.getNotifications().get(0));
        assertEquals("Hello", user1.getNotifications().get(0));
        assertEquals("Hello", class1.getNotifications().get(0));

        user1.delete();
        user2.delete();
        class1.delete();
    }

    @Test
    public void covidNotificationTest() {
        Class class1 = new Class("psyc-100");
        User user1 = new User("Jacko");
        user1.setClass1("psyc-100");
        User user2 = new User("Sam");
        Building building = new Building("RTH");

        user1.addVisit("RTH");
        user2.addVisit("RTH");
        building.addVisit("jacko");
        building.addVisit("Sam");

        user1.setCovidStatus(Status.infected);

        assertEquals(1, user2.getNotifications().size());
        assertEquals(1, class1.getNotifications().size());
        System.out.print(user2.getNotifications().get(0));


        user1.delete();
        user2.delete();
        building.delete();
        class1.delete();


    }

    @AfterClass
    public static void end() {
        redis.shutdown();
    }

}