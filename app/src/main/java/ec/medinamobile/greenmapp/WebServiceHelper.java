package ec.medinamobile.greenmapp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Supertel on 11/5/16.
 */
public class WebServiceHelper extends BaseActivity{

    private static final int CONNECTION_TIMEOUT = 20;


    //private static final String WS_SERVER_URL_MI_AWS = "http://54.187.29.10:8080/YachayGreenWS/ArbolesWS";
    private static final String WS_SERVER_URL_MAE = "http://greenmapp.ambiente.gob.ec:8080/GreenMappWS/ArbolesWS?wsdl";
    private static final String WS_SERVER_URL_LOCAL = "http://192.168.1.102:8080/YachayGreenWS/ArbolesWS";
    private static final String WS_SERVER_URL=WS_SERVER_URL_MAE;
    //private static final String WS_NAMESPACE = "http://integra.medval.ec/";
    private static final String WS_NAMESPACE = "http://yachaygreenws.medinamobile.ec/";
    //public static final String WS_SOAP_ACTION_GETREQUEST = "http://integra.medval.ec/SirenaVecinalWS/";
    public static final String WS_SOAP_ACTION_GETREQUEST = "http://yachaygreenws.medinamobile.ec/GreenMappWS/ArbolesWS/";


    private static final String WS_METHOD_NAME_GETARBOLES = "getArboles";
    private static final String WS_METHOD_NAME_GETARBOLESSINCE = "getArbolesSinceID";
    private static final String WS_METHOD_NAME_GETPADRINOS = "getPadrinosForArbol";
    private static final String WS_METHOD_NAME_INSERTARBOL = "insertArbol";
    private static final String WS_METHOD_NAME_GETARBOLESAPADRINADOS = "getArbolesForUserEmail";
    private static final String WS_METHOD_NAME_GETIMAGEN = "getImagenForArbolId";
    private static final String WS_PARAMETER_REQUEST_PROVINCIA = "provincia";
    private static final String WS_PARAMETER_REQUEST_CIUDAD = "ciudad";
    private static final String WS_PARAMETER_REQUEST_IDARBOL = "idarbol";
    private static final String WS_PARAMETER_REQUEST_ULTIMOID = "ultimoid";
    private static final String WS_PARAMETER_REQUEST_ARBOL = "arbol";
    private static final String WS_PARAMETER_REQUEST_USUARIO = "usuario";
    private static final String WS_PARAMETER_REQUEST_IMAGEN = "imagen";


    public static final String DIRECTORY_IMAGES = "http://greenmapp.ambiente.gob.ec:8080/arboles/images/";

    //Obtiene todos los arboles, o por provincia o por ciudad
    public static SoapSerializationEnvelope getSoapEnvelopeArboles(String provincia, String ciudad){

        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_GETARBOLES);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_PROVINCIA, provincia);
        request.addProperty(WS_PARAMETER_REQUEST_CIUDAD, ciudad);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    //Obtiene todos los arboles, o por provincia o por ciudad
    public static SoapSerializationEnvelope getSoapEnvelopeArbolesSinceID(long ultimoid){

        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_GETARBOLESSINCE);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_ULTIMOID, ultimoid);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    //Inserta un arbol y convierte al usuario en padrino
    public static SoapSerializationEnvelope getSoapEnvelopeInsertArbol(String arbol , String usuario, String imagen){

        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_INSERTARBOL);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_ARBOL, arbol);
        request.addProperty(WS_PARAMETER_REQUEST_USUARIO, usuario);
        request.addProperty(WS_PARAMETER_REQUEST_IMAGEN, imagen);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    //Obtiene los padrinos de un arbol con identificador idarbol
    public static SoapSerializationEnvelope getSoapEnvelopePadrinos(long idarbol){

        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_GETPADRINOS);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_IDARBOL, idarbol);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    public static SoapSerializationEnvelope getSoapEnvelopeArbolesForUser(String usuario) {
        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_GETARBOLESAPADRINADOS);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_USUARIO, usuario);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }

    public static SoapSerializationEnvelope getSoapEnvelopeImagen(long arbolId) {
        SoapObject request = new SoapObject(WS_NAMESPACE, WS_METHOD_NAME_GETIMAGEN);

        //Parametros enviados al Web Service
        request.addProperty(WS_PARAMETER_REQUEST_IDARBOL, arbolId);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        return envelope;
    }


    public static HttpTransportSE getHttpTransport(int timeOut) {
        // TODO Auto-generated method stub
        return timeOut==-1?new HttpTransportSE(WS_SERVER_URL):new HttpTransportSE(WS_SERVER_URL, timeOut);
    }

}
