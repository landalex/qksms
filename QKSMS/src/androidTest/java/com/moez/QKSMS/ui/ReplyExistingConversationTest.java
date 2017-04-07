package com.moez.QKSMS.ui;


import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.moez.QKSMS.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReplyExistingConversationTest extends MainActivityTest{

    @Test
    public void replyExistingConversationTest() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.conversations_list), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        long timestamp = System.currentTimeMillis();
        String messageString = "Another Message " + timestamp;

        ViewInteraction qKEditText = onView(
                allOf(withId(R.id.compose_reply_text), isDisplayed()));
        qKEditText.perform(replaceText(messageString), closeSoftKeyboard());

        ViewInteraction sendButton = onView(
                allOf(withId(R.id.compose_button), isDisplayed()));
        sendButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withText(messageString),
                        isDisplayed()));
        textView.check(matches(withText(messageString)));

    }
}
