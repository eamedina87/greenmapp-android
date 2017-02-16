package ec.medinamobile.greenmapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Supertel on 10/5/16.
 */
public class Arbol implements Parcelable{


    public static final int ARBOL_COMPLETE = 0;
    public static final int ARBOL_INCOMPLETE = -1;
    public static final int ARBOL_MISSING_TIPO = -2;
    public static final int ARBOL_MISSING_DESCRIPCION = -3;
    public static final int ARBOL_MISSING_LATITUD = -4;
    public static final int ARBOL_MISSING_LONGITUD = -5;
    public static final int ARBOL_MISSING_PROVINCIA = -6;
    public static final int ARBOL_MISSING_CIUDAD = -7;
    public static final int ARBOL_MISSING_IMAGE = -8;
    private long idarbol;
    private String imagen;
    private double latitud;
    private double longitud;
    private String provincia;
    private String ciudad;
    private String sector;
    private String direccion;
    private long fecha;
    private int patrimonial;
    private double altura;
    private double edad;
    private String tipo;
    private String descripcion;
    private String nombre;
    private String estado;
    private String nombre_cientifico;
    private int version;
    private float distancia;
    private String imagen_path;

    public Arbol(Cursor c){
        setIdarbol(c.getLong(0));
        setImagen(c.getString(1));
        setLatitud(c.getDouble(2));
        setLongitud(c.getDouble(3));
        setProvincia(c.getString(4));
        setCiudad(c.getString(5));
        setSector(c.getString(6));
        setDireccion(c.getString(7));
        setFecha(c.getLong(8));
        setPatrimonial(c.getInt(9));
        setAltura(c.getDouble(10));
        setEdad(c.getDouble(11));
        setTipo(c.getString(12));
        setDescripcion(c.getString(13));
        setNombre(c.getString(14));
        setEstado(c.getString(15));
        setNombre_cientifico(c.getString(16));
        setVersion(c.getInt(17));
        setImagen_path(c.getString(18));
    }

    public Arbol(){
        setIdarbol(-1);
        setImagen("");
        setLatitud(900);
        setLongitud(900);
        setProvincia("");
        setCiudad("");
        setSector("");
        setDireccion("");
        setFecha(-1);
        setPatrimonial(0);
        setAltura(-1);
        setEdad(-1);
        setTipo("");
        setDescripcion("");
        setNombre("");
        setEstado("");
        setNombre_cientifico("");
        setVersion(-1);
        setDistancia(-1);
        setImagen_path("");
    }

    public Arbol(JSONObject a) {
        try{
            JSONObject ar = a.getJSONObject("arbol");
            //System.out.println("ARBOL:"+a.toString());
            //System.out.println("ARBOL2:"+ar.toString());
            setIdarbol(ar.getLong("idarbol"));
            setTipo(ar.getString("tipo"));
            setDescripcion(ar.getString("descripcion"));
            setPatrimonial(ar.getInt("patrimonial"));
            setFecha(ar.getLong("fecha_envio"));
            setVersion(ar.getInt("version"));
            setImagen(ar.getString("imagen"));

            JSONObject u = ar.getJSONObject("ubicacion");
            setProvincia(u.getString("provincia"));
            setCiudad(u.getString("ciudad"));
            setSector(u.getString("sector"));
            setDireccion(u.getString("direccion"));
            setLatitud(u.getDouble("latitud"));
            setLongitud(u.getDouble("longitud"));
        } catch (Exception e){

        }

    }

    public String toJsonString(){
        String out = null;
        try{
            JSONObject o = new JSONObject();
            o.put("arbol", new JSONObject().
                    put("idarbol", getIdarbol())
                    .put("tipo", getTipo())
                    .put("descripcion", getDescripcion())
                    .put("patrimonial", getPatrimonial())
                    .put("fecha_envio", getFecha())
                    .put("version", getVersion())
                    .put("imagen", getImagen())
                    .put("ubicacion", new JSONObject().
                                    put("provincia", getProvincia())
                                    .put("ciudad", getCiudad())
                                    .put("sector", getSector())
                                    .put("direccion", getDireccion())
                                    .put("latitud", getLatitud())
                                    .put("longitud", getLongitud())
                    ));
            out = o.toString();

        } catch (Exception e){


        }
        return out;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(getIdarbol());
        parcel.writeString(getImagen());
        parcel.writeDouble(getLatitud());
        parcel.writeDouble(getLongitud());
        parcel.writeString(getProvincia());
        parcel.writeString(getCiudad());
        parcel.writeString(getSector());
        parcel.writeString(getDireccion());
        parcel.writeLong(getFecha());
        parcel.writeInt(getPatrimonial());
        parcel.writeDouble(getAltura());
        parcel.writeDouble(getEdad());
        parcel.writeString(getTipo());
        parcel.writeString(getDescripcion());
        parcel.writeString(getNombre());
        parcel.writeString(getEstado());
        parcel.writeString(getNombre_cientifico());
        parcel.writeInt(getVersion());
        parcel.writeFloat(getDistancia());
        parcel.writeString(getImagen_path());
    }

    public Arbol(Parcel source) {
        setIdarbol(source.readLong());
        setImagen(source.readString());
        setLatitud(source.readDouble());
        setLongitud(source.readDouble());
        setProvincia(source.readString());
        setCiudad(source.readString());
        setSector(source.readString());
        setDireccion(source.readString());
        setFecha(source.readLong());
        setPatrimonial(source.readInt());
        setAltura(source.readDouble());
        setEdad(source.readDouble());
        setTipo(source.readString());
        setDescripcion(source.readString());
        setNombre(source.readString());
        setEstado(source.readString());
        setNombre_cientifico(source.readString());
        setVersion(source.readInt());
        setDistancia(source.readFloat());
        setImagen_path(source.readString());
    }

    public static final Parcelable.Creator<Arbol> CREATOR = new Creator<Arbol>() {

        public Arbol createFromParcel(Parcel source) {

            return new Arbol(source);
        }

        public Arbol[] newArray(int size) {

            return new Arbol[size];
        }

    };
    @Override
    public int describeContents() {
        return 0;
    }



    public long getIdarbol() {
        return idarbol;
    }

    public void setIdarbol(long idarbol) {
        this.idarbol = idarbol;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public int getPatrimonial() {
        return patrimonial;
    }

    public void setPatrimonial(int patrimonial) {
        this.patrimonial = patrimonial;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getEdad() {
        return edad;
    }

    public void setEdad(double edad) {
        this.edad = edad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre_cientifico() {
        return nombre_cientifico;
    }

    public void setNombre_cientifico(String nombre_cientifico) {
        this.nombre_cientifico = nombre_cientifico;
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("idarbol", getIdarbol());
        values.put("imagen", getImagen());
        values.put("latitud", getLatitud());
        values.put("longitud", getLongitud());
        values.put("provincia", getProvincia().toUpperCase());
        values.put("ciudad", getCiudad().toUpperCase());
        values.put("sector", getSector().toUpperCase());
        values.put("direccion", getDireccion().toUpperCase());
        values.put("fecha", getFecha());
        values.put("patrimonial", getPatrimonial());
        values.put("altura", getAltura());
        values.put("edad", getEdad());
        values.put("tipo", getTipo());
        values.put("descripcion", getDescripcion());
        values.put("nombre", getNombre());
        values.put("estado", getEstado());
        values.put("nombre_cientifico", getNombre_cientifico());
        values.put("version", getVersion());
        return values;
    }

    public int isComplete() {
        int out = ARBOL_COMPLETE;
        if (getIdarbol()!=0)
            out = ARBOL_INCOMPLETE;
        if (getTipo()== null || getTipo().equals(""))
            out = ARBOL_MISSING_TIPO;
        if (getDescripcion()==null || getDescripcion().equals(""))
            out = ARBOL_MISSING_DESCRIPCION;
        if (getLatitud()==900)
            out = ARBOL_MISSING_LATITUD;
        if (getLongitud()==900)
            out = ARBOL_MISSING_LONGITUD;
        /*
        if (getProvincia()==null || getProvincia().equals(""))
            out = ARBOL_MISSING_PROVINCIA;
        if (getCiudad()==null || getCiudad().equals(""))
            out = ARBOL_MISSING_CIUDAD;
            */
        if (getImagen().equals(""))
            out = ARBOL_MISSING_IMAGE;
        return out;
    }

    public Location getLocation() {
        Location tmp = new Location("");
        tmp.setLongitude(getLongitud());
        tmp.setLatitude(getLatitud());
        return tmp;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public String getImagen_path() {
        return imagen_path;
    }

    public void setImagen_path(String imagen_path) {
        this.imagen_path = imagen_path;
    }

    public String toString(){
        return getIdarbol()+";"+getLatitud()+";"+getLongitud();
    }
}
