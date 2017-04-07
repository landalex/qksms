package com.moez.QKSMS.ui;


import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.moez.QKSMS.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewConversationTest extends MainActivityTest {

    @Test
    public void newConversationTest() {
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recipientsEditTextView = onView(
                allOf(withId(R.id.compose_recipients),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                0),
                        isDisplayed()));
        recipientsEditTextView.check(matches(isDisplayed()));

        ViewInteraction editText = onView(
                allOf(withHint(R.string.hint_reply), isDisplayed()));
        editText.check(matches(withHint("Enter a message")));

        String randomAddress = String.valueOf(System.currentTimeMillis()).substring(0, 10);

        ViewInteraction autoCompleteContactView = onView(
                withId(R.id.compose_recipients));
        autoCompleteContactView.perform(click(), replaceText(randomAddress));

        String messageString = "Hello World " + randomAddress;

        ViewInteraction qKEditText = onView(
                allOf(withId(R.id.compose_reply_text), isDisplayed()));
        qKEditText.perform(click(), replaceText(messageString), closeSoftKeyboard());

        ViewInteraction sendButton = onView(
                allOf(withId(R.id.compose_button), isDisplayed()));
        sendButton.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withText(messageString), isDisplayed()));
        textView.check(matches(withText(messageString)));

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
