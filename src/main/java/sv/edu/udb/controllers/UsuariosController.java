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

import sv.edu.udb.beans.Usuario;
import sv.edu.udb.beans.Usuario.TipoUsuario;
import sv.edu.udb.model.UsuarioModel;

/**
 *
 * @author cindy
 */
@WebServlet(name = "UsuariosController", urlPatterns = {"/usuarios.do"})
public class UsuariosController extends HttpServlet {
    
    UsuarioModel modelo = new UsuarioModel();
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
            case "listar":
                listar(request, response);
                break;
            case "nuevo":
                prepararTiposUsuario(request); 
                
                request.getRequestDispatcher("/usuario/nuevoUsuario.jsp").forward(request, response);
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
    
 

    private void prepararTiposUsuario(HttpServletRequest request) {
        request.setAttribute("tiposUsuario", TipoUsuario.values()); 
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Usuario> listaUsuarios = modelo.listarTodos();
            request.setAttribute("listaUsuarios", listaUsuarios);
            request.getRequestDispatcher("/usuario/listarUsuario.jsp").forward(request, response);
        } catch (Exception e) {
            Logger.getLogger(UsuariosController.class.getName()).log(Level.SEVERE, "Error al cargar lista de usuarios.", e);
            request.getSession().setAttribute("fracaso", "Error al cargar la lista de usuarios.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");  
        }
    }

    private void insertar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        
    
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password"); 
        String tipoUsuarioStr = request.getParameter("tipoUsuario");

       
        if (nombre == null || nombre.trim().isEmpty()) listaErrores.add("El nombre es obligatorio.");
        if (correo == null || correo.trim().isEmpty()) listaErrores.add("El correo es obligatorio.");
        if (password == null || password.trim().isEmpty()) listaErrores.add("La contraseña es obligatoria.");
        if (tipoUsuarioStr == null || tipoUsuarioStr.trim().isEmpty()) listaErrores.add("El tipo de usuario es obligatorio.");
        
        Usuario usuario = new Usuario();
        

        TipoUsuario tipoUsuario = null;
        if (tipoUsuarioStr != null && !tipoUsuarioStr.trim().isEmpty()) {
            try {
                tipoUsuario = TipoUsuario.valueOf(tipoUsuarioStr.trim());
            } catch (IllegalArgumentException e) {
                listaErrores.add("Tipo de Usuario inválido.");
            }
        }
       
        if (!listaErrores.isEmpty()) {
         
            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setTipoUsuario(tipoUsuario);
            
            prepararTiposUsuario(request); 
            request.setAttribute("listaErrores", listaErrores);
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/usuario/nuevoUsuario.jsp").forward(request, response);
            return;
        } 
        

        try {
            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim());
            usuario.setTipoUsuario(tipoUsuario);
            
            usuario.setPassword(password); 

            if (modelo.insertar(usuario) > 0) {
                request.getSession().setAttribute("exito", "Usuario registrado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo registrar el usuario. El correo podría estar duplicado.");
            }
            response.sendRedirect("usuarios.do");
            
        } catch (Exception ex) {
            Logger.getLogger(UsuariosController.class.getName()).log(Level.SEVERE, "Error en inserción.", ex);
            request.getSession().setAttribute("fracaso", "Error interno al intentar registrar el usuario.");
            response.sendRedirect("usuarios.do?op=nuevo"); 
        }
    }
    
    private void obtener(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            Usuario miUsuario = modelo.obtenerPorId(idUsuario);
            
            if (miUsuario != null) {
                request.setAttribute("usuario", miUsuario);
                prepararTiposUsuario(request); 
                request.getRequestDispatcher("/usuario/editarUsuario.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("fracaso", "Usuario no encontrado.");
                response.sendRedirect("usuarios.do");
            }

        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de usuario inválido.");
            response.sendRedirect("usuarios.do");
        } catch (Exception ex) {
            Logger.getLogger(UsuariosController.class.getName()).log(Level.SEVERE, "Error al obtener usuario.", ex);
            response.sendRedirect("usuarios.do");
        }
    }

    private void modificar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listaErrores.clear();
        Usuario miUsuario = new Usuario();
        
        try {
           
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            miUsuario.setIdUsuario(idUsuario);
            
   
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String tipoUsuarioStr = request.getParameter("tipoUsuario");
            String password = request.getParameter("password");

        
            if (nombre == null || nombre.trim().isEmpty()) listaErrores.add("El nombre es obligatorio.");
            if (correo == null || correo.trim().isEmpty()) listaErrores.add("El correo es obligatorio.");
            if (tipoUsuarioStr == null || tipoUsuarioStr.trim().isEmpty()) listaErrores.add("El tipo de usuario es obligatorio.");
            
       
            miUsuario.setNombre(nombre != null ? nombre.trim() : null);
            miUsuario.setCorreo(correo != null ? correo.trim() : null);
            
            TipoUsuario tipoUsuario = null;
            if (tipoUsuarioStr != null && !tipoUsuarioStr.trim().isEmpty()) {
                 try {
                     tipoUsuario = TipoUsuario.valueOf(tipoUsuarioStr.trim());
                     miUsuario.setTipoUsuario(tipoUsuario);
                 } catch (IllegalArgumentException e) {
                     listaErrores.add("Tipo de Usuario inválido.");
                 }
            }
            
         
            if (!listaErrores.isEmpty()) {
                request.setAttribute("listaErrores", listaErrores);
                request.setAttribute("usuario", miUsuario);
                prepararTiposUsuario(request); 
                request.getRequestDispatcher("/usuario/editarUsuario.jsp").forward(request, response);
                return;
            } 
            
      
            
     
            if (password != null && !password.trim().isEmpty()) {
               
                if (modelo.actualizarPassword(idUsuario, password) == 0) {
                     throw new Exception("Error al actualizar la contraseña.");
                }
            }
            
     
            miUsuario.setTipoUsuario(tipoUsuario);
            
            if (modelo.actualizar(miUsuario) > 0) {
                request.getSession().setAttribute("exito", "Usuario modificado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se pudo modificar el usuario (correo duplicado o error en BD).");
            }
            response.sendRedirect(request.getContextPath() + "/usuarios.do");

        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de usuario inválido.");
            response.sendRedirect(request.getContextPath() + "/usuarios.do");
        } catch (Exception ex) {
             Logger.getLogger(UsuariosController.class.getName()).log(Level.SEVERE, "Error en modificación: " + ex.getMessage(), ex);
             request.getSession().setAttribute("fracaso", "Error interno al intentar modificar el usuario.");
             response.sendRedirect(request.getContextPath() + "/usuarios.do");
        }
    }
    
    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            if (modelo.eliminar(idUsuario) > 0) {
                request.getSession().setAttribute("exito", "Usuario eliminado exitosamente");
            } else {
                request.getSession().setAttribute("fracaso", "No se puede eliminar este usuario.");
            }
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("fracaso", "ID de usuario inválido.");
        }
        response.sendRedirect(request.getContextPath() + "/usuarios.do");
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
