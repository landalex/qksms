package com.moez.QKSMS.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.moez.QKSMS.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;

/**
 * Base test class for tests beginning in the Main Activity. Provides a setUp method that disables
 * the Welcome screen and MMS setup dialogs.
 */

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void setUp() throws Exception {
        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();
        sharedPreferences.putBoolean(SettingsFragment.WELCOME_SEEN, true);
        sharedPreferences.putBoolean(MainActivity.MMS_SETUP_DONT_ASK_AGAIN, true);
        sharedPreferences.commit();
        mActivityTestRule.launchActivity(null);
    }
}
