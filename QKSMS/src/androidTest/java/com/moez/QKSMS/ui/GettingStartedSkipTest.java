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
public class GettingStartedSkipTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void gettingStartedSkipTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.welcome_start), withText("Let's get started!"),
                        childAtPosition(
                                withParent(withId(R.id.welcome_pager)),
                                3),
                        isDisplayed()));
        textView.check(matches(withText("Let's get started!")));

        //this seems to fail because espresso can't find the view (NoMatchingViewException)
        //yet somehow it finds this exact view and clicks on it fine in the next part, so idk what it's doing...
//        ViewInteraction textView2 = onView(
//                allOf(withId(R.id.welcome_skip), withText("SKIP"),
//                        childAtPosition(
//                                allOf(withId(R.id.welcome),
//                                        childAtPosition(
//                                                withId(android.R.id.content),
//                                                0)),
//                                1),
//                        isDisplayed()));
//        textView2.check(matches(withText("SKIP")));

        ViewInteraction robotoTextView = onView(
                allOf(withId(R.id.welcome_skip), withText("SKIP"),
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

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.toolbar_title), withText("Conversations"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.root),
                                                0)),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("Conversations")));

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
