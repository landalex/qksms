package com.moez.QKSMS.mmssms;

import android.content.Context;
import android.content.Intent;

import com.moez.QKSMS.receiver.MarkReadReceiver;
import com.moez.QKSMS.service.MarkReadService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Trevor on 3/29/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*"})
@Config(sdk = 22)
//@PrepareForTest(Context.class)
public class ReceiverTest {
    Context fakeContext;


    @Before
    public void setup() {
        fakeContext = PowerMockito.mock(Context.class);
    }

    @Test
    public void testMarkReadReceiver() {
//        MarkReadReceiver receiver = new MarkReadReceiver();
//        when(fakeContext.startService(any(Intent.class)));
//        receiver.onReceive(fakeContext, new Intent().putExtra("thread_id", 21));
//        fakeContext.startService(null);
//        verify(fakeContext, times(1)).startService(any(Intent.class));
//        try {
//            PowerMockito.verifyNew(Intent.class).withArguments(fakeContext, MarkReadService.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
