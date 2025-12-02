/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package sv.edu.udb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.Material;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.controllers.materiales.LibroController;
import sv.edu.udb.controllers.materiales.RevistaController;
import sv.edu.udb.controllers.materiales.AudiovisualController;
import sv.edu.udb.controllers.materiales.OtroDocumentoController;

/**
 * Controlador unificado para gestionar todos los tipos de materiales de la biblioteca
 * Delega las operaciones específicas a controladores especializados
 * @author bryan
 */
@WebServlet(name = "MaterialesController", urlPatterns = {"/materiales.do"})
public class MaterialesController extends HttpServlet {
    private MaterialModel modelo = new MaterialModel();
    private List<String> listaErrores = new ArrayList<>();
    
    // Controladores especializados para cada tipo de material
    private LibroController libroController;
    private RevistaController revistaController;
    private AudiovisualController audiovisualController;
    private OtroDocumentoController otroDocumentoController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        libroController = new LibroController(listaErrores);
        revistaController = new RevistaController(listaErrores);
        audiovisualController = new AudiovisualController(listaErrores);
        otroDocumentoController = new OtroDocumentoController(listaErrores);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String operacion = request.getParameter("op");
        
        if (operacion == null || operacion.isEmpty()) {
            operacion = "listar";
        }
        
        switch (operacion) {
            case "listar":
                listar(request, response);
                break;
            case "filtrar":
                filtrar(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "insertar":
                insertar(request, response);
                break;
            case "obtener":
                obtener(request, response);
                break;
            case "modificar":
                modificar(request, response);
                break;
            case "eliminar":
                eliminar(request, response);
                break;
            default:
                listar(request, response);
                break;
        }
    }

    /**
     * Lista todos los materiales
     */
    private void listar(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        List<Material> listaMateriales = modelo.listarTodos();
        request.setAttribute("listaMateriales", listaMateriales);
        request.setAttribute("tipoFiltro", "Todos");
        request.getRequestDispatcher("/materiales/listarMateriales.jsp").forward(request, response);
    }

    /**
     * Filtra materiales por tipo
     */
    private void filtrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipo = request.getParameter("tipo");
        List<Material> listaMateriales;
        
        if (tipo == null || tipo.equals("Todos")) {
            listaMateriales = modelo.listarTodos();
            request.setAttribute("tipoFiltro", "Todos");
        } else {
            try {
                Material.TipoMaterial tipoMaterial = Material.TipoMaterial.valueOf(tipo);
                listaMateriales = modelo.listarPorTipo(tipoMaterial);
                request.setAttribute("tipoFiltro", tipo);
            } catch (IllegalArgumentException e) {
                listaMateriales = modelo.listarTodos();
                request.setAttribute("tipoFiltro", "Todos");
            }
        }
        
        request.setAttribute("listaMateriales", listaMateriales);
        request.getRequestDispatcher("/materiales/listarMateriales.jsp").forward(request, response);
    }

    /**
     * Elimina un material
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idMaterial = Integer.parseInt(request.getParameter("id"));
            if (modelo.eliminar(idMaterial)) {
                request.getSession().setAttribute("exito", "Material eliminado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se puede eliminar este material");
            }
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de material inválido");
        }
        response.sendRedirect(request.getContextPath() + "/materiales.do");
    }
    
    /**
     * Muestra el formulario para nuevo material según el tipo
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipoMaterial = request.getParameter("tipo");
        
        if (tipoMaterial == null || tipoMaterial.isEmpty()) {
            request.getSession().setAttribute("fracaso", "Debe especificar el tipo de material");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        switch (tipoMaterial) {
            case "Libro":
                libroController.mostrarFormularioNuevo(request, response);
                break;
            case "Revista":
                revistaController.mostrarFormularioNuevo(request, response);
                break;
            case "Audiovisual":
                audiovisualController.mostrarFormularioNuevo(request, response);
                break;
            case "Otro":
                otroDocumentoController.mostrarFormularioNuevo(request, response);
                break;
            default:
                request.getSession().setAttribute("fracaso", "Tipo de material no válido");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
                break;
        }
    }
    
    /**
     * Inserta un nuevo material según el tipo
     */
    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipoMaterial = request.getParameter("tipo");
        
        if (tipoMaterial == null || tipoMaterial.isEmpty()) {
            request.getSession().setAttribute("fracaso", "Debe especificar el tipo de material");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        switch (tipoMaterial) {
            case "Libro":
                libroController.insertar(request, response);
                break;
            case "Revista":
                revistaController.insertar(request, response);
                break;
            case "Audiovisual":
                audiovisualController.insertar(request, response);
                break;
            case "Otro":
                otroDocumentoController.insertar(request, response);
                break;
            default:
                request.getSession().setAttribute("fracaso", "Tipo de material no válido");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
                break;
        }
    }
    
    /**
     * Obtiene un material para editar
     */
    private void obtener(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipoMaterial = request.getParameter("tipo");
        String idMaterial = request.getParameter("id");
        
        if (tipoMaterial == null || tipoMaterial.isEmpty()) {
            request.getSession().setAttribute("fracaso", "Debe especificar el tipo de material");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        if (idMaterial == null || idMaterial.isEmpty()) {
            request.getSession().setAttribute("fracaso", "Debe especificar el ID del material");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        try {
            int id = Integer.parseInt(idMaterial);
            
            switch (tipoMaterial) {
                case "Libro":
                    libroController.obtener(request, response, id);
                    break;
                case "Revista":
                    revistaController.obtener(request, response, id);
                    break;
                case "Audiovisual":
                    audiovisualController.obtener(request, response, id);
                    break;
                case "Otro":
                    otroDocumentoController.obtener(request, response, id);
                    break;
                default:
                    request.getSession().setAttribute("fracaso", "Tipo de material no válido");
                    response.sendRedirect(request.getContextPath() + "/materiales.do");
                    break;
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("fracaso", "ID de material inválido");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
        }
    }
    
    /**
     * Modifica un material existente
     */
    private void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipoMaterial = request.getParameter("tipo");
        
        if (tipoMaterial == null || tipoMaterial.isEmpty()) {
            request.getSession().setAttribute("fracaso", "Debe especificar el tipo de material");
            response.sendRedirect(request.getContextPath() + "/materiales.do");
            return;
        }
        
        switch (tipoMaterial) {
            case "Libro":
                libroController.modificar(request, response);
                break;
            case "Revista":
                revistaController.modificar(request, response);
                break;
            case "Audiovisual":
                audiovisualController.modificar(request, response);
                break;
            case "Otro":
                otroDocumentoController.modificar(request, response);
                break;
            default:
                request.getSession().setAttribute("fracaso", "Tipo de material no válido");
                response.sendRedirect(request.getContextPath() + "/materiales.do");
                break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Controlador unificado de materiales - Biblioteca UDB";
    }// </editor-fold>

}
