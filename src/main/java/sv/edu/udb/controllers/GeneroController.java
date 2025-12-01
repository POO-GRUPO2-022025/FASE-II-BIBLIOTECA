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
import sv.edu.udb.beans.Genero;
import sv.edu.udb.model.GeneroModel;

/**
 *
 * @author cindy
 */
@WebServlet(name = "GeneroController", urlPatterns = {"/generos.do"})
public class GeneroController extends HttpServlet {
    
        GeneroModel modelo = new GeneroModel();
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
                request.getRequestDispatcher("/genero/nuevoGenero.jsp").forward(request, response);
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
         try 
         {List<Genero> listaGenero = modelo.listarTodas();
           
            request.setAttribute("listaGeneros", listaGenero); 
          
            request.getRequestDispatcher("/genero/listarGeneros.jsp").forward(request, response);
        } catch (Exception ex) {
            Logger.getLogger(GeneroController.class.getName()).log(Level.SEVERE, "Error al listar géneros", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno al listar géneros.");

    } 
    } 

     private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();   
       String nombre = request.getParameter("nombre");
       String descripcionParam = request.getParameter("descripcion");
       String descripcion = (descripcionParam == null) ? "" : descripcionParam.trim();
        if (nombre == null || nombre.trim().isEmpty()) {
            listaErrores.add("El nombre del género es obligatorio.");
        } else {
            nombre = nombre.trim();
        }
        

        if (!listaErrores.isEmpty()) {
           
            Genero genero = new Genero();
            genero.setNombre(nombre);
            genero.setDescripcion(descripcion);
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("genero", genero);
       
            request.getRequestDispatcher("/genero/nuevoGenero.jsp").forward(request, response);
            return;
        } else {
            
            Genero genero = new Genero(nombre, descripcion);
            modelo.insertar(genero);
            request.getSession().setAttribute("exito", "Género insertado exitosamente");
            response.sendRedirect("generos.do"); 
        }
    }

  private void obtener(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
           int codigo = Integer.parseInt(request.getParameter("id"));
            Genero miGenero = modelo.obtenerPorId(codigo);
            if (miGenero != null) {
                request.setAttribute("genero", miGenero);
                request.getRequestDispatcher("/genero/editarGenero.jsp").forward(request, response);
            }

        } catch (ServletException | IOException ex) {
           Logger.getLogger(GeneroController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

private void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            listaErrores.clear();
            Genero miGenero = new Genero();
            miGenero.setIdGenero(Integer.parseInt(request.getParameter("codigo")));
            miGenero.setNombre(request.getParameter("nombre"));
            miGenero.setDescripcion(request.getParameter("descripcion"));
            
     
            if(miGenero.getNombre() == null || miGenero.getNombre().trim().isEmpty()) {
                listaErrores.add("El nombre del género es obligatorio.");
            } else {
                miGenero.setNombre(miGenero.getNombre().trim());
            }
            
            if(!listaErrores.isEmpty()) {
                request.setAttribute("listaErrores", listaErrores);
                request.setAttribute("genero", miGenero);
                request.getRequestDispatcher("/genero/editarGenero.jsp").forward(request, response);
                return;
            } else {
                if (modelo.actualizar(miGenero) > 0) {
                    request.getSession().setAttribute("exito", "Género modificado exitosamente");
                } else {
                    request.getSession().setAttribute("fracaso", "No se pudo modificar el género");
                }
                response.sendRedirect(request.getContextPath() + "/generos.do");
            }
        } catch (ServletException | IOException ex) {
            Logger.getLogger(GeneroController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idGenero = Integer.parseInt(request.getParameter("id"));
            if (modelo.eliminar(idGenero) > 0) {
                request.getSession().setAttribute("exito", "Género eliminado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se puede eliminar este género (Asegúrese que ningún libro lo utilice)");
            }
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de género inválido");
        }
        response.sendRedirect(request.getContextPath() + "/generos.do");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Controlador para la entidad Géneros.";
    }
}




      
            /* TODO output your page here. You may use following sample code. */
       
  

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
 

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
 
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */

    // </editor-fold>


