package sv.edu.udb.controllers.materiales;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.Audiovisual;
import sv.edu.udb.beans.Material.TipoMaterial;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.model.materiales.AudiovisualModel;

/**
 * Controlador específico para operaciones CRUD de Audiovisuales
 */
public class AudiovisualController {
    
    private List<String> listaErrores;
    
    public AudiovisualController(List<String> listaErrores) {
        this.listaErrores = listaErrores;
    }
    
    /**
     * Muestra el formulario para nuevo audiovisual
     */
    public void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/materiales/nuevoAudiovisual.jsp").forward(request, response);
    }
    
    /**
     * Inserta un nuevo audiovisual
     */
    public void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String formato = request.getParameter("formato");
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (formato == null || formato.trim().isEmpty()) {
            listaErrores.add("El formato es obligatorio.");
        }
        
        int cantidadTotal = 0;
        int duracion = 0;
        
        try {
            cantidadTotal = Integer.parseInt(request.getParameter("cantidadTotal"));
            if (cantidadTotal < 0) {
                listaErrores.add("La cantidad total debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La cantidad total debe ser un número válido.");
        }
        
        try {
            duracion = Integer.parseInt(request.getParameter("duracion"));
            if (duracion < 0) {
                listaErrores.add("La duración debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La duración debe ser un número válido.");
        }
        
        if (!listaErrores.isEmpty()) {
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("titulo", titulo);
            request.setAttribute("ubicacion", ubicacion);
            request.setAttribute("formato", formato);
            request.setAttribute("cantidadTotal", cantidadTotal);
            request.setAttribute("duracion", duracion);
            request.getRequestDispatcher("/materiales/nuevoAudiovisual.jsp").forward(request, response);
            return;
        }
        
        // Crear objeto Audiovisual
        Audiovisual audiovisual = new Audiovisual();
        audiovisual.setTipoMaterial(TipoMaterial.Audiovisual);
        audiovisual.setTitulo(titulo.trim());
        audiovisual.setUbicacion(ubicacion.trim());
        audiovisual.setCantidadTotal(cantidadTotal);
        audiovisual.setCantidadDisponible(cantidadTotal);
        audiovisual.setCantidadPrestada(0);
        audiovisual.setCantidadDaniada(0);
        audiovisual.setFormato(formato.trim());
        audiovisual.setDuracion(duracion);
        
        // Insertar en la base de datos
        try {
            AudiovisualModel audiovisualModel = new AudiovisualModel();
            Audiovisual audiovisualInsertado = audiovisualModel.insert(audiovisual);
            
            if (audiovisualInsertado != null) {
                request.getSession().setAttribute("exito", "Audiovisual agregado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo agregar el audiovisual");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al agregar el audiovisual: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
    
    /**
     * Obtiene un audiovisual para editar
     */
    public void obtener(HttpServletRequest request, HttpServletResponse response, int id)
            throws ServletException, IOException {
        try {
            AudiovisualModel audiovisualModel = new AudiovisualModel();
            Audiovisual audiovisual = audiovisualModel.obtenerPorId(id);
            
            if (audiovisual != null) {
                request.setAttribute("audiovisual", audiovisual);
                request.getRequestDispatcher("/materiales/editarAudiovisual.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("fracaso", "Audiovisual no encontrado");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al cargar el audiovisual: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/materiales.do");
        }
    }
    
    /**
     * Modifica un audiovisual existente
     */
    public void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String formato = request.getParameter("formato");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            request.getSession().setAttribute("fracaso", "ID del audiovisual es obligatorio");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (formato == null || formato.trim().isEmpty()) {
            listaErrores.add("El formato es obligatorio.");
        }
        
        int id = 0;
        int cantidadTotal = 0;
        int duracion = 0;
        
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            listaErrores.add("ID inválido.");
        }
        
        try {
            cantidadTotal = Integer.parseInt(request.getParameter("cantidadTotal"));
            if (cantidadTotal < 0) {
                listaErrores.add("La cantidad total debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La cantidad total debe ser un número válido.");
        }
        
        try {
            duracion = Integer.parseInt(request.getParameter("duracion"));
            if (duracion < 0) {
                listaErrores.add("La duración debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La duración debe ser un número válido.");
        }
        
        if (!listaErrores.isEmpty()) {
            request.setAttribute("listaErrores", listaErrores);
            try {
                obtener(request, response, id);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
            return;
        }
        
        // Crear objeto Audiovisual con datos actualizados
        Audiovisual audiovisual = new Audiovisual();
        audiovisual.setIdMaterial(id);
        audiovisual.setTipoMaterial(TipoMaterial.Audiovisual);
        audiovisual.setTitulo(titulo.trim());
        audiovisual.setUbicacion(ubicacion.trim());
        audiovisual.setCantidadTotal(cantidadTotal);
        audiovisual.setFormato(formato.trim());
        audiovisual.setDuracion(duracion);
        
        // Actualizar en la base de datos
        try {
            AudiovisualModel audiovisualModel = new AudiovisualModel();
            MaterialModel materialModel = new MaterialModel();
            
            boolean actualizado = audiovisualModel.update(audiovisual);
            
            if (actualizado) {
                actualizado = materialModel.actualizar(audiovisual);
            }
            
            if (actualizado) {
                request.getSession().setAttribute("exito", "Audiovisual actualizado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo actualizar el audiovisual");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al actualizar el audiovisual: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
}
