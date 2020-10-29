package com.dietmanager.deliveryboy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.dietmanager.deliveryboy.R;
import com.dietmanager.deliveryboy.api.ApiInterface;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;

/**
 * Created by Tamil on 10/14/2017.
 */

public class Utils {
    public static boolean showLog = true;
    public static String address = "";
    Retrofit retrofit;
    ApiInterface apiInterface;

    public static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static void displayMessage(Activity activity, String toastString) {
        try {
            Snackbar.make(activity.getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(activity, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }


    public static File storeInFile(Context context, Bitmap bitmap, String filename, String type) {
        File f = new File(context.getCacheDir(), filename);
        try {
            //create a file to write bitmap data
            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (type.contains("png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            else
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);

            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;

        //Convert bitmap to byte array

    }


    public static String getTimeFromString(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, hh:mm aa");

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }


    public static boolean checktimings(String time) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(currentTime);

            if (date1.after(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        if (myDate.before(today)) {
            return false;
        }
        return true;
    }

    public static String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    public static String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String mDate = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        return mDate;
    }

    public static String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String time = new SimpleDateFormat("hh:mm aaa").format(cal.getTime());
        return time;
    }


    public static String getDeliveryTime(String date) throws ParseException {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String time = new SimpleDateFormat("hh:mm aaa").format(cal.getTime());
        return time;
    }

    public static String getDisplayDateAndTime(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat dateStringFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = dateFormat.parse(dateString);
            return dateStringFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getOrderScheduleDateAndTime(String dateString) {
        if (TextUtils.isEmpty(dateString)) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dateStringFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = dateFormat.parse(dateString);
            return dateStringFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void print(String tag, String message) {
        if (showLog) {
            Log.v(tag, message);
        }
    }

    public static String toFirstCharUpperAll(String string) {
        StringBuffer sb = new StringBuffer(string);
        for (int i = 0; i < sb.length(); i++)
            if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        return sb.toString();
    }

    public static String getExtensionFromUrl(String url) {
        if (url == null) {
            return null;
        }

        return url.substring(url.lastIndexOf("."));
    }

    public static void showToast(Context context, String input) {
        Toast.makeText(context, input, Toast.LENGTH_SHORT).show();
    }

//
//    public String getAddress(final Context context, final double latitude, final double longitude) {
//        retrofit = new Retrofit.Builder()
//                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiInterface = retrofit.create(ApiInterface.class);
//        Call<ResponseBody> call = apiInterface.getResponse(latitude + "," + longitude,
//                context.getResources().getString(R.string.google_api_key));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.e("sUCESS", "SUCESS" + response.body());
//                if (response.body() != null) {
//                    try {
//                        String bodyString = new String(response.body().bytes());
//                        Log.e("sUCESS", "bodyString" + bodyString);
//                        JSONObject jsonObj = new JSONObject(bodyString);
//                        JSONArray jsonArray = jsonObj.optJSONArray("results");
//                        if (jsonArray.length() > 0) {
//                            address = jsonArray.optJSONObject(0).optString("formatted_address");
//                            Log.v("Formatted Address", "" + GlobalData.addressHeader);
//                        } else {
//                            address = "" + latitude + "" + longitude;
//
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    address = "" + latitude + "" + longitude;
//                }
//                //BroadCast Listner
//                Intent intent = new Intent("location");
//                // You can also include some extra data.
//                intent.putExtra("message", "This is my message!");
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("onFailure", "onFailure" + call.request().url());
//                address = "" + latitude + "" + longitude;
//
//            }
//        });
//        return address;
//
//    }


}
