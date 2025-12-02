package sv.edu.udb.controllers;

import sv.edu.udb.beans.Usuario;
import sv.edu.udb.beans.Prestamo;
import sv.edu.udb.beans.Material;
import sv.edu.udb.model.PrestamoModel;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.servicios.PrestamoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador SIMPLIFICADO para gestionar préstamos
 * Todo se maneja desde listarPrestamos.jsp
 */
@WebServlet("/prestamos.do")
public class PrestamosController extends HttpServlet {
    
    private PrestamoService prestamoService;
    private PrestamoModel prestamoModel;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.prestamoService = new PrestamoService();
        this.prestamoModel = new PrestamoModel();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String op = request.getParameter("op");
        if (op == null) op = "listar";
        
        switch (op) {
            case "listar":
                listar(request, response);
                break;
            case "filtrar":
                filtrar(request, response);
                break;
            case "solicitar":
                solicitar(request, response);
                break;
            case "aprobar":
                aprobar(request, response);
                break;
            case "denegar":
                denegar(request, response);
                break;
            case "devolver":
                devolver(request, response);
                break;
            case "abonar":
                abonar(request, response);
                break;
            default:
                listar(request, response);
                break;
        }
    }
    
    /**
     * Lista todos los préstamos con toda la información necesaria para la vista
     */
    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        List<Prestamo> prestamos;
        
        // Determinar si es administrador
        boolean esAdmin = usuario.getTipoUsuario() == Usuario.TipoUsuario.Encargado;
        
        // Cargar préstamos según el tipo de usuario (con información de usuario y material)
        if (esAdmin) {
            prestamos = prestamoModel.selectAllWithInfo();
        } else {
            prestamos = prestamoModel.selectByUsuarioWithInfo(usuario.getIdUsuario());
        }
        
        // Para usuarios normales: verificar disponibilidad para solicitar préstamos
        if (!esAdmin) {
            Map<String, Object> disponibilidad = prestamoService.verificarDisponibilidadPrestamo(usuario.getIdUsuario());
            
            boolean puedeSolicitar = (boolean) disponibilidad.get("puedeSolicitar");
            int prestamosActivos = (int) disponibilidad.get("prestamosActivos");
            int limiteMaximo = (int) disponibilidad.get("limiteMaximo");
            
            // Verificar si tiene moras pendientes
            boolean tieneMora = false;
            List<Prestamo> prestamosConMora = prestamoModel.selectPrestamosConMora();
            for (Prestamo p : prestamosConMora) {
                if (p.getIdUsuario() == usuario.getIdUsuario()) {
                    tieneMora = true;
                    break;
                }
            }
            
            // Determinar razón de bloqueo
            String razonBloqueo = "";
            if (tieneMora) {
                puedeSolicitar = false;
                razonBloqueo = "Tiene mora pendiente. Debe pagar antes de solicitar préstamos.";
            } else if (!puedeSolicitar) {
                razonBloqueo = "Ha alcanzado el límite de " + limiteMaximo + " préstamos simultáneos.";
            }
            
            // Obtener materiales disponibles
            MaterialModel materialModel = new MaterialModel();
            List<Material> materialesDisponibles = materialModel.listarTodos().stream()
                .filter(m -> m.getCantidadDisponible() > 0)
                .collect(java.util.stream.Collectors.toList());
            
            request.setAttribute("puedeSolicitar", puedeSolicitar);
            request.setAttribute("prestamosActivos", prestamosActivos);
            request.setAttribute("limiteMaximo", limiteMaximo);
            request.setAttribute("tieneMora", tieneMora);
            request.setAttribute("razonBloqueo", razonBloqueo);
            request.setAttribute("materialesDisponibles", materialesDisponibles);
        }
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("usuario", usuario);
        request.setAttribute("esAdmin", esAdmin);
        request.getRequestDispatcher("/prestamos/listarPrestamos.jsp").forward(request, response);
    }
    
    /**
     * Filtra préstamos por estado o tipo de material
     */
    private void filtrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        String filtroEstado = request.getParameter("estado");
        String filtroTipoMaterial = request.getParameter("tipoMaterial");
        
        List<Prestamo> prestamos;
        
        if (filtroEstado != null && !filtroEstado.isEmpty()) {
            prestamos = prestamoModel.selectPrestamosPorEstado(filtroEstado);
        } else {
            prestamos = prestamoModel.selectAll();
        }
        
        // Filtrar por tipo de material si es necesario
        if (filtroTipoMaterial != null && !filtroTipoMaterial.isEmpty()) {
            MaterialModel materialModel = new MaterialModel();
            prestamos = prestamos.stream()
                .filter(p -> {
                    Material mat = materialModel.obtenerPorId(p.getIdMaterial());
                    return mat != null && mat.getTipoMaterial().name().equals(filtroTipoMaterial);
                })
                .collect(Collectors.toList());
        }
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("usuario", usuario);
        request.setAttribute("filtroEstado", filtroEstado);
        request.setAttribute("filtroTipoMaterial", filtroTipoMaterial);
        request.getRequestDispatcher("/prestamos/listarPrestamos.jsp").forward(request, response);
    }
    
    /**
     * Solicita un nuevo préstamo
     */
    private void solicitar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        int idMaterial = Integer.parseInt(request.getParameter("idMaterial"));
        
        // El servicio maneja todas las validaciones
        Map<String, Object> resultado = prestamoService.solicitarPrestamo(
            usuario.getIdUsuario(), idMaterial);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=listar");
    }
    
    /**
     * Aprueba un préstamo
     */
    private void aprobar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        String fechaEstimadaStr = request.getParameter("fechaEstimada");
        
        LocalDate fechaEstimada = null;
        if (fechaEstimadaStr != null && !fechaEstimadaStr.isEmpty()) {
            fechaEstimada = LocalDate.parse(fechaEstimadaStr);
        }
        
        Map<String, Object> resultado = prestamoService.aprobarPrestamo(idPrestamo, fechaEstimada);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=listar");
    }
    
    /**
     * Deniega un préstamo
     */
    private void denegar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        
        Map<String, Object> resultado = prestamoService.denegarPrestamo(idPrestamo);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=listar");
    }
    
    /**
     * Registra la devolución de un préstamo
     */
    private void devolver(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        
        Map<String, Object> resultado = prestamoService.registrarDevolucion(idPrestamo);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=listar");
    }
    
    /**
     * Registra un abono a mora
     */
    private void abonar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        BigDecimal monto = new BigDecimal(request.getParameter("monto"));
        
        Map<String, Object> resultado = prestamoService.abonarMora(idPrestamo, monto);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=listar");
    }
    
    /**
     * MÉTODO TEMPORAL: Obtiene usuario de sesión o crea uno de prueba
     */
    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            String tipoTest = request.getParameter("userTest");
            
            if ("admin".equals(tipoTest)) {
                usuario = crearUsuarioTemporal(1, "Admin Test", "admin@test.com", Usuario.TipoUsuario.Encargado);
            } else if ("profesor".equals(tipoTest)) {
                usuario = crearUsuarioTemporal(2, "Profesor Test", "profesor@test.com", Usuario.TipoUsuario.Profesor);
            } else {
                usuario = crearUsuarioTemporal(4, "Alumno Test", "alumno@test.com", Usuario.TipoUsuario.Alumno);
            }
            
            session.setAttribute("usuario", usuario);
        }
        
        return usuario;
    }
    
    /**
     * MÉTODO TEMPORAL: Crea un usuario de prueba
     */
    private Usuario crearUsuarioTemporal(int id, String nombre, String correo, Usuario.TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setTipoUsuario(tipo);
        return usuario;
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
}
