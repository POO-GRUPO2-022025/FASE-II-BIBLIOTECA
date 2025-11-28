package sv.edu.udb.controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sv.edu.udb.beans.Editorial;
import sv.edu.udb.model.EditorialModel;

/**
 *
 * @author bryan
 */
@WebServlet(name = "EditorialesController", urlPatterns = { "/editoriales.do" })
public class EditorialesController extends HttpServlet {
    EditorialModel modelo = new EditorialModel();
    List<String> listaErrores = new ArrayList<>();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (request.getParameter("op") == null) {
            listar(request, response);
            return;
        }
        String operacion = request.getParameter("op");

        switch (operacion) {
            case "nuevo":
                request.getRequestDispatcher("/editoriales/nuevaEditorial.jsp").forward(request, response);
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
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Editorial> listaEditoriales = modelo.listarTodas();
        request.setAttribute("listaEditoriales", listaEditoriales);
        request.getRequestDispatcher("/editoriales/listarEditoriales.jsp").forward(request, response);
    }

    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            listaErrores.add("El nombre del editorial es obligatorio.");
        } else {
            nombre = nombre.trim();
        }
        String pais = request.getParameter("pais");
        if (pais == null || pais.trim().isEmpty()) {
            listaErrores.add("El pais es obligatorio.");
        } else {
            pais = pais.trim();
        }
        if (!listaErrores.isEmpty()) {
            Editorial editorial = new Editorial();
            editorial.setNombre(nombre);
            editorial.setPais(pais);
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("editorial", editorial);
            request.getRequestDispatcher("/editoriales/nuevaEditorial.jsp").forward(request, response);
            return;
        } else {
            Editorial editorial = new Editorial(nombre, pais);
            modelo.insertar(editorial);
            response.sendRedirect("editoriales.do");
        }
    }

    private void obtener(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int codigo = Integer.parseInt(request.getParameter("id"));
            Editorial miEditorial = modelo.obtenerPorId(codigo);
            if (miEditorial != null) {
                request.setAttribute("editorial", miEditorial);
                request.getRequestDispatcher("/editoriales/editarEditorial.jsp").forward(request, response);
            }

        } catch (ServletException | IOException ex) {
            Logger.getLogger(EditorialesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            listaErrores.clear();
            Editorial miEditorial = new Editorial();
            miEditorial.setIdEditorial(Integer.parseInt(request.getParameter("codigo")));
            miEditorial.setNombre(request.getParameter("nombre"));
            miEditorial.setPais(request.getParameter("pais"));
            
            if(miEditorial.getNombre() == null || miEditorial.getNombre().trim().isEmpty()) {
                listaErrores.add("El nombre del editorial es obligatorio.");
            } else {
                miEditorial.setNombre(miEditorial.getNombre().trim());
            }
            if(miEditorial.getPais() == null || miEditorial.getPais().trim().isEmpty()) {
                listaErrores.add("El pais es obligatorio.");
            } else {
                miEditorial.setPais(miEditorial.getPais().trim());
            }
            
            if(!listaErrores.isEmpty()) {
                request.setAttribute("listaErrores", listaErrores);
                request.setAttribute("editorial", miEditorial);
                request.getRequestDispatcher("/editoriales/editarEditorial.jsp").forward(request, response);
                return;
            } else {
                if (modelo.actualizar(miEditorial) > 0) {
                    request.getSession().setAttribute("exito", "Editorial modificado exitosamente");
                } else {
                    request.getSession().setAttribute("fracaso", "No se pudo modificar el editorial");
                }
                response.sendRedirect(request.getContextPath() + "/editoriales.do");
            }
        } catch (ServletException ex) {
            Logger.getLogger(EditorialesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EditorialesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idEditorial = Integer.parseInt(request.getParameter("id"));
            if (modelo.eliminar(idEditorial) > 0) {
                request.getSession().setAttribute("exito", "Editorial eliminado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se puede eliminar este editorial");
            }
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de editorial inválido");
        }
        response.sendRedirect(request.getContextPath() + "/editoriales.do");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
        return "Short description";
    }// </editor-fold>

}
