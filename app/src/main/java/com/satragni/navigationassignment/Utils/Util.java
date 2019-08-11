package com.satragni.navigationassignment.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.satragni.navigationassignment.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dell on 21/12/17.
 */

public class Util {
    public static boolean cameraFlag = false;
    public static boolean contactFlag = false;
    public  static boolean isJourneyStarted=false;
    public static boolean isAutocancel = false;
    private static PopupWindow popupWindow;


    public static boolean checkNetStatus(final Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()== null){
            View view=activity.findViewById(android.R.id.content);
//            final Snackbar snackbar = Snackbar.make(view,"No or poor internet connectivity", Snackbar.LENGTH_LONG);
//            View vw = snackbar.getView();
//            TextView tview = (TextView)vw.findViewById(android.support.design.R.id.snackbar_text);
//            tview.setTextColor(Color.parseColor("#fcc71a"));
            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("Info");
            dialog.setMessage("Network disconnected. Please check your Internet connection.");
            dialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    checkNetStatus(activity);
                }
            });
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
            return false;
        }
        return  true;
    }
    public static boolean checkNetStatusResult(final Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()== null){
            return false;
        }
        return  true;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }




    public static String getAddressFromLocation(Context context, Location position) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String locality = "";

        try {
            List<Address> listAddresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);
            if(null!=listAddresses && listAddresses.size()>0){
                //locality = listAddresses.get(0).getAddressLine(1);
                locality = listAddresses.get(0).getAddressLine(0) + ", " + listAddresses.get(0).getAddressLine(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locality;
    }
    public static String getDateTime(String mongoDate){
        String outputTime="";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

            Date date = format.parse(mongoDate);
            int offSet= Calendar.getInstance().getTimeZone().getOffset(date.getTime());
            long dt=date.getTime();

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM d',' h:mm a");

            outputTime=simpleDateFormat.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputTime;
    }

    public static String getDateTimeEpochAt(Long epochTime){
        String outputTime="";
        try {
            Date date = new Date(epochTime);
            int offSet= Calendar.getInstance().getTimeZone().getOffset(date.getTime());
            long dt=date.getTime();

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM, yyyy - EE '@' h:mm a");

            outputTime=simpleDateFormat.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputTime;
    }
    public static String getAmPmEpoch(Long epochTime){
        String outputTime="";
        try {
            Date date = new Date(epochTime);
            int offSet= Calendar.getInstance().getTimeZone().getOffset(date.getTime());
            long dt=date.getTime();

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mma");

            outputTime=simpleDateFormat.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputTime;
    }

    public static String hhmm(int min){
        if(min>0){
            String time="";
            if(min>59){
                time=(min/60)+"hr "+(min%60)+" min";
            }else{
                time=min+" min";
            }
            return time;
        }
        return "N/A";
    }

    public static String getCurrentDate(){
        long ms= System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        return simpleDateFormat.format(calendar.getTime());
    }
    public static String getDummyImageText(String text){

        String word = null;
        try {

            String split[] = text.trim().split(" ");
            if (split != null) {
                int len = split.length;
                if (len > 1) {
                    word = split[0].substring(0, 1).toUpperCase() + split[len - 1].substring(0, 1).toUpperCase();
                } else if (len == 1) {
                    word = split[0].substring(0, 1).toUpperCase();
                }
            } else {
                word = "!";
            }
        }catch (Exception e){
            e.printStackTrace();
            word="!";
        }
        return word;
    }
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static int convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }
    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            if (inputMethodManager.isAcceptingText())
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyBoardFragment(Activity activity,View view) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void showKeyBoardFragment(Activity activity) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void subscribeToTopics(String token){

    }

    public static LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    public static String getLastnCharacters(String inputString,
                                            int subStringLength){
        int length = inputString.length();
        if(length <= subStringLength){
            return inputString;
        }
        int startIndex = length-subStringLength;
        return inputString.substring(startIndex);
    }


    public static void ClosePOPUP()
    {
        if(popupWindow!=null){
            popupWindow.dismiss();
            popupWindow=null;
        }

    }
}
