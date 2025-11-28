package sv.edu.udb.beans;

import java.io.Serializable;

public class Autor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idAutor;
    private String nombre;
    private String apellidos;
    private String pais;

    public Autor() {}

    public Autor(int idAutor, String nombre, String apellidos, String pais) {
        this.idAutor = idAutor;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.pais = pais;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
