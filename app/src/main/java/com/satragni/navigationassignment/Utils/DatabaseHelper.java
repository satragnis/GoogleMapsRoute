package com.satragni.navigationassignment.Utils;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dell on 21/12/17.
 */

public class DatabaseHelper {

    //private static DatabaseHelper dbh = new DatabaseHelper();
    static DB dbh = null;

    private  DatabaseHelper(){};

    public static DB getInstance(Context ctx)
    {
        try {
            if (dbh == null)
                dbh = DBFactory.open(ctx);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return dbh;
    }

    public static void closeInstance()
    {
        try {
            if (dbh != null)
            {
                dbh.close();
                dbh = null;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void setAccountDetails(Context context,String key,String value){
        try {
            //DB snappydb = DBFactory.open(context); //create or open an existing database using the default name
            DB snappydb = getInstance(context);
            snappydb.put(key,value);
            // get array of string
            Log.d("SNAPPYDB", "PUT-> DATA: "+"key:"+key+" value:" +value);
            //snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static void setJsonData(Context context,String key,JSONObject value){
        try {
            DB snappydb = getInstance(context);
            ; //create or open an existing database using the default name
            snappydb.put(key,value);
            // get array of string
            Log.d("SNAPPYDB", "PUT-> DATA:"+value);
            //snappydb.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static void setJsonArray(Context context,String key,JSONArray value){
        try {
            DB snappydb = getInstance(context);
            //create or open an existing database using the default name
            snappydb.put(key,value);
            // get array of string
            Log.d("SNAPPYDB", "PUT-> DATA:"+value.toString());
            //snappydb.close();

        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public static String getAccountDetails(Context context,String key){
        String data="NOTFOUND";
        try {
            DB snappydb = getInstance(context);
            //create or open an existing database using the default name
            if(snappydb.exists(key)){
                data = snappydb.get(key);
            }else {
                return "NOTFOUND";
            }
            Log.d("SNAPPYDB", "onCreate-> DATA:");
            //snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
            return "NOTFOUND";
        }
        return data;
    }

    public static String getJsonData(Context context,String key){
        String stringData="NOTFOUND";
        try {
            DB snappydb = getInstance(context); //create or open an existing database using the default name
            boolean isKeyExist = snappydb.exists(key);
            if(isKeyExist) {
                stringData = snappydb.get(key);
                Log.d("SNAPPYDB", "onCreate-> DATA:");
            }else{
                return "NOTFOUND";
            }
            //snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
            return "NOTFOUND";
        }
        return stringData;
    }

    public static String getJsonArrayData(Context context,String key){
        String stringData="NOTFOUND";
        try {
            DB snappydb = getInstance(context); //create or open an existing database using the default name
            boolean isKeyExist = snappydb.exists(key);
            if(isKeyExist) {
                stringData = snappydb.get(key);
                Log.d("SNAPPYDB", "onCreate-> DATA:");
            }else{
                return "NOTFOUND";
            }
            //snappydb.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
            return "NOTFOUND";
        }
        return stringData;
    }

    public static void tearDown() {
        try {
            if(dbh!=null){
                dbh.destroy();
                dbh.close();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        dbh = null;
    }





}
