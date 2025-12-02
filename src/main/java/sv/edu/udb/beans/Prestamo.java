package sv.edu.udb.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Estado {
        Pendiente,
        En_Curso,
        Devuelto,
        Denegado
    }

    private int idPrestamo;
    private int idUsuario;
    private int idMaterial;
    private int idMora;
    private Date fechaPrestamo;
    private Date fechaEstimada;
    private Date fechaDevolucion;
    private BigDecimal moraTotal;
    private Estado estado;
    
    // Campos adicionales para mostrar información relacionada (no se mapean a BD)
    private String nombreUsuario;
    private String tituloMaterial;
    private String tipoMaterial;

    public Prestamo() {}

    public Prestamo(int idPrestamo, int idUsuario, int idMaterial,
                    int idMora, Date fechaPrestamo, Date fechaEstimada, Date fechaDevolucion,
                    BigDecimal moraTotal, Estado estado) {
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.idMora = idMora;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEstimada = fechaEstimada;
        this.fechaDevolucion = fechaDevolucion;
        this.moraTotal = moraTotal;
        this.estado = estado;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public int getIdMora() {
        return idMora;
    }

    public void setIdMora(int idMora) {
        this.idMora = idMora;
    }

    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public BigDecimal getMoraTotal() {
        return moraTotal;
    }

    public void setMoraTotal(BigDecimal moraTotal) {
        this.moraTotal = moraTotal;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Date getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(Date fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
    }

    public void setTituloMaterial(String tituloMaterial) {
        this.tituloMaterial = tituloMaterial;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }
}
