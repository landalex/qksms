package com.moez.QKSMS.mmssms;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.SmsManager;

import com.android.mms.transaction.ProgressCallbackEntity;
import com.moez.QKSMS.common.QKPreferences;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowSmsManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.after;
import static org.robolectric.Shadows.shadowOf;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*"})
@Config(sdk = 22)
@PrepareForTest(Transaction.class)
public class TransactionTest {

    @Before
    public void setUp() throws Exception {
        QKPreferences.init(RuntimeEnvironment.application);
    }

    @Test
    public void testCheckMms() throws Exception {
        Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Message message = new Message("Test", "5555555555", bitmap);
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());
        assertTrue(transaction.checkMMS(message));
    }

    @Test
    public void testCheckMmsFalse() throws Exception {
        Message message = new Message("Test", "5555555555");
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());
        assertFalse(transaction.checkMMS(message));
    }

    @Test
    public void testSendSms() throws Exception {
        final String text = "Test";
        final String[] addresses = new String[] {"5555555555"};
        Message message = new Message(text, addresses);
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());

        Cursor mockCursor = Mockito.mock(Cursor.class);
        Mockito.doReturn(true).when(mockCursor).moveToFirst();
        Mockito.doReturn(0L).when(mockCursor).getLong(anyInt());
        Mockito.doNothing().when(mockCursor).close();

        ContentProvider mockProvider = Mockito.mock(ContentProvider.class);
        Mockito.doReturn(mockCursor).when(mockProvider)
                .query(any(Uri.class), any(String[].class), anyString(), any(String[].class), anyString());
        Mockito.doReturn(Uri.parse("content://sms/inbox/1")).when(mockProvider).insert(any(Uri.class), any(ContentValues.class));

        ShadowContentResolver.registerProvider("sms", mockProvider);

        transaction.sendNewMessage(message, 0);

        Thread.sleep(1000);

        ShadowSmsManager shadowSmsManager = shadowOf(SmsManager.getDefault());
        ShadowSmsManager.TextMultipartParams sentParams = shadowSmsManager.getLastSentMultipartTextMessageParams();

        assertNotNull(sentParams);
        assertEquals(sentParams.getDestinationAddress(), addresses[0]);
        assertEquals(sentParams.getParts().get(0), text);
    }

    @Ignore
    @Test
    public void testSendNewMms() throws Exception {
        final String text = "Test";
        final String[] addresses = new String[] {"5555555555"};
        Bitmap[] bitmaps = new Bitmap[] {Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)};
        Message message = new Message(text, addresses, bitmaps);
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());

        NetworkInfo mockInfo = Mockito.mock(NetworkInfo.class);
        Mockito.doReturn(NetworkInfo.State.CONNECTED).when(mockInfo).getState();

        ShadowConnectivityManager shadowConnectivityManager = shadowOf((ConnectivityManager)RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE));
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_MOBILE_MMS, mockInfo);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ProgressCallbackEntity.PROGRESS_STATUS_ACTION);
        BroadcastReceiver receiver = Mockito.mock(BroadcastReceiver.class);
        RuntimeEnvironment.application.registerReceiver(receiver, filter);

        transaction.sendNewMessage(message, 0);

        Mockito.verify(receiver, after(1000)).onReceive(any(Context.class), any(Intent.class));
    }

}