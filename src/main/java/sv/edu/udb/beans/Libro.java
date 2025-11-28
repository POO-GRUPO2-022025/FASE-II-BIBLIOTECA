package sv.edu.udb.beans;

import java.util.ArrayList;
import java.util.List;

public class Libro extends Material {
    
    private String editorial;
    private int idEditorial;
    private String isbn;
    private List<Integer> idsAutores;

    public Libro() {
        this.idsAutores = new ArrayList<>();
    }

    public Libro(int idMaterial, TipoMaterial tipoMaterial, String titulo, String ubicacion,
                 int cantidadTotal, int cantidadDisponible,
                 int cantidadPrestada, int cantidadDaniada, int idEditorial, String isbn) {
        super(idMaterial, tipoMaterial, titulo, ubicacion, cantidadTotal, cantidadDisponible, cantidadPrestada, cantidadDaniada);
        this.idEditorial = idEditorial;
        this.isbn = isbn;
        this.idsAutores = new ArrayList<>();
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getIdEditorial() {
        return idEditorial;
    }

    public void setIdEditorial(int idEditorial) {
        this.idEditorial = idEditorial;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<Integer> getIdsAutores() {
        return idsAutores;
    }

    public void setIdsAutores(List<Integer> idsAutores) {
        this.idsAutores = idsAutores;
    }

    public void addIdAutor(int idAutor) {
        if (this.idsAutores == null) {
            this.idsAutores = new ArrayList<>();
        }
        this.idsAutores.add(idAutor);
    }
}
