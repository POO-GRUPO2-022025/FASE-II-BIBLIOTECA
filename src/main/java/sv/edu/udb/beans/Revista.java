package sv.edu.udb.beans;

import java.time.LocalDate;

public class Revista extends Material {
    
    private String volumen;
    private String numero;
    private LocalDate fechaPublicacion;

    public Revista() {}

    public Revista(int idMaterial, TipoMaterial tipoMaterial, String titulo, String ubicacion,
                   int cantidadTotal, int cantidadDisponible,
                   int cantidadPrestada, int cantidadDaniada,
                   String volumen, String numero, LocalDate fechaPublicacion) {
        super(idMaterial, tipoMaterial, titulo, ubicacion, cantidadTotal, cantidadDisponible, cantidadPrestada, cantidadDaniada);
        this.volumen = volumen;
        this.numero = numero;
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getVolumen() {
        return volumen;
    }

    public void setVolumen(String volumen) {
        this.volumen = volumen;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
