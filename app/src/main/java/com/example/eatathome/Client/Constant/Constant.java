package com.example.eatathome.Client.Constant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.eatathome.Client.Model.Food;
import com.example.eatathome.Client.Model.Request;
import com.example.eatathome.Client.Model.Restaurant;
import com.example.eatathome.Client.Model.User;
import com.example.eatathome.Client.Remote.APIService;
import com.example.eatathome.Client.Remote.GoogleRetrofitClient;
import com.example.eatathome.Client.Remote.IGoogleService;
import com.example.eatathome.Client.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Constant {
    public static User currentUser;
    public static Request currentRequest;
    public static Restaurant currentRestaurant;
    public static Food currentFood;
    public static final String CATEGORY_ID = "CategoryId";
    public static final String FOOD_ID = "FoodId";
    public static final String RESTAURANT_ID = "RestaurantId";
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static  String restaurantSelected = "";

    public static String currentKey;

    public static String PHONE_TEXT = "userPhone";

    public static final int PICK_IMAGE_REQUEST = 71;

    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI(){

        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static String convertCodeToStatus(String status) {

        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "Preparing Orders";
        else if(status.equals("2"))
            return "Shipping";
        else
            return "Delivered";
    }

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){

                for(int i=0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd-MM-yyyy hh:mm aaa"
                , calendar).toString());
        return date.toString();
    }


}
