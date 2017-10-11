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
public class CreateMazeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
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
                allOf(withId(R.id.btnAddCave), withText("Agregar cueva"), isDisplayed()));
        button5.perform(click());

        ViewInteraction button6 = onView(
                allOf(withId(R.id.btnAddCave), withText("Agregar cueva"), isDisplayed()));
        button6.perform(click());

        ViewInteraction button7 = onView(
                allOf(withId(R.id.btnAddCave), withText("Agregar cueva"), isDisplayed()));
        button7.perform(click());

        ViewInteraction button8 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button8.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText2.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText3.perform(replaceText("1"), closeSoftKeyboard());

        ViewInteraction button9 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button9.perform(click());

        ViewInteraction button10 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button10.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText4.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText5.perform(replaceText("6"), closeSoftKeyboard());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText6.perform(replaceText("7"), closeSoftKeyboard());

        ViewInteraction button11 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button11.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Ok")));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction button12 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button12.perform(click());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText7.perform(click());

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText8.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText9.perform(replaceText("9"), closeSoftKeyboard());

        ViewInteraction button13 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button13.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Ok")));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction button14 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button14.perform(click());

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText10.perform(click());

        ViewInteraction editText11 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText11.perform(replaceText("12"), closeSoftKeyboard());

        ViewInteraction editText12 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText12.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction button15 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button15.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Ok")));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction button16 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button16.perform(click());

        ViewInteraction editText13 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText13.perform(click());

        ViewInteraction editText14 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText14.perform(replaceText("2"), closeSoftKeyboard());

        ViewInteraction editText15 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText15.perform(replaceText("4"), closeSoftKeyboard());

        ViewInteraction button17 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button17.perform(click());

        ViewInteraction button18 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button18.perform(click());

        ViewInteraction editText16 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText16.perform(click());

        ViewInteraction editText17 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText17.perform(replaceText("4"), closeSoftKeyboard());

        ViewInteraction editText18 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText18.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction button19 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button19.perform(click());

        ViewInteraction button20 = onView(
                allOf(withId(R.id.btnAddArc), withText("Agregar camino"), isDisplayed()));
        button20.perform(click());

        ViewInteraction editText19 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText19.perform(click());

        ViewInteraction editText20 = onView(
                allOf(withId(R.id.editTxtCave1), isDisplayed()));
        editText20.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction editText21 = onView(
                allOf(withId(R.id.editTxtCave2), isDisplayed()));
        editText21.perform(replaceText("3"), closeSoftKeyboard());

        ViewInteraction button21 = onView(
                allOf(withId(R.id.btnAccept), withText("Aceptar"), isDisplayed()));
        button21.perform(click());

        ViewInteraction button22 = onView(
                allOf(withId(R.id.imgBtnCheck), withText("Guardar dibujo"), isDisplayed()));
        button22.perform(click());

        ViewInteraction editText22 = onView(
                allOf(withId(R.id.editTxtNameMaze), isDisplayed()));
        editText22.perform(click());

        ViewInteraction editText23 = onView(
                allOf(withId(R.id.editTxtNameMaze), isDisplayed()));
        editText23.perform(replaceText("prueba1"), closeSoftKeyboard());

        ViewInteraction button23 = onView(
                allOf(withId(R.id.btnAcceptName), withText("Aceptar"), isDisplayed()));
        button23.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Sí")));
        appCompatButton5.perform(scrollTo(), click());

        pressBack();

        pressBack();

        ViewInteraction button24 = onView(
                allOf(withId(R.id.bttnChooseLib), withText("Biblioteca de laberintos"), isDisplayed()));
        button24.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.text_view_item), withText("Nombre: prueba1-1507735749\nNúmero de cuevas: 5"),
                        childAtPosition(
                                withId(R.id.listViewMazes),
                                0),
                        isDisplayed()));
        textView.perform(click());

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
