package sv.edu.udb.beans;

public class Audiovisual extends Material {
    
    private String formato;
    private int duracion;

    public Audiovisual() {}

    public Audiovisual(int idMaterial, TipoMaterial tipoMaterial, String titulo, String ubicacion,
                       int cantidadTotal, int cantidadDisponible,
                       int cantidadPrestada, int cantidadDaniada,
                       String formato, int duracion) {
        super(idMaterial, tipoMaterial, titulo, ubicacion, cantidadTotal, cantidadDisponible, cantidadPrestada, cantidadDaniada);
        this.formato = formato;
        this.duracion = duracion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}
