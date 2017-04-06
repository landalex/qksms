package com.moez.QKSMS.ui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.moez.QKSMS.BuildConfig;
import com.moez.QKSMS.ui.settings.SettingsFragment;
import com.moez.QKSMS.ui.welcome.WelcomeActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants=BuildConfig.class, sdk=22, manifest="src/main/AndroidManifest.xml")
public class MainActivityTest {

    @SuppressLint("CommitPrefEdits")
    @Test
    public void testLaunchWelcomeActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putBoolean(SettingsFragment.WELCOME_SEEN, false).commit();

        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().get(); //Robolectric.setupActivity(MainActivity.class);

//        Intent expectedIntent = new Intent(mainActivity, WelcomeActivity.class);
//        Assert.assertEquals(shadowOf(mainActivity).getNextStartedActivity(), expectedIntent);
        assertEquals(
                WelcomeActivity.class.getCanonicalName(),
                shadowOf(mainActivity).getNextStartedActivity().getComponent().getClassName());
    }

    @SuppressLint("CommitPrefEdits")
    @Test
    public void testSkipWelcomeActivity() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putBoolean(SettingsFragment.WELCOME_SEEN, true).commit();

        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        assertEquals(null, shadowOf(mainActivity).getNextStartedActivity());
    }
}