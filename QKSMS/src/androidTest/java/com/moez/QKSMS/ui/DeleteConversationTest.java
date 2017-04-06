package com.moez.QKSMS.ui;


import android.support.test.espresso.ViewInteraction;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.moez.QKSMS.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class DeleteConversationTest extends MainActivityTest{

    @Test
    public void deleteConversationTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String randomAddress = String.valueOf(System.currentTimeMillis()).substring(0, 10);

        ViewInteraction autoCompleteContactView = onView(
                withId(R.id.compose_recipients));
        autoCompleteContactView.perform(scrollTo(), typeText(randomAddress), closeSoftKeyboard());

        ViewInteraction qKEditText = onView(
                allOf(withId(R.id.compose_reply_text), isDisplayed()));
        qKEditText.perform(click(), replaceText("Test"), closeSoftKeyboard());

        ViewInteraction sendButton = onView(
                allOf(withId(R.id.compose_button), isDisplayed()));
        sendButton.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.root)))),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction conversationListItemView = onView(
                allOf(withText(randomAddress),
//                        withParent(withId(R.id.conversations_list)),
                        isDisplayed()));
        conversationListItemView.check(matches(isDisplayed()));
        conversationListItemView.perform(longClick());

//        ViewInteraction recyclerView = onView(
//                allOf(withId(R.id.conversations_list), isDisplayed()));
//        recyclerView.perform(actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction deleteButton = onView(
                        withContentDescription("Delete"));
        deleteButton.perform(click());

        ViewInteraction qKTextView = onView(
                allOf(withId(R.id.buttonPositive), withText("YES"),
                        withParent(allOf(withId(R.id.buttonPanel),
                                withParent(withId(R.id.parentPanel)))),
                        isDisplayed()));
        qKTextView.perform(click());

        conversationListItemView.check(doesNotExist());
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
