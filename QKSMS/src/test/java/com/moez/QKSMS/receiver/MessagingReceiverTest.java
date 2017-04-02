package com.moez.QKSMS.receiver;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.moez.QKSMS.BuildConfig;
import com.moez.QKSMS.common.utils.PhoneNumberUtils;
import com.moez.QKSMS.service.NotificationService;

import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(constants=BuildConfig.class, sdk=22, manifest="src/main/AndroidManifest.xml")
public class MessagingReceiverTest {

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

    @Test
    public void testReceivedMessage() throws Exception {
        Application application = RuntimeEnvironment.application;
        MessagingReceiver messagingReceiver = new MessagingReceiver();
        shadowOf(application).clearStartedServices();

        ShadowBroadcastReceiver shadowBroadcastReceiver = shadowOf(messagingReceiver);
        Intent fakeIntent = createFakeSmsReceivedIntent("5555555555", "Test");

        Cursor mockCursor = Mockito.mock(Cursor.class);
        Mockito.doReturn(true).when(mockCursor).moveToFirst();
        Mockito.doReturn(0L).when(mockCursor).getLong(anyInt());
        Mockito.doNothing().when(mockCursor).close();

        ContentProvider mockProvider = Mockito.mock(ContentProvider.class);
        Mockito.doReturn(mockCursor).when(mockProvider)
                .query(any(Uri.class), any(String[].class), anyString(), any(String[].class), anyString());
        Mockito.doReturn(Uri.parse("content://sms/inbox/1")).when(mockProvider).insert(any(Uri.class), any(ContentValues.class));

        ShadowContentResolver.registerProvider("sms", mockProvider);

        shadowBroadcastReceiver.onReceive(application, fakeIntent, new AtomicBoolean());

        Intent expectedService = new Intent(application, NotificationService.class);
        Intent serviceIntent = shadowOf(application).getNextStartedService();
        assertNotNull("Service started ",serviceIntent);
        assertEquals("Started service class ", serviceIntent.getComponent(),
                expectedService.getComponent());

    }
}