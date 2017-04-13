package com.moez.QKSMS.mmssms;

import android.graphics.Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class MessageTest {

    private static final Bitmap TEST_INPUT_BITMAP = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    private static final String TEST_SUBJECT = "Test Subject";

    private static final String MIME_TYPE_AUDIO = "audio/wav";
    private static final String MIME_TYPE_VIDEO = "video/3gpp";

    @Test
    public void testMimeTypeAudio() {
        Message message = new Message("test", "test");
        message.setAudio("Audio".getBytes());
        assertEquals(message.getMediaMimeType(), MIME_TYPE_AUDIO);
    }

    @Test
    public void testMimeTypeVideo() {
        Message message = new Message("test", "test");
        message.setVideo("Video".getBytes());
        assertEquals(message.getMediaMimeType(), MIME_TYPE_VIDEO);
    }

    @Test
    public void testMimeTypeMedia() {
        Message message = new Message("test", "test");

        message.setMedia("Audio".getBytes(), MIME_TYPE_AUDIO);
        assertEquals(message.getMediaMimeType(), MIME_TYPE_AUDIO);

        message.setMedia("Video".getBytes(), MIME_TYPE_VIDEO);
        assertEquals(message.getMediaMimeType(), MIME_TYPE_VIDEO);
    }

    /**
     * Tests to ensure all constructors that take a single string argument of multiple addresses behave the same way
     * i.e. they all split the single address string into multiple strings and remove any excess padding
     */
    @Test
    public void testMultipleAddressConstructors() {
        //constructors should get rid of extra "padding" (spaces) at the beginning and end of input string
        final String testAddressInputs = " Address1 Address2 Address3 Address4 ";
        final String[] expectedAddressOutputs = {"Address1", "Address2", "Address3", "Address4"};

        Message messageTest = new Message("Text", testAddressInputs);
        testArrayEquality(expectedAddressOutputs, messageTest.getAddresses());

        Message messageSubjectTest = new Message("Text", testAddressInputs, "Subject");
        testArrayEquality(expectedAddressOutputs, messageSubjectTest.getAddresses());

        Message messageBitmapTest = new Message("Text", testAddressInputs, TEST_INPUT_BITMAP);
        testArrayEquality(expectedAddressOutputs, messageBitmapTest.getAddresses());

        Message messageBitmapSubject = new Message("Text", testAddressInputs, TEST_INPUT_BITMAP, "Subject");
        testArrayEquality(expectedAddressOutputs, messageBitmapSubject.getAddresses());

        Message messageBitmaps = new Message("Text", testAddressInputs, new Bitmap[]{TEST_INPUT_BITMAP});
        testArrayEquality(expectedAddressOutputs, messageBitmaps.getAddresses());

        Message messageBitmapsSubject = new Message("Test", testAddressInputs, new Bitmap[]{TEST_INPUT_BITMAP}, "Subject");
        testArrayEquality(expectedAddressOutputs, messageBitmapsSubject.getAddresses());
    }

    private void testArrayEquality(Object[] expectedArray, Object[] actualArray) {
        assertEquals("Array Lengths differ. expectedArray: " + expectedArray.length + " actualArray: " + actualArray.length, expectedArray.length, actualArray.length);
        int arraySize = expectedArray.length;
        for (int i = 0; i < arraySize; i++) {
            assertEquals(expectedArray[i], actualArray[i]);
        }
    }

    @Test
    public void testSetAddress() {
        final String testAddress = "ADDRESS";
        Message message = new Message();
        message.setAddress(testAddress);
        assertEquals(message.getAddresses()[0], testAddress);
    }

    @Test
    public void testSetImage() {
        Message message = new Message();
        message.setImage(TEST_INPUT_BITMAP);
        assertEquals(message.getImages()[0], TEST_INPUT_BITMAP);
    }

    @Test
    public void testSetSubject() {
        Message message = new Message();
        message.setSubject(TEST_SUBJECT);
        assertEquals(message.getSubject(), TEST_SUBJECT);
    }

    @Test
    public void testSetSave() {
        Message message = new Message();
        message.setSave(false);
        assertFalse(message.getSave());

        message.setSave(true);
        assertTrue(message.getSave());
    }

    @Test
    public void testSetMessageType() {
        Message message = new Message();
        message.setType(Message.TYPE_SMSMMS);
        assertEquals(message.getType(), Message.TYPE_SMSMMS);

        message.setType(Message.TYPE_VOICE);
        assertEquals(message.getType(), Message.TYPE_VOICE);
    }

    @Test
    public void testSetDelay() {
        final int delay = 10;
        Message message = new Message();
        message.setDelay(delay);
        assertEquals(message.getDelay(), delay);
    }

    @Test
    public void testAddImage() {
        Bitmap inputBitmap1 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Bitmap inputBitmap2 = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        Bitmap inputBitmap3 = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);

        Bitmap[] expectedOutput = {inputBitmap1, inputBitmap2, inputBitmap3};

        Message message = new Message();
        message.addImage(inputBitmap1);
        message.addImage(inputBitmap2);
        message.addImage(inputBitmap3);

        Bitmap[] bitmaps = message.getImages();
        testArrayEquality(bitmaps, expectedOutput);
    }

    @Test
    public void testAddAddress() {
        //input and output differ because the default constructor adds an empty address by default
        String[] inputAddresses = {"address1", "address2", "address3"};
        String[] outputAddresses = {"", "address1", "address2", "address3"};

        Message message = new Message();
        for (String address : inputAddresses) {
            message.addAddress(address);
        }

        testArrayEquality(outputAddresses, message.getAddresses());
    }

    @Test
    public void testConstructorDefaults() {
        //TODO: write checks for every constructor - perhaps write a method that creates a message for every constructor and passes each instance to an anonymous object that tests desired conditions
        Message message = new Message();
        checkDefaultsTrue(message);

        Message message1 = new Message("Text", "Address", new Bitmap[] {TEST_INPUT_BITMAP});
        checkDefaultsTrue(message1);

        Message message2 = new Message("Text", "Address", new Bitmap[] {TEST_INPUT_BITMAP}, "SUBJECT");
        checkDefaultsTrue(message2);
    }

    public void checkDefaultsTrue(Message message) {
        assertEquals(message.getType(), Message.TYPE_SMSMMS);
        assertEquals(message.getDelay(), 0);
        assertNull(message.getMediaMimeType());
        assertTrue(message.getSave());
    }

    @Test
    public void testBitmapToByteArray() {
        assertEquals(0, Message.bitmapToByteArray(null).length);
    }
}
