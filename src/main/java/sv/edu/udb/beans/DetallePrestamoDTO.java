package sv.edu.udb.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Bean de transporte para mostrar el detalle completo de un préstamo
 * Incluye información del préstamo, usuario, material y cálculos de mora
 */
public class DetallePrestamoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Información del préstamo
    private Prestamo prestamo;
    
    // Información del usuario
    private Usuario usuario;
    
    // Información del material
    private Material material;
    
    // Información de la tarifa de mora
    private Mora tarifa;
    
    // Cálculos de retraso y mora
    private int diasRetraso;
    private BigDecimal moraCalculada;
    private BigDecimal moraOriginal; // Mora calculada originalmente (antes de abonos)
    private boolean tieneRetraso;
    
    // Permisos de acciones
    private boolean puedeAprobar;
    private boolean puedeDenegar;
    private boolean puedeDevolver;
    private boolean puedeAbonarMora;
    
    // Información adicional
    private int prestamosActivosUsuario;
    private int limiteMaximoUsuario;
    private int diasPrestamoDefault;
    
    // Constructor vacío
    public DetallePrestamoDTO() {
        this.diasRetraso = 0;
        this.moraCalculada = BigDecimal.ZERO;
        this.moraOriginal = BigDecimal.ZERO;
        this.tieneRetraso = false;
        this.puedeAprobar = false;
        this.puedeDenegar = false;
        this.puedeDevolver = false;
        this.puedeAbonarMora = false;
    }

    // Getters y Setters
    
    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Mora getTarifa() {
        return tarifa;
    }

    public void setTarifa(Mora tarifa) {
        this.tarifa = tarifa;
    }

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public void setDiasRetraso(int diasRetraso) {
        this.diasRetraso = diasRetraso;
    }

    public BigDecimal getMoraCalculada() {
        return moraCalculada;
    }

    public void setMoraCalculada(BigDecimal moraCalculada) {
        this.moraCalculada = moraCalculada;
    }

    public BigDecimal getMoraOriginal() {
        return moraOriginal;
    }

    public void setMoraOriginal(BigDecimal moraOriginal) {
        this.moraOriginal = moraOriginal;
    }

    public boolean isTieneRetraso() {
        return tieneRetraso;
    }

    public void setTieneRetraso(boolean tieneRetraso) {
        this.tieneRetraso = tieneRetraso;
    }

    public boolean isPuedeAprobar() {
        return puedeAprobar;
    }

    public void setPuedeAprobar(boolean puedeAprobar) {
        this.puedeAprobar = puedeAprobar;
    }

    public boolean isPuedeDenegar() {
        return puedeDenegar;
    }

    public void setPuedeDenegar(boolean puedeDenegar) {
        this.puedeDenegar = puedeDenegar;
    }

    public boolean isPuedeDevolver() {
        return puedeDevolver;
    }

    public void setPuedeDevolver(boolean puedeDevolver) {
        this.puedeDevolver = puedeDevolver;
    }

    public boolean isPuedeAbonarMora() {
        return puedeAbonarMora;
    }

    public void setPuedeAbonarMora(boolean puedeAbonarMora) {
        this.puedeAbonarMora = puedeAbonarMora;
    }

    public int getPrestamosActivosUsuario() {
        return prestamosActivosUsuario;
    }

    public void setPrestamosActivosUsuario(int prestamosActivosUsuario) {
        this.prestamosActivosUsuario = prestamosActivosUsuario;
    }

    public int getLimiteMaximoUsuario() {
        return limiteMaximoUsuario;
    }

    public void setLimiteMaximoUsuario(int limiteMaximoUsuario) {
        this.limiteMaximoUsuario = limiteMaximoUsuario;
    }

    public int getDiasPrestamoDefault() {
        return diasPrestamoDefault;
    }

    public void setDiasPrestamoDefault(int diasPrestamoDefault) {
        this.diasPrestamoDefault = diasPrestamoDefault;
    }
}
