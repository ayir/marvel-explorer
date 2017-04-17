package com.riya.marvel.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.riya.marvel.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by RiyaSharma on 16-04-2017.
 */
public class Tools {

    public static final String URL_MARVEL = "http://www.marvel.com";

    public static final String PUBLIC_KEY = "4ac22d9ee0211e7776d5e08f1737a489";
    public static final String PRIVATE_KEY = "963f119536f48f5f90ad08cf8c683e517af1d364";

    public static final String TIMESTAMP = "ts";
    public static final String API_KEY = "apikey";
    public static final String HASH = "hash";

    public static Dialog dialogSpinner(Context context, String msg) {
        Dialog dialog = new Dialog(context, R.style.DialogSpinner);
        dialog.setContentView(R.layout.dialogspinner);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        message.setText(msg);
        return dialog;
    }

    public static void alertDialog(final Context context, final String msg) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.app_name))
                    .setMessage(msg)
                    .create();
            dialog.setButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();
        } catch (Exception e) {
        }
    }

    public static String genKeyUser() {
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        String hash = Tools.md5(ts + PRIVATE_KEY + PUBLIC_KEY);
        return "?" + TIMESTAMP + "=" + ts + "&" + API_KEY + "=" + PUBLIC_KEY + "&" +
                HASH + "=" + hash;
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            String hash = new String(Strings.hexEncode(digest.digest()));

            return hash;
            //byte messageDigest[] = digest.digest();

            // Create Hex String
            //StringBuffer hexString = new StringBuffer();
            //for (int i=0; i<messageDigest.length; i++)
            //    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            //return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
