package sv.edu.udb.beans;

import java.io.Serializable;
import org.mindrot.jbcrypt.BCrypt;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TipoUsuario {
        Encargado,
        Profesor,
        Alumno
    }

    private int idUsuario;
    private String nombre;
    private String correo;
    private String passwordHash;
    private TipoUsuario tipoUsuario;

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String correo, String password, TipoUsuario tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = encriptarPass(password);
        this.tipoUsuario = tipoUsuario;
    }

    // Método para encriptar la clave
    public String encriptarPass(String passPlano) {
        return BCrypt.hashpw(passPlano, BCrypt.gensalt());
    }

    // Método para verificar el password
    public boolean verificarPass(String passPlano) {
        return BCrypt.checkpw(passPlano, this.passwordHash);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = encriptarPass(password);
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
