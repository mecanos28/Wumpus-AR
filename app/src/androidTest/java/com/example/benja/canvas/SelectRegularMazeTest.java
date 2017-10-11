package com.example.benja.canvas;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SelectRegularMazeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void selectRegularMazeTest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.Individual), withText("Individual"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.bttnStartGame), withText("Iniciar el juego"), isDisplayed()));
        button2.perform(click());

        pressBack();

        ViewInteraction button3 = onView(
                allOf(withId(R.id.bttnStartGame), withText("Iniciar el juego"), isDisplayed()));
        button3.perform(click());

        pressBack();

        ViewInteraction button4 = onView(
                allOf(withId(R.id.bttnStartGame), withText("Iniciar el juego"), isDisplayed()));
        button4.perform(click());

        pressBack();

        ViewInteraction button5 = onView(
                allOf(withId(R.id.bttnStartGame), withText("Iniciar el juego"), isDisplayed()));
        button5.perform(click());

        pressBack();

        ViewInteraction button6 = onView(
                allOf(withId(R.id.bttnStartGame), withText("Iniciar el juego"), isDisplayed()));
        button6.perform(click());

        pressBack();

    }

}
