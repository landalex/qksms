package com.moez.QKSMS.mmssms;

import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.SmsManager;

import com.moez.QKSMS.BuildConfig;
import com.moez.QKSMS.common.QKPreferences;

import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSmsManager;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.times;
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

    @Ignore
    @Test
    public void testSendSms() throws Exception {
        final String address = "5555555555";
        final String body = "Hello World";
        ArrayList<String> parts = new ArrayList<>();
        parts.add(body);

        Message message = new Message(body, address);
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());

        Transaction spyTransaction = PowerMockito.spy(transaction);
        PowerMockito
                .when(spyTransaction,
                        Transaction.class.getMethod("sendDelayedSms", SmsManager.class, String.class, String.class,
                                ArrayList.class, ArrayList.class, int.class, Uri.class));
//                        "sendDelayedSms", SmsManager.getDefault(), address, parts,
//                        new ArrayList<>(), new ArrayList<>(), 0, null).thenReturn(null);

//        transaction.sendNewMessage(message, 0);

        PowerMockito.verifyPrivate(spyTransaction, after(1000).times(1))
                .invoke("sendDelayedSms", SmsManager.getDefault(), address, parts,
                        any(ArrayList.class), any(ArrayList.class), anyInt(), any(Uri.class));
    }

    @Ignore
    @Test
    // TODO: Figure out how to delay this enough to let the thread in sendDelayedSms run
    public void testSendDelayedSms() throws Exception {
        final String address = "5555555555";
        final String body = "Hello World";
        ArrayList<String> parts = new ArrayList<>();
        parts.add(body);

        Message message = new Message(body, address);
        Transaction transaction = new Transaction(RuntimeEnvironment.application, new Settings());
        transaction.sendNewMessage(message, 0);

        ShadowSmsManager shadowSmsManager = shadowOf(SmsManager.getDefault());
        ShadowSmsManager.TextMultipartParams sentParams = shadowSmsManager.getLastSentMultipartTextMessageParams();

        assertEquals(sentParams.getDestinationAddress(), address);
        assertEquals(sentParams.getParts().get(0), parts.get(0));
    }
}