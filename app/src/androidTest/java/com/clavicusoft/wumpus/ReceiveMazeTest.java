package com.clavicusoft.wumpus;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.clavicusoft.wumpus.Select.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReceiveMazeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void receiveMazeTest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.buttonMultijuador), withText("Multijugador"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.btVisible), withText("Recibir"), isDisplayed()));
        button2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Sí")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button2), withText("No")));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction button3 = onView(
                allOf(withId(R.id.buttonMultijuador), withText("Multijugador"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button3.perform(click());

        pressBack();

        ViewInteraction button4 = onView(
                allOf(withId(R.id.Individual), withText("Individual"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button4.perform(click());

        ViewInteraction button5 = onView(
                allOf(withId(R.id.bttnChooseLib), withText("Biblioteca de laberintos"), isDisplayed()));
        button5.perform(click());

    }

}
