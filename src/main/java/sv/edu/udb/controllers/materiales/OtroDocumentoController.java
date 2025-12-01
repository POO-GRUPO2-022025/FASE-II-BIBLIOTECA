package sv.edu.udb.controllers.materiales;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.OtroDocumento;
import sv.edu.udb.beans.Material.TipoMaterial;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.model.materiales.OtroDocumentoModel;

/**
 * Controlador específico para operaciones CRUD de Otros Documentos
 */
public class OtroDocumentoController {
    
    private List<String> listaErrores;
    
    public OtroDocumentoController(List<String> listaErrores) {
        this.listaErrores = listaErrores;
    }
    
    /**
     * Muestra el formulario para nuevo documento
     */
    public void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/materiales/nuevoOtroDocumento.jsp").forward(request, response);
    }
    
    /**
     * Inserta un nuevo documento
     */
    public void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String descripcion = request.getParameter("descripcion");
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            listaErrores.add("La descripción es obligatoria.");
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
            request.setAttribute("descripcion", descripcion);
            request.setAttribute("cantidadTotal", cantidadTotal);
            request.getRequestDispatcher("/materiales/nuevoOtroDocumento.jsp").forward(request, response);
            return;
        }
        
        // Crear objeto OtroDocumento
        OtroDocumento otroDocumento = new OtroDocumento();
        otroDocumento.setTipoMaterial(TipoMaterial.Otro);
        otroDocumento.setTitulo(titulo.trim());
        otroDocumento.setUbicacion(ubicacion.trim());
        otroDocumento.setCantidadTotal(cantidadTotal);
        otroDocumento.setCantidadDisponible(cantidadTotal);
        otroDocumento.setCantidadPrestada(0);
        otroDocumento.setCantidadDaniada(0);
        otroDocumento.setDescripcion(descripcion.trim());
        
        // Insertar en la base de datos
        try {
            OtroDocumentoModel otroDocumentoModel = new OtroDocumentoModel();
            OtroDocumento documentoInsertado = otroDocumentoModel.insert(otroDocumento);
            
            if (documentoInsertado != null) {
                request.getSession().setAttribute("exito", "Documento agregado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo agregar el documento");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al agregar el documento: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
    
    /**
     * Obtiene un documento para editar
     */
    public void obtener(HttpServletRequest request, HttpServletResponse response, int id)
            throws ServletException, IOException {
        try {
            OtroDocumentoModel otroDocumentoModel = new OtroDocumentoModel();
            OtroDocumento otroDocumento = otroDocumentoModel.obtenerPorId(id);
            
            if (otroDocumento != null) {
                request.setAttribute("otroDocumento", otroDocumento);
                request.getRequestDispatcher("/materiales/editarOtroDocumento.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("fracaso", "Documento no encontrado");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al cargar el documento: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/materiales.do");
        }
    }
    
    /**
     * Modifica un documento existente
     */
    public void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String ubicacion = request.getParameter("ubicacion");
        String descripcion = request.getParameter("descripcion");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            request.getSession().setAttribute("fracaso", "ID del documento es obligatorio");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            listaErrores.add("La descripción es obligatoria.");
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
        
        // Crear objeto OtroDocumento con datos actualizados
        OtroDocumento otroDocumento = new OtroDocumento();
        otroDocumento.setIdMaterial(id);
        otroDocumento.setTipoMaterial(TipoMaterial.Otro);
        otroDocumento.setTitulo(titulo.trim());
        otroDocumento.setUbicacion(ubicacion.trim());
        otroDocumento.setCantidadTotal(cantidadTotal);
        otroDocumento.setDescripcion(descripcion.trim());
        
        // Actualizar en la base de datos
        try {
            OtroDocumentoModel otroDocumentoModel = new OtroDocumentoModel();
            MaterialModel materialModel = new MaterialModel();
            
            boolean actualizado = otroDocumentoModel.update(otroDocumento);
            
            if (actualizado) {
                actualizado = materialModel.actualizar(otroDocumento);
            }
            
            if (actualizado) {
                request.getSession().setAttribute("exito", "Documento actualizado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo actualizar el documento");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al actualizar el documento: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
}
