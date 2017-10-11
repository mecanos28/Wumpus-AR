package com.example.benja.canvas;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SendMazeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest2() {
        ViewInteraction button = onView(
                allOf(withId(R.id.Individual), withText("Individual"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.bttnDrawLab), withText("Dibujar un laberinto"), isDisplayed()));
        button2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("Ok")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction button3 = onView(
                allOf(withId(R.id.btnAddCave), withText("Agregar cueva"), isDisplayed()));
        button3.perform(click());

        ViewInteraction button4 = onView(
                allOf(withId(R.id.btnAddCave), withText("Agregar cueva"), isDisplayed()));
        button4.perform(click());

        ViewInteraction button5 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button5.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText2.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText3.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction button6 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button6.perform(click());

        ViewInteraction button7 = onView(
                allOf(withId(R.id.imgBtnCheck), withText("Guardar dibujo"), isDisplayed()));
        button7.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.editTxtNameMaze), isDisplayed()));
        editText4.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.editTxtNameMaze), isDisplayed()));
        editText5.perform(replaceText("prueba "), closeSoftKeyboard());

        ViewInteraction button8 = onView(
                allOf(withId(R.id.btnAcceptName), withText("Aceptar"), isDisplayed()));
        button8.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button2), withText("No")));
        appCompatButton2.perform(scrollTo(), click());

        pressBack();

        pressBack();

        ViewInteraction button9 = onView(
                allOf(withId(R.id.buttonMultijuador), withText("Multijugador"),
                        withParent(withId(R.id.linearLayout)),
                        isDisplayed()));
        button9.perform(click());

        ViewInteraction button10 = onView(
                allOf(withId(R.id.btOnBluetooth), withText("Enviar"), isDisplayed()));
        button10.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.text_view_item), withText("Nombre: prueba -1507736207\nNúmero de cuevas: 2"),
                        childAtPosition(
                                withId(R.id.listViewMazes),
                                0),
                        isDisplayed()));
        textView.perform(click());

        ViewInteraction button11 = onView(
                allOf(withId(R.id.scan), withText("Seleccione un dispositivo"), isDisplayed()));
        button11.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withText("HUAWEI P8\n0C:D6:BD:8A:8F:B4"),
                        childAtPosition(
                                withId(R.id.paired_devices),
                                2),
                        isDisplayed()));
        textView2.perform(click());

        ViewInteraction button12 = onView(
                allOf(withId(R.id.button_send), withText("Enviar laberinto"), isDisplayed()));
        button12.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
