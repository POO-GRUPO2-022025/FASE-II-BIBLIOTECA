package sv.edu.udb.controllers;

import sv.edu.udb.beans.DetallePrestamoDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            case "detalle":
                detalle(request, response);
                break;
            case "buscarMaterial":
                buscarMaterial(request, response);
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
            String razonBloqueo = (String) disponibilidad.getOrDefault("mensaje", "");
            
            request.setAttribute("puedeSolicitar", puedeSolicitar);
            request.setAttribute("prestamosActivos", prestamosActivos);
            request.setAttribute("limiteMaximo", limiteMaximo);
            request.setAttribute("razonBloqueo", razonBloqueo);
        }
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("usuario", usuario);
        request.setAttribute("esAdmin", esAdmin);
        request.getRequestDispatcher("/prestamos/listarPrestamos.jsp").forward(request, response);
    }
    
    /**
     * Filtra préstamos por estado, tipo de material o mora
     * Usa una sola query SQL optimizada con filtros dinámicos
     */
    private void filtrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        boolean esAdmin = usuario.getTipoUsuario() == Usuario.TipoUsuario.Encargado;
        
        String filtroEstado = request.getParameter("estado");
        String filtroTipoMaterial = request.getParameter("tipoMaterial");
        String filtroMoraStr = request.getParameter("conMora");
        boolean conMora = "true".equals(filtroMoraStr);
        
        // Una sola query con filtros dinámicos (como selectPrestamosDetalladoFiltrado)
        List<Prestamo> prestamos = prestamoModel.selectPrestamosFiltrados(
            filtroEstado, 
            filtroTipoMaterial, 
            conMora
        );
        
        request.setAttribute("prestamos", prestamos);
        request.setAttribute("usuario", usuario);
        request.setAttribute("esAdmin", esAdmin);
        request.setAttribute("filtroEstado", filtroEstado);
        request.setAttribute("filtroTipoMaterial", filtroTipoMaterial);
        request.setAttribute("conMora", conMora);
        request.getRequestDispatcher("/prestamos/listarPrestamos.jsp").forward(request, response);
    }
    
    /**
     * Busca materiales por título y tipo para solicitar préstamo
     */
    private void buscarMaterial(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        String titulo = request.getParameter("titulo");
        String tipo = request.getParameter("tipo");
        
        List<Material> materiales = new ArrayList<>();
        MaterialModel materialModel = new MaterialModel();
        
        // Solo buscar si al menos un criterio está presente
        if ((titulo != null && !titulo.trim().isEmpty()) || (tipo != null && !tipo.trim().isEmpty())) {
            materiales = materialModel.buscarMaterialesFiltrados(titulo, tipo);
        }
        
        request.setAttribute("materiales", materiales);
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/prestamos/seleccionarMaterial.jsp").forward(request, response);
    }
    
    /**
     * Muestra el detalle de un préstamo con todas sus opciones de acción
     */
    private void detalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Usuario usuario = obtenerUsuarioSesion(request);
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        
        // Obtener detalle completo del préstamo
        DetallePrestamoDTO detalle = prestamoService.obtenerDetallePrestamo(idPrestamo);
        
        if (detalle == null) {
            request.setAttribute("error", "Préstamo no encontrado");
            listar(request, response);
            return;
        }
        
        request.setAttribute("detalle", detalle);
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/prestamos/detallePrestamo.jsp").forward(request, response);
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
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=detalle&idPrestamo=" + idPrestamo);
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
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=detalle&idPrestamo=" + idPrestamo);
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
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=detalle&idPrestamo=" + idPrestamo);
    }
    
    /**
     * Registra un abono a mora
     */
    private void abonar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int idPrestamo = Integer.parseInt(request.getParameter("idPrestamo"));
        BigDecimal monto = new BigDecimal(request.getParameter("montoAbono"));
        
        Map<String, Object> resultado = prestamoService.abonarMora(idPrestamo, monto);
        
        if ((boolean) resultado.get("success")) {
            request.getSession().setAttribute("mensaje", resultado.get("mensaje"));
        } else {
            request.getSession().setAttribute("error", resultado.get("mensaje"));
        }
        
        response.sendRedirect(request.getContextPath() + "/prestamos.do?op=detalle&idPrestamo=" + idPrestamo);
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
                usuario = crearUsuarioTemporal(3, "Alumno Test", "alumno@test.com", Usuario.TipoUsuario.Alumno);
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
