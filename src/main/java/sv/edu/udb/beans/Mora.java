package sv.edu.udb.beans;

import java.io.Serializable;
import java.math.BigDecimal;

public class Mora implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int idMora;
    private int anioAplicable;
    private Usuario.TipoUsuario tipoUsuario;
    private BigDecimal tarifaDiaria;

    public Mora() {}

    public Mora(int idMora, int anioAplicable, Usuario.TipoUsuario tipoUsuario,
                BigDecimal tarifaDiaria) {
        this.idMora = idMora;
        this.anioAplicable = anioAplicable;
        this.tipoUsuario = tipoUsuario;
        this.tarifaDiaria = tarifaDiaria;
    }

    public int getIdMora() {
        return idMora;
    }

    public void setIdMora(int idMora) {
        this.idMora = idMora;
    }

    public int getAnioAplicable() {
        return anioAplicable;
    }

    public void setAnioAplicable(int anioAplicable) {
        this.anioAplicable = anioAplicable;
    }

    public Usuario.TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Usuario.TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public BigDecimal getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void setTarifaDiaria(BigDecimal tarifaDiaria) {
        this.tarifaDiaria = tarifaDiaria;
    }
}
