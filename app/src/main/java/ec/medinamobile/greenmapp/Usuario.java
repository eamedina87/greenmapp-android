package ec.medinamobile.greenmapp;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Supertel on 10/5/16.
 */
public class Usuario {

    private int idusuario;
    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;
    private String telefono;

    public Usuario(Cursor c){
        setIdusuario(c.getInt(0));
        setNombre(c.getString(1));
        setApellido(c.getString(2));
        setCedula(c.getString(3));
        setCorreo(c.getString(4));
        setTelefono(c.getString(5));
    }


    public Usuario(){
        setIdusuario(-1);
        setNombre("");
        setApellido("");
        setCedula("");
        setCorreo("");
        setTelefono("");
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put("idusuario", getIdusuario());
        values.put("nombre", getNombre());
        values.put("apellido", getApellido());
        values.put("cedula", getCedula());
        values.put("correo", getCorreo());
        values.put("telefono", getTelefono());
        return values;
    }


}
