package com.moez.QKSMS.ui;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.moez.QKSMS.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GettingStartedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void gettingStartedTest() {
        ViewInteraction qKTextView = onView(
                allOf(withId(R.id.welcome_start), withText("Let's get started!"), isDisplayed()));
        qKTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.welcome_quickreply_title), withText("Pick a color"),
                        childAtPosition(
                                withParent(withId(R.id.welcome_pager)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Pick a color")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.welcome_skip), withText("FINISH"),
                        childAtPosition(
                                allOf(withId(R.id.welcome),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("FINISH")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.welcome_previous),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.welcome_next),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                4),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.welcome_next),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                4),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction imageView4 = onView(
                allOf(withId(R.id.welcome_next), isDisplayed()));
        imageView4.perform(click());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.welcome_pager),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewPager3.perform(swipeLeft());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.welcome_night_title), withText("Night mode"),
                        childAtPosition(
                                withParent(withId(R.id.welcome_pager)),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Night mode")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.welcome_night_hint), withText("Toggle me"),
                        childAtPosition(
                                withParent(withId(R.id.welcome_pager)),
                                3),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.welcome_skip), withText("FINISH"),
                        childAtPosition(
                                allOf(withId(R.id.welcome),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        textView5.check(matches(isDisplayed()));

        ViewInteraction imageView5 = onView(
                allOf(withId(R.id.welcome_previous),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                0),
                        isDisplayed()));
        imageView5.check(matches(isDisplayed()));

        ViewInteraction imageView6 = onView(
                allOf(withId(R.id.welcome_next),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                4),
                        isDisplayed()));
        imageView6.check(matches(isDisplayed()));

        ViewInteraction imageView7 = onView(
                allOf(withId(R.id.welcome_next),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.welcome),
                                        2),
                                4),
                        isDisplayed()));
        imageView7.check(matches(isDisplayed()));

        ViewInteraction robotoTextView = onView(
                allOf(withId(R.id.welcome_skip), withText("FINISH"),
                        withParent(allOf(withId(R.id.welcome),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        robotoTextView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.toolbar_title), withText("Conversations"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.root),
                                                0)),
                                0),
                        isDisplayed()));
        textView6.check(matches(withText("Conversations")));

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
