package ec.medinamobile.greenmapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Supertel on 10/5/16.
 */
public class BaseFragment extends Fragment{

    protected boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null) {
            return activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /*protected void showMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
    }*/

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "GREENMAPP_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    protected String getUserJson(){
        String out = null;
        try{
            JSONObject o = new JSONObject();
            o.put("usuario", new JSONObject().
                    put("idusuario", getCurrentUsuarioId())
                    .put("nombre",getCurrentNombre())
                    .put("correo", getCurrentEmail())
                    .put("telefono", getCurrentTelefono())
                    .put("cedula", getCurrentCedula()));
            out = o.toString();
        } catch (Exception e){


        }
        return out;
    }

    protected long getLastUpdateDate()
    {
        return getLongPref("lastUpdate");
    }

    protected void setLastupdateDate(long value)
    {
        setLongPref("lastUpdate", value);
    }


    protected int getCurrentUsuarioId()
    {
        return getIntPref("usuarioId");
    }

    protected void setCurrentUsuarioId(int value)
    {
        setIntPref("usuarioId", value);
    }



    protected String getCurrentTelefono()
    {
        return getStringPref("telefono");
    }

    protected void setCurrentTelefono(String value)
    {
        setStringPref("telefono", value);
    }

    protected String getCurrentCedula()
    {
        return getStringPref("cedula");
    }

    protected void setCurrentCedula(String value)
    {
        setStringPref("cedula", value);
    }


    protected boolean isDBCreated(){
        return getBooleanPref("dbcreated");
    }

    protected void setDBCreated(){
        setBooleanPref("dbcreated", true);
    }

    protected String getCurrentEmail()
    {
        return getStringPref("email");
    }

    protected void setCurrentEmail(String value)
    {
        setStringPref("email", value);
    }

    protected String getCurrentPictureUri()
    {
        return getStringPref("picture");
    }

    protected void setCurrentPictureUri(String value)
    {
        setStringPref("picture", value);
    }

    protected String getCurrentNombre()
    {
        return getStringPref("nombre");
    }

    protected void setCurrentNombre(String value)
    {
        setStringPref("nombre", value);
    }

    protected String getCurrentToken()
    {
        return getStringPref("nombre");
    }

    protected void setCurrentToken(String value)
    {
        setStringPref("nombre", value);
    }

    protected void setIntPref(String name, int value) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    protected int getIntPref(String name) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
        return settings.getInt(name, 0);
    }

    protected void setLongPref(String name, long value) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(name, value);
        editor.commit();
    }

    protected long getLongPref(String name) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
        return settings.getLong(name, -1);
    }

    protected void setStringPref(String name, String value) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    protected String getStringPref(String name) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
        return settings.getString(name,"default");
    }

    protected void setBooleanPref(String name, boolean value) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    protected boolean getBooleanPref(String name) {
        SharedPreferences settings = getActivity().getSharedPreferences("SETTINGS", 0);
        return settings.getBoolean(name,false);
    }
}
