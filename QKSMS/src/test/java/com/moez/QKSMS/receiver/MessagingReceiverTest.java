package com.moez.QKSMS.receiver;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.moez.QKSMS.BuildConfig;
import com.moez.QKSMS.common.ConversationPrefsHelper;
import com.moez.QKSMS.common.utils.PhoneNumberUtils;
import com.moez.QKSMS.data.Message;
import com.moez.QKSMS.service.NotificationService;
import com.moez.QKSMS.transaction.NotificationManager;
import com.moez.QKSMS.transaction.SmsHelper;
import com.moez.QKSMS.ui.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBroadcastReceiver;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants=BuildConfig.class, sdk=22, manifest="src/main/AndroidManifest.xml")
public class MessagingReceiverTest {
    private static final String SMS_PROVIDER = "sms";
    private static final String MMS_SMS_PROVIDER = "mms-sms";
    private static final String SAMPLE_ADDRESS = "5555555555";
    private static final String SAMPLE_BODY = "Test";
    private static final String SAMPLE_SMS_URI = "content://sms/inbox/1";
    private static final String ADDRESS_KEY = "address";
    private static final String BODY_KEY = "body";
    private static final String DATE_SENT_KEY = "date_sent";

    private Application application;
    private ShadowBroadcastReceiver shadowBroadcastReceiver;

    private Intent createFakeSmsReceivedIntent(String sender, String body) {
        byte[] pdu = null;
        byte[] scBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD("0000000000");
        byte[] senderBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte[] dateBytes = new byte[7];
        Calendar calendar = new GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(lsmcs);
            bo.write(scBytes);
            bo.write(0x04);
            bo.write((byte) sender.length());
            bo.write(senderBytes);
            bo.write(0x00);
            bo.write(0x00); // encoding: 0 for default 7bit
            bo.write(dateBytes);
            try {
                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
                Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod(
                        "stringToGsm7BitPacked", new Class[] { String.class });
                stringToGsm7BitPacked.setAccessible(true);
                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null,
                        body);
                bo.write(bodybytes);
            } catch (Exception e) {
            }

            pdu = bo.toByteArray();
        } catch (IOException e) {
        }

        Intent intent = new Intent();
        intent.setClassName("com.android.mms",
                "com.android.mms.transaction.SmsReceiverService");
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        return intent;
    }

    private static byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }

    private ContentProvider buildMockContentProvider(Uri fakeUri, Cursor mockCursor) {
        ContentProvider mockProvider = Mockito.mock(ContentProvider.class);
        Mockito.doReturn(mockCursor).when(mockProvider)
                .query(any(Uri.class), any(String[].class), anyString(), any(String[].class), anyString());
        Mockito.doReturn(1).when(mockProvider)
                .update(any(Uri.class), any(ContentValues.class), anyString(), any(String[].class));
        Mockito.doReturn(fakeUri).when(mockProvider).insert(any(Uri.class), any(ContentValues.class));
        return mockProvider;
    }

    private Cursor buildMockCursor() {
        Cursor mockCursor = Mockito.mock(Cursor.class);
        Mockito.doReturn(true).when(mockCursor).moveToFirst();
        Mockito.doReturn(0L).when(mockCursor).getLong(anyInt());
        Mockito.doNothing().when(mockCursor).close();
        return mockCursor;
    }

    @Before
    public void setUp() throws Exception {
        NotificationManager.init(RuntimeEnvironment.application);
        application = RuntimeEnvironment.application;
        shadowBroadcastReceiver = shadowOf(new MessagingReceiver());
        shadowOf(application).clearStartedServices();
    }

    @Test
    public void testReceiveNullIntent() throws Exception {
        shadowBroadcastReceiver.onReceive(application, new Intent(), new AtomicBoolean());
        assertNull(shadowOf(application).getNextStartedService());
    }

    @Test
    public void testNotificationOnReceivedMessage() throws Exception {
        Intent fakeIntent = createFakeSmsReceivedIntent(SAMPLE_ADDRESS, SAMPLE_BODY);

        ContentProvider mockProvider = buildMockContentProvider(Uri.parse(SAMPLE_SMS_URI), buildMockCursor());
        ShadowContentResolver.registerProvider(SMS_PROVIDER, mockProvider);

        shadowBroadcastReceiver.onReceive(application, fakeIntent, new AtomicBoolean());

        Intent expectedService = new Intent(application, NotificationService.class);
        Intent serviceIntent = shadowOf(application).getNextStartedService();
        assertNotNull("Service not started ",serviceIntent);
        assertEquals("Service Component is not the expected Notification Service", expectedService.getComponent(),
                serviceIntent.getComponent());
    }

    @Test
    public void testDisabledNotificationOnReceivedMessage() throws Exception {
        Intent fakeIntent = createFakeSmsReceivedIntent(SAMPLE_ADDRESS, SAMPLE_BODY);

        Uri fakeUri = Uri.parse(SAMPLE_SMS_URI);
        ContentProvider mockProvider = buildMockContentProvider(fakeUri, buildMockCursor());
        ShadowContentResolver.registerProvider(SMS_PROVIDER, mockProvider);
        ShadowContentResolver.registerProvider(MMS_SMS_PROVIDER, mockProvider);

        Message message = new Message(application, fakeUri);
        ConversationPrefsHelper conversationPrefs = new ConversationPrefsHelper(application, message.getThreadId());
        conversationPrefs.putBoolean(SettingsFragment.NOTIFICATIONS, false);

        shadowBroadcastReceiver.onReceive(application, fakeIntent, new AtomicBoolean());

        assertNull(shadowOf(application).getNextStartedService());
        ArgumentCaptor<Uri> uriCaptor = ArgumentCaptor.forClass(Uri.class);
        Mockito.verify(mockProvider).update(uriCaptor.capture(), any(ContentValues.class), anyString(), any(String[].class));
        assertEquals(Uri.parse("content://sms/0"), uriCaptor.getValue());
    }

    @Test
    public void testReceivedSmsAddedToInbox() throws Exception {
        Intent fakeIntent = createFakeSmsReceivedIntent(SAMPLE_ADDRESS, SAMPLE_BODY);

        final long timestamp = System.currentTimeMillis();
        Uri fakeUri = Uri.parse(SAMPLE_SMS_URI);
        ContentValues fakeContentValues = new ContentValues();
        fakeContentValues.put(ADDRESS_KEY, SAMPLE_ADDRESS);
        fakeContentValues.put(BODY_KEY, SAMPLE_BODY);
        fakeContentValues.put(DATE_SENT_KEY, timestamp);

        ContentProvider mockProvider = buildMockContentProvider(fakeUri, buildMockCursor());

        ShadowContentResolver.registerProvider(SMS_PROVIDER, mockProvider);

        shadowBroadcastReceiver.onReceive(application, fakeIntent, new AtomicBoolean());

        ArgumentCaptor<Uri> uriCaptor = ArgumentCaptor.forClass(Uri.class);
        ArgumentCaptor<ContentValues> cvCaptor = ArgumentCaptor.forClass(ContentValues.class);
        Mockito.verify(mockProvider, times(1)).insert(uriCaptor.capture(), cvCaptor.capture());

        assertEquals(SmsHelper.RECEIVED_MESSAGE_CONTENT_PROVIDER, uriCaptor.getValue());
        assertEquals(SAMPLE_ADDRESS, cvCaptor.getValue().get(ADDRESS_KEY));
        assertEquals(SAMPLE_BODY, cvCaptor.getValue().get(BODY_KEY));
    }
}