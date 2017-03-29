package com.moez.QKSMS.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.test.ProviderTestCase2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Trevor on 3/28/2017.
 */
public class ContactHelperTest extends ProviderTestCase2 {
    public ContactHelperTest(Class providerClass, String providerAuthority) {
        super(providerClass, ContactsContract.AUTHORITY);
    }


    public void testMeow() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Contacts.DISPLAY_NAME, "Trevor");
        Uri resultingUri = getMockContentResolver().insert(ContactsContract.AUTHORITY_URI, contentValues);

        ContentValues addressValues = new ContentValues();
        addressValues.put(Telephony.TextBasedSmsColumns.ADDRESS, "Hello");
        Uri newResultingUri = getMockContentResolver().insert(Message.SMS_CONTENT_PROVIDER, addressValues);
//        contentValues.put(, "Hello");
        String name = ContactHelper.getName(getMockContext(), "Hello");
        assertTrue("Trevor".equals(name));
    }
}