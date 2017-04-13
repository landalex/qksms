package com.moez.QKSMS.mmssms;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SettingsTest {

    @Test
    public void testCopyConstructor() {
        String port = "100";
        Settings settings = new Settings();
        settings.setPort(port);

        Settings settingsCopy = new Settings(settings);
        assertEquals(settingsCopy.getPort(), port);
    }

    @Test
    public void testSetMmsc() {
        String mmsc = "Test";
        Settings settings = new Settings();
        settings.setMmsc(mmsc);
        assertEquals(settings.getMmsc(), mmsc);
    }

    @Test
    public void testSetProxy() {
        String proxy = "Proxy";
        Settings settings = new Settings();
        settings.setProxy(proxy);
        assertEquals(settings.getProxy(), proxy);
    }

    @Test
    public void testGroup() {
        Settings settings = new Settings();
        settings.setGroup(true);
        assertTrue(settings.getGroup());
    }

    @Test
    public void testSetMaxAttachmentSize() {
        long attachmentSize = 100;
        Settings settings = new Settings();
        settings.setMaxAttachmentSize(attachmentSize);
        assertEquals(settings.getMaxAttachmentSize(), attachmentSize);
    }

    @Test
    public void testSetSignature() {
        String signature = "test";
        Settings settings = new Settings();
        settings.setSignature(signature);
        assertEquals(settings.getSignature(), signature);
    }

    @Test
    public void testDeliveryReports() {
        Settings settings = new Settings();
        settings.setDeliveryReports(true);
        assertTrue(settings.getDeliveryReports());
    }

}
