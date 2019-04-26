package ru.vital.daily.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public final class ContactsProvider {

    public static List<String> getContactPhones(@Nullable Context context) {
        if (context != null)
            try {
                List<String> result = new ArrayList<>();
                Cursor phones = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);
                if (phones != null) {
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (phoneNumber != null) {
//                        exclude all non-digit characters
                            phoneNumber = phoneNumber.replaceAll("[^\\d+]", "");
                            result.add(phoneNumber);
                        }
                    }
                    phones.close();
                }
                return result;
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        return new ArrayList<>();
    }

}
