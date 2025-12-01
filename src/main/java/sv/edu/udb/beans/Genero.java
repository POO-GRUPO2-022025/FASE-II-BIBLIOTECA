
package sv.edu.udb.beans;
import java.io.Serializable;

public class Genero implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idGenero;
    private String nombre;
    private String descripcion;

    public Genero() {}

   public Genero(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Genero(int idGenero, String nombre, String descripcion) {
        this.idGenero = idGenero;
        this.nombre = nombre;
       this.descripcion = descripcion;
    }

    public int getIdGenero() {
        return idGenero;
    }

  public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

  public String getDescripcion() { 
        return descripcion;
    }

  public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion;
    }
     }



