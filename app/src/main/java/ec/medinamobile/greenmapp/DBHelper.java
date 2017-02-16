package ec.medinamobile.greenmapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Supertel on 10/5/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String dbName = "yachay.db";
    private final static int dbVersion = 1;
    private SQLiteDatabase mDatabase = null;
    private Context context = null;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public DBHelper(Context ctx) throws SQLiteException{
        super(ctx, dbName, null, dbVersion);
        context = ctx;
        mDatabase = getReadableDatabase();
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL("CREATE TABLE arbol (\n" +
                    "  idarbol integer NOT NULL,\n" +
                    "  imagen varchar(200) NOT NULL,\n" +
                    "  latitud varchar(45) NOT NULL,\n" +
                    "  longitud varchar(45) NOT NULL,\n" +
                    "  provincia varchar(45) NOT NULL,\n" +
                    "  ciudad varchar(45) NOT NULL,\n" +
                    "  sector varchar(45) ,\n" +
                    "  direccion varchar(45) ,\n" +
                    "  fecha long NOT NULL,\n" +
                    "  patrimonial int,\n" +
                    "  altura double ,\n" +
                    "  edad double ,\n" +
                    "  tipo varchar(45) ,\n" +
                    "  descripcion ,\n" +
                    "  nombre ,\n" +
                    "  estado ,\n" +
                    "  nombre_cientifico varchar(200) ,\n" +
                    "  version int(11) NOT NULL,\n" +
                    "  imagen_path varchar(200) ,\n" +
                    "  PRIMARY KEY (idarbol))");
            sqLiteDatabase.execSQL("CREATE TABLE usuario (\n" +
                    "  idusuario integer NOT NULL,\n" +
                    "  nombre varchar(45),\n" +
                    "  apellido varchar(45),\n" +
                    "  cedula varchar(10) ,\n" +
                    "  correo varchar(100) NOT NULL,\n" +
                    "  telefono varchar(45) ,\n" +
                    "  PRIMARY KEY (idusuario))");
            sqLiteDatabase.execSQL("CREATE TABLE usuario_arbol (\n" +
                    "  idusuario_arbol integer NOT NULL,\n" +
                    "  idusuario int(11) NOT NULL,\n" +
                    "  idarbol int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (idusuario_arbol))");

            SharedPreferences settings = context.getSharedPreferences("SETTINGS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dbcreated", true);
            editor.commit();

        } catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertUsuario(Usuario user) throws SQLiteException{
        mDatabase = getWritableDatabase();
        return mDatabase.insert("usuario",null, user.getContentValues());
    }

    public Usuario getUsuario() throws SQLiteException{
        mDatabase = getReadableDatabase();
        Usuario user = new Usuario();
        Cursor c = mDatabase.rawQuery("SELECT * FROM usuario",null);
        if (c.getCount()>0){
            c.moveToFirst();
            try {
                user = new Usuario (c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        return user;

    }

    public long insertArbol(Arbol arbol) throws SQLiteException{
        mDatabase = getWritableDatabase();
        return mDatabase.insert("arbol",null, arbol.getContentValues());
    }

    public ArrayList<Arbol> getAllArboles(){
        mDatabase = getReadableDatabase();
        ArrayList<Arbol> arboles = new ArrayList<Arbol>();
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol", null);
        if (c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                Arbol arbol = null;
                try {
                    arbol = new Arbol (c);
                    arboles.add(arbol);
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();
        return arboles;
    }

    public ArrayList<Arbol> getAllArbolesForProvincia(String provincia){
        mDatabase = getReadableDatabase();
        ArrayList<Arbol> arboles = new ArrayList<Arbol>();
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol WHERE provincia LIKE ?", new String[]{provincia});
        if (c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                Arbol arbol = null;
                try {
                    arbol = new Arbol (c);
                    arboles.add(arbol);
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();
        return arboles;
    }

    public ArrayList<Arbol> getAllArbolesForCiudad(String ciudad){
        mDatabase = getReadableDatabase();
        ArrayList<Arbol> arboles = new ArrayList<Arbol>();
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol WHERE ciudad LIKE ?", new String[]{ciudad.toUpperCase()});
        if (c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                Arbol arbol = null;
                try {
                    arbol = new Arbol (c);
                    arboles.add(arbol);
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();
        return arboles;
    }

    public String[] getAllProvincias() {
        String[] result = null;
        mDatabase = getReadableDatabase();
        Cursor c = mDatabase.rawQuery("SELECT DISTINCT(provincia) COLLATE NOCASE FROM arbol", null);
        if (c.getCount()>0){
            String[] provincias = new String[c.getCount()];
            c.moveToFirst();
            while (!c.isAfterLast()){
                try {
                    provincias[c.getPosition()] = c.getString(0);
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result = provincias;
        }
        c.close();
        return result;
    }

    public String[] getAllCiudadesForProvincia(String provincia) {
        String[] result = null;
        mDatabase = getReadableDatabase();
        Cursor c = mDatabase.rawQuery("SELECT DISTINCT(ciudad) COLLATE NOCASE FROM arbol WHERE provincia like ? COLLATE NOCASE", new String[]{provincia});
        if (c.getCount()>0){
            String[] ciudades = new String[c.getCount()];
            c.moveToFirst();
            while (!c.isAfterLast()){
                try {
                    ciudades[c.getPosition()] = c.getString(0);
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result = ciudades;
        }
        c.close();
        return result;
    }


    public int getEstadisticasForCiudad(String provincia, String ciudad){
        mDatabase = getReadableDatabase();
        int result = -1;
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol WHERE provincia like ? and ciudad like ? COLLATE NOCASE", new String[]{provincia, ciudad});
        result = c.getCount();
        c.close();
        return result;
    }

    public int getPatrimonialesForCiudad(String provincia, String ciudad){
        mDatabase = getReadableDatabase();
        int result = -1;
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol WHERE patrimonial=1 and provincia like ? and ciudad like ? COLLATE NOCASE", new String[]{provincia, ciudad});
        result = c.getCount();
        c.close();
        return result;
    }

    public ArrayList<Estadistica> getEstadisticasForProvincia(String provincia){
        ArrayList<Estadistica> estadisticas = null;
        String [] ciudades = getAllCiudadesForProvincia(provincia);
        if (ciudades!=null && ciudades.length>0){
            estadisticas = new ArrayList<>();
            for (String ciudad:ciudades){
                int estadistica = getEstadisticasForCiudad(provincia, ciudad);
                int patrimoniales = getPatrimonialesForCiudad(provincia, ciudad);
                if (estadistica>0){
                    estadisticas.add(new Estadistica(ciudad, estadistica, patrimoniales));
                }
            }
        }
        return estadisticas;
    }

    public Arbol getArbolById(Long idarbol) {
        mDatabase = getReadableDatabase();
        Arbol a = new Arbol();
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol WHERE idarbol=?", new String[]{String.valueOf(idarbol)});
        if (c.getCount()>0){
            c.moveToFirst();
            try {
                a = new Arbol (c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();
        return a;
    }

    public ArrayList<Arbol> getAllArbolesForUsuarioId(int idusuario) {
        mDatabase = getReadableDatabase();
        ArrayList<Arbol> arboles = new ArrayList<Arbol>();
        Cursor c = mDatabase.rawQuery("SELECT * FROM arbol_usuario WHERE idusuario = ?", new String[]{String.valueOf(idusuario)});
        if (c.getCount()>0){
            c.moveToFirst();
            while (!c.isAfterLast()){
                //idusuario_arbol idusuario idarbol
                try {
                    Cursor c1 = mDatabase.rawQuery("SELECT * FROM arbol WHERE idarbol = ?", new String[]{String.valueOf(c.getLong(2))});
                    if (c1.getCount()>0){
                        c1.moveToFirst();
                        arboles.add(new Arbol (c1));
                    }
                    c1.close();
                    c.moveToNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();
        return arboles;
    }

    public long insertArbolUsuario(long idarbol, int idusuario){
        mDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idarbol",idarbol);
        values.put("idusuario", idusuario);
        return mDatabase.insert("usuario_arbol",null, values);
    }

    public ArrayList<Arbol> getArbolesCercanos(String provincia, String ciudad){
        ArrayList<Arbol> result = null;
        if (ciudad!=null && !(ciudad.equals("") || ciudad.equals("default"))){
            result = getAllArbolesForCiudad(ciudad);
        } else if (provincia!=null && !(provincia.equals("") || provincia.equals("default"))){
            result = getAllArbolesForProvincia(provincia);
        }
        return result;
    }

    public int updateArbol(Arbol arbol) {
        mDatabase = getWritableDatabase();
        return mDatabase.update("arbol", arbol.getContentValues(), "idarbol=?", new String[]{String.valueOf(arbol.getIdarbol())});
    }
}
