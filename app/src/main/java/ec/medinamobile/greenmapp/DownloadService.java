package ec.medinamobile.greenmapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * Created by Supertel on 10/5/16.
 */
public class DownloadService extends Service {
    private getDataFromWS task;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task = new getDataFromWS();
        task.execute();
        return super.onStartCommand(intent, flags, startId);


    }


    private class getDataFromWS extends  AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                SoapSerializationEnvelope envelope = WebServiceHelper.getSoapEnvelopeArboles("all","all");
                HttpTransportSE transport = WebServiceHelper.getHttpTransport(30);
                transport.call(WebServiceHelper.WS_SOAP_ACTION_GETREQUEST, envelope);
                Object resultXML = (Object) envelope.getResponse();

                if (resultXML != null)
                    Log.i("RESPONSE-ARBOLES:",resultXML.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
