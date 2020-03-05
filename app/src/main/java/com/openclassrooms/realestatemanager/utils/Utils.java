package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {


    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.90);
    }

    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros * 1.12);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context The context
     * @return true if Connected, false if not connected
     */
    public static Boolean isInternetAvailable(Context context){
//        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//        return wifi.isWifiEnabled();
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
        }else {
            isConnected = false;
        }
        return isConnected;
    }
}
