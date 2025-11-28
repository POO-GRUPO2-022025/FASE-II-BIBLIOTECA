/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package sv.edu.udb.controllers;

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

import sv.edu.udb.beans.Autor;
import sv.edu.udb.model.AutorModel;

/**
 *
 * @author bryan
 */
@WebServlet(name = "AutoresController", urlPatterns = {"/autores.do"})
public class AutoresController extends HttpServlet {
    AutorModel modelo = new AutorModel();
    List<String> listaErrores = new ArrayList<>();

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
        response.setContentType("text/html;charset=UTF-8");
        if (request.getParameter("op") == null) {
            listar(request, response);
            return;
        }

        String operacion = request.getParameter("op");

        switch (operacion) {
            case "nuevo":
                request.getRequestDispatcher("/autores/nuevoAutor.jsp").forward(request, response);
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
        List<Autor> listaAutores = modelo.listarTodos();
        request.setAttribute("listaAutores", listaAutores);
        request.getRequestDispatcher("/autores/listarAutores.jsp").forward(request, response);
    }

    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
        String nombre = request.getParameter("nombre");
        if (nombre == null || nombre.trim().isEmpty()) {
            listaErrores.add("El nombre del autor es obligatorio.");
        } else {
            nombre = nombre.trim();
        }

        String apellidos = request.getParameter("apellidos");
        if (apellidos == null || apellidos.trim().isEmpty()) {
            listaErrores.add("Los apellidos del autor son obligatorios.");
        } else {
            apellidos = apellidos.trim();
        }

        String pais = request.getParameter("pais");
        if (pais == null || pais.trim().isEmpty()) {
            listaErrores.add("El pais es obligatorio.");
        } else {
            pais = pais.trim();
        }
        
        if (!listaErrores.isEmpty()) {
            Autor autor = new Autor();
            autor.setNombre(nombre);
            autor.setApellidos(apellidos);
            autor.setPais(pais);
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("autor", autor);
            request.getRequestDispatcher("/autores/nuevoAutor.jsp").forward(request, response);
            return;
        } else {
            Autor autor = new Autor(nombre, apellidos, pais);
            modelo.insertar(autor);
            response.sendRedirect("autores.do");
        }
    }

    private void obtener(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int codigo = Integer.parseInt(request.getParameter("id"));
            Autor miAutor = modelo.obtenerPorId(codigo);
            if (miAutor != null) {
                request.setAttribute("autor", miAutor);
                request.getRequestDispatcher("/autores/editarAutor.jsp").forward(request, response);
            }

        } catch (ServletException | IOException ex) {
            Logger.getLogger(AutoresController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            listaErrores.clear();
            Autor miAutor = new Autor();
            miAutor.setIdAutor(Integer.parseInt(request.getParameter("codigo")));
            miAutor.setApellidos(request.getParameter("apellidos"));
            miAutor.setNombre(request.getParameter("nombre"));
            miAutor.setPais(request.getParameter("pais"));
            
            if(miAutor.getNombre() == null || miAutor.getNombre().trim().isEmpty()) {
                listaErrores.add("El nombre del autor es obligatorio.");
            } else {
                miAutor.setNombre(miAutor.getNombre().trim());
            }

            if(miAutor.getApellidos() == null || miAutor.getApellidos().trim().isEmpty()) {
                listaErrores.add("Los apellidos del autor son obligatorios.");
            } else {
                miAutor.setApellidos(miAutor.getApellidos().trim());
            }

            if(miAutor.getPais() == null || miAutor.getPais().trim().isEmpty()) {
                listaErrores.add("El pais es obligatorio.");
            } else {
                miAutor.setPais(miAutor.getPais().trim());
            }
            
            if(!listaErrores.isEmpty()) {
                request.setAttribute("listaErrores", listaErrores);
                request.setAttribute("autor", miAutor);
                request.getRequestDispatcher("/autores/editarAutor.jsp").forward(request, response);
                return;
            } else {
                if (modelo.actualizar(miAutor) > 0) {
                    request.getSession().setAttribute("exito", "Autor modificado exitosamente");
                } else {
                    request.getSession().setAttribute("fracaso", "No se pudo modificar el autor");
                }
                response.sendRedirect(request.getContextPath() + "/autores.do");
            }
        } catch (ServletException ex) {
            Logger.getLogger(AutoresController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AutoresController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idAutor = Integer.parseInt(request.getParameter("id"));
            if (modelo.eliminar(idAutor) > 0) {
                request.getSession().setAttribute("exito", "Autor eliminado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se puede eliminar este autor");
            }
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de autor inválido");
        }
        response.sendRedirect(request.getContextPath() + "/autores.do");
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
        return "Short description";
    }// </editor-fold>

}
