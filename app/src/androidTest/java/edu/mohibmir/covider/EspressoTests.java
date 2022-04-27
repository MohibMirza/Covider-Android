package edu.mohibmir.covider;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EspressoTests {

    @Rule
    public ActivityScenarioRule<Login> activityRule =
            new ActivityScenarioRule<>(Login.class);

    @Before
    public void setUp() throws Exception{
        Intents.init();
    }

    @Test
    public void checkRegisterClick() {
        onView(withId(R.id.textViewLinkRegister)).perform(click());

        // Verify that an intent to the dialer was sent with the correct action, phone
        // number and package. Think of Intents intended API as the equivalent to Mockito's verify.
        intended(allOf(
                hasComponent(Register.class.getName())));
    }
    @Test
    public void checkLoginClickWithNoInfo() {
        onView(withId(R.id.appCompatButtonLogin)).perform(click());

        // Verify that an intent to the dialer was sent with the correct action, phone
        // number and package. Think of Intents intended API as the equivalent to Mockito's verify.
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Wrong email or password")));
    }
    @Test
    public void checkEmailTextInputWorks() {
        onView(withId(R.id.textInputEditTextEmail))
                .perform(typeText("Sam"), closeSoftKeyboard());

        onView(withId(R.id.textInputEditTextEmail)).check(matches(withText("Sam")));
    }
    @Test
    public void checkPasswordTextInputWorks() {
        onView(withId(R.id.textInputEditTextPassword))
                .perform(typeText("1234"), closeSoftKeyboard());

        onView(withId(R.id.textInputEditTextPassword)).check(matches(withText("1234")));
    }
    @Test
    public void checkLoginWorks() {
        onView(withId(R.id.textInputEditTextEmail))
                .perform(typeText("Sam"), closeSoftKeyboard());
        onView(withId(R.id.textInputEditTextPassword))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.appCompatButtonLogin)).perform(click());
        intended(allOf(
                hasComponent(MainActivity.class.getName())));
    }
    @Test
    public void checkLoginFails() {
        onView(withId(R.id.textInputEditTextEmail))
                .perform(typeText("J"), closeSoftKeyboard());
        onView(withId(R.id.textInputEditTextPassword))
                .perform(typeText("1235"), closeSoftKeyboard());
        onView(withId(R.id.appCompatButtonLogin)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Wrong email or password")));
    }
    @Test
    public void checkTextInputEmail() {
        onView(withId(R.id.textInputEditTextPassword))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.appCompatButtonLogin)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Wrong email or password")));
    }
    @Test
    public void checkTextInputPassword() {
        onView(withId(R.id.textInputEditTextEmail))
                .perform(typeText("Sam"), closeSoftKeyboard());
        onView(withId(R.id.appCompatButtonLogin)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Wrong email or password")));
    }

    @Test
    public void checkLoginClickfromRegister() {
        onView(withId(R.id.textViewLinkRegister)).perform(click());
        onView(withId(R.id.appCompatTextViewLoginLink)).perform(click());
        intended(allOf(
                hasComponent(Login.class.getName())));
    }

    @After
    public void tearDown() throws Exception{
        Intents.release();
    }
}