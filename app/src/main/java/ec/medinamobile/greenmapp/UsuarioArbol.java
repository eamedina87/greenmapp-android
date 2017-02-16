package ec.medinamobile.greenmapp;

/**
 * Created by Supertel on 10/5/16.
 */
public class UsuarioArbol {

    private long idusuarioarbol;
    private int idusuario;
    private long idarbol;

    public UsuarioArbol(){
        setIdarbol(-1);
        setIdusuario(-1);
        setIdusuarioarbol(-1);
    }

    public long getIdusuarioarbol() {
        return idusuarioarbol;
    }

    public void setIdusuarioarbol(long idusuarioarbol) {
        this.idusuarioarbol = idusuarioarbol;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public long getIdarbol() {
        return idarbol;
    }

    public void setIdarbol(long idarbol) {
        this.idarbol = idarbol;
    }
}
