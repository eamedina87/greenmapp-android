package ec.medinamobile.greenmapp;

/**
 * Created by Supertel on 18/5/16.
 */
public class Estadistica {

    private String ciudad;
    private int arboles;
    private int patrimoniales;

    public Estadistica(){
        setCiudad("");
        setArboles(-1);
        setPatrimoniales(-1);
    }

    public Estadistica(String ciudad, int arboles, int patrimoniales){
        setCiudad(ciudad);
        setArboles(arboles);
        setPatrimoniales(patrimoniales);
    }
    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getArboles() {
        return arboles;
    }

    public void setArboles(int arboles) {
        this.arboles = arboles;
    }

    public int getPatrimoniales() {
        return patrimoniales;
    }

    public void setPatrimoniales(int patrimoniales) {
        this.patrimoniales = patrimoniales;
    }
}
