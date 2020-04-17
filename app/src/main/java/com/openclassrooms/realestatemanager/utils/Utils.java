package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;


/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {


    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars prix en dollars
     * @return prix en euro
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.90);
    }

    /**
     * Conversion d'un prix d'un bien immobilier (Euros vers Dollars)
     * @param euros prix en euros
     * @return prix en dollar
     */
    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros * 1.12);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(LocalDate date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(dateTimeFormatter);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context The context
     * @return true if Connected, false if not connected
     */
    public static Boolean isInternetAvailable(Context context){
        boolean isConnected;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnected();
        return isConnected;
    }
}
