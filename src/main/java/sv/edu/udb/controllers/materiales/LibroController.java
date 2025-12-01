package sv.edu.udb.controllers.materiales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.Libro;
import sv.edu.udb.beans.Material.TipoMaterial;
import sv.edu.udb.model.AutorModel;
import sv.edu.udb.model.EditorialModel;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.model.materiales.LibroModel;

/**
 * Controlador específico para operaciones CRUD de Libros
 */
public class LibroController {
    
    private List<String> listaErrores;
    
    public LibroController(List<String> listaErrores) {
        this.listaErrores = listaErrores;
    }
    
    /**
     * Muestra el formulario para nuevo libro
     */
    public void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AutorModel autorModel = new AutorModel();
        EditorialModel editorialModel = new EditorialModel();
        
        request.setAttribute("listaAutores", autorModel.listarTodos());
        request.setAttribute("listaEditoriales", editorialModel.listarTodas());
        request.getRequestDispatcher("/materiales/nuevoLibro.jsp").forward(request, response);
    }
    
    /**
     * Inserta un nuevo libro
     */
    public void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String titulo = request.getParameter("titulo");
        String isbn = request.getParameter("isbn");
        String ubicacion = request.getParameter("ubicacion");
        String[] autoresIds = request.getParameterValues("autores");
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            listaErrores.add("El ISBN es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (autoresIds == null || autoresIds.length == 0) {
            listaErrores.add("Debe seleccionar al menos un autor.");
        }
        
        int cantidadTotal = 0;
        int editorialId = 0;
        
        try {
            cantidadTotal = Integer.parseInt(request.getParameter("cantidadTotal"));
            if (cantidadTotal < 0) {
                listaErrores.add("La cantidad total debe ser mayor o igual a 0.");
            }
        } catch (NumberFormatException e) {
            listaErrores.add("La cantidad total debe ser un número válido.");
        }
        
        try {
            editorialId = Integer.parseInt(request.getParameter("editorial"));
        } catch (NumberFormatException e) {
            listaErrores.add("Debe seleccionar una editorial válida.");
        }
        
        if (!listaErrores.isEmpty()) {
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("titulo", titulo);
            request.setAttribute("isbn", isbn);
            request.setAttribute("ubicacion", ubicacion);
            request.setAttribute("cantidadTotal", cantidadTotal);
            request.setAttribute("editorialId", editorialId);
            mostrarFormularioNuevo(request, response);
            return;
        }
        
        // Crear objeto Libro
        Libro libro = new Libro();
        libro.setTipoMaterial(TipoMaterial.Libro);
        libro.setTitulo(titulo.trim());
        libro.setUbicacion(ubicacion.trim());
        libro.setCantidadTotal(cantidadTotal);
        libro.setCantidadDisponible(cantidadTotal);
        libro.setCantidadPrestada(0);
        libro.setCantidadDaniada(0);
        libro.setIsbn(isbn.trim());
        libro.setIdEditorial(editorialId);
        
        // Convertir autores a lista de IDs
        List<Integer> listaAutoresIds = new ArrayList<>();
        for (String autorIdStr : autoresIds) {
            try {
                listaAutoresIds.add(Integer.parseInt(autorIdStr));
            } catch (NumberFormatException e) {
                // Ignorar IDs inválidos
            }
        }
        libro.setIdsAutores(listaAutoresIds);
        
        // Insertar en la base de datos
        try {
            LibroModel libroModel = new LibroModel();
            Libro libroInsertado = libroModel.insert(libro);
            
            if (libroInsertado != null) {
                request.getSession().setAttribute("exito", "Libro agregado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo agregar el libro");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al agregar el libro: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
    
    /**
     * Obtiene un libro para editar
     */
    public void obtener(HttpServletRequest request, HttpServletResponse response, int id)
            throws ServletException, IOException {
        try {
            LibroModel libroModel = new LibroModel();
            Libro libro = libroModel.obtenerPorId(id);
            
            if (libro != null) {
                request.setAttribute("libro", libro);
                
                // Cargar listas necesarias para el formulario
                AutorModel autorModel = new AutorModel();
                EditorialModel editorialModel = new EditorialModel();
                request.setAttribute("listaAutores", autorModel.listarTodos());
                request.setAttribute("listaEditoriales", editorialModel.listarTodas());
                
                request.getRequestDispatcher("/materiales/editarLibro.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("fracaso", "Libro no encontrado");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al cargar el libro: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/materiales.do");
        }
    }
    
    /**
     * Modifica un libro existente
     */
    public void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String isbn = request.getParameter("isbn");
        String ubicacion = request.getParameter("ubicacion");
        String[] autoresIds = request.getParameterValues("autores");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            request.getSession().setAttribute("fracaso", "ID del libro es obligatorio");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        if (titulo == null || titulo.trim().isEmpty()) {
            listaErrores.add("El título es obligatorio.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            listaErrores.add("El ISBN es obligatorio.");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            listaErrores.add("La ubicación es obligatoria.");
        }
        if (autoresIds == null || autoresIds.length == 0) {
            listaErrores.add("Debe seleccionar al menos un autor.");
        }
        
        int id = 0;
        int cantidadTotal = 0;
        int editorialId = 0;
        
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
            editorialId = Integer.parseInt(request.getParameter("editorial"));
        } catch (NumberFormatException e) {
            listaErrores.add("Debe seleccionar una editorial válida.");
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
        
        // Crear objeto Libro con datos actualizados
        Libro libro = new Libro();
        libro.setIdMaterial(id);
        libro.setTipoMaterial(TipoMaterial.Libro);
        libro.setTitulo(titulo.trim());
        libro.setUbicacion(ubicacion.trim());
        libro.setCantidadTotal(cantidadTotal);
        libro.setIsbn(isbn.trim());
        libro.setIdEditorial(editorialId);
        
        // Convertir autores a lista de IDs
        List<Integer> listaAutoresIds = new ArrayList<>();
        for (String autorIdStr : autoresIds) {
            try {
                listaAutoresIds.add(Integer.parseInt(autorIdStr));
            } catch (NumberFormatException e) {
                // Ignorar IDs inválidos
            }
        }
        libro.setIdsAutores(listaAutoresIds);
        
        // Actualizar en la base de datos
        try {
            LibroModel libroModel = new LibroModel();
            MaterialModel materialModel = new MaterialModel();
            
            // Actualizar datos del libro
            boolean actualizado = libroModel.update(libro);
            
            // Actualizar datos base del material
            if (actualizado) {
                actualizado = materialModel.actualizar(libro);
            }
            
            if (actualizado) {
                request.getSession().setAttribute("exito", "Libro actualizado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo actualizar el libro");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("fracaso", "Error al actualizar el libro: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
}
