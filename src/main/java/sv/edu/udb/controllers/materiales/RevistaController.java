package sv.edu.udb.controllers.materiales;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.Revista;
import sv.edu.udb.beans.Material.TipoMaterial;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.model.materiales.RevistaModel;

/**
 * Controlador específico para operaciones CRUD de Revistas
 */
public class RevistaController {
    
    private List<String> listaErrores;
    
    public RevistaController(List<String> listaErrores) {
        this.listaErrores = listaErrores;
    }
    
    /**
     * Muestra el formulario para nueva revista
     */
    public void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/materiales/nuevaRevista.jsp").forward(request, response);
    }
    
    /**
     * Inserta una nueva revista
     */
    public void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String volumen = request.getParameter("volumen");
        String numero = request.getParameter("numero");
        String fechaPublicacion = request.getParameter("fechaPublicacion");
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (volumen == null || volumen.trim().isEmpty()) {
            listaErrores.add("El volumen es obligatorio.");
        }
        if (numero == null || numero.trim().isEmpty()) {
            listaErrores.add("El número es obligatorio.");
        }
        if (fechaPublicacion == null || fechaPublicacion.trim().isEmpty()) {
            listaErrores.add("La fecha de publicación es obligatoria.");
        }
        
        int cantidadTotal = 0;
        try {
            cantidadTotal = Integer.parseInt(request.getParameter("cantidadTotal"));
            if (cantidadTotal < 0) {
                listaErrores.add("La cantidad total debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La cantidad total debe ser un número válido.");
        }
        
        if (!listaErrores.isEmpty()) {
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("titulo", titulo);
            request.setAttribute("ubicacion", ubicacion);
            request.setAttribute("volumen", volumen);
            request.setAttribute("numero", numero);
            request.setAttribute("fechaPublicacion", fechaPublicacion);
            request.setAttribute("cantidadTotal", cantidadTotal);
            request.getRequestDispatcher("/materiales/nuevaRevista.jsp").forward(request, response);
            return;
        }
        
        // Crear objeto Revista
        Revista revista = new Revista();
        revista.setTipoMaterial(TipoMaterial.Revista);
        revista.setTitulo(titulo.trim());
        revista.setUbicacion(ubicacion.trim());
        revista.setCantidadTotal(cantidadTotal);
        revista.setCantidadDisponible(cantidadTotal);
        revista.setCantidadPrestada(0);
        revista.setCantidadDaniada(0);
        revista.setVolumen(volumen.trim());
        revista.setNumero(numero.trim());
        revista.setFechaPublicacion(LocalDate.parse(fechaPublicacion));
        
        // Insertar en la base de datos
        try {
            RevistaModel revistaModel = new RevistaModel();
            Revista revistaInsertada = revistaModel.insert(revista);
            
            if (revistaInsertada != null) {
                request.getSession().setAttribute("exito", "Revista agregada exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo agregar la revista");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al agregar la revista: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
    
    /**
     * Obtiene una revista para editar
     */
    public void obtener(HttpServletRequest request, HttpServletResponse response, int id)
            throws ServletException, IOException {
        try {
            RevistaModel revistaModel = new RevistaModel();
            Revista revista = revistaModel.obtenerPorId(id);
            
            if (revista != null) {
                request.setAttribute("revista", revista);
                request.getRequestDispatcher("/materiales/editarRevista.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("fracaso", "Revista no encontrada");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al cargar la revista: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/materiales.do");
        }
    }
    
    /**
     * Modifica una revista existente
     */
    public void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String volumen = request.getParameter("volumen");
        String numero = request.getParameter("numero");
        String fechaPublicacion = request.getParameter("fechaPublicacion");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            request.getSession().setAttribute("fracaso", "ID de la revista es obligatorio");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (volumen == null || volumen.trim().isEmpty()) {
            listaErrores.add("El volumen es obligatorio.");
        }
        if (numero == null || numero.trim().isEmpty()) {
            listaErrores.add("El número es obligatorio.");
        }
        if (fechaPublicacion == null || fechaPublicacion.trim().isEmpty()) {
            listaErrores.add("La fecha de publicación es obligatoria.");
        }
        
        int id = 0;
        int cantidadTotal = 0;
        
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
        
        if (!listaErrores.isEmpty()) {
            request.setAttribute("listaErrores", listaErrores);
            try {
                obtener(request, response, id);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
            return;
        }
        
        // Crear objeto Revista con datos actualizados
        Revista revista = new Revista();
        revista.setIdMaterial(id);
        revista.setTipoMaterial(TipoMaterial.Revista);
        revista.setTitulo(titulo.trim());
        revista.setUbicacion(ubicacion.trim());
        revista.setCantidadTotal(cantidadTotal);
        revista.setVolumen(volumen.trim());
        revista.setNumero(numero.trim());
        revista.setFechaPublicacion(LocalDate.parse(fechaPublicacion));
        
        // Actualizar en la base de datos
        try {
            RevistaModel revistaModel = new RevistaModel();
            MaterialModel materialModel = new MaterialModel();
            
            boolean actualizado = revistaModel.update(revista);
            
            if (actualizado) {
                actualizado = materialModel.actualizar(revista);
            }
            
            if (actualizado) {
                request.getSession().setAttribute("exito", "Revista actualizada exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo actualizar la revista");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al actualizar la revista: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
}
