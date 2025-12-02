<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gestión de Préstamos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dataTables.bootstrap.min.css"/>
    <style>
        .badge-pendiente { background-color: #f0ad4e; }
        .badge-en-curso { background-color: #5bc0de; }
        .badge-devuelto { background-color: #5cb85c; }
        .badge-denegado { background-color: #d9534f; }
        .mora-warning { color: #d9534f; font-weight: bold; }
        .filter-section { margin-bottom: 20px; padding: 15px; background-color: #f5f5f5; border-radius: 5px; }
        .info-box { padding: 15px; margin-bottom: 20px; border-radius: 5px; }
        .info-box.success { background-color: #dff0d8; border: 1px solid #d6e9c6; }
        .info-box.warning { background-color: #fcf8e3; border: 1px solid #faebcc; }
        .info-box.danger { background-color: #f2dede; border: 1px solid #ebccd1; }
    </style>
</head>
<body>
    <jsp:include page="/cabecera.jsp"/>
    <jsp:include page="/menu.jsp"/>
    
    <div class="container" style="margin-top: 20px;">
        <div class="row">
            <div class="col-md-12">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <c:choose>
                                <c:when test="${esAdmin}">
                                    Gestión de Préstamos (Administrador)
                                </c:when>
                                <c:otherwise>
                                    Mis Préstamos
                                </c:otherwise>
                            </c:choose>
                        </h3>
                    </div>
                    <div class="panel-body">
                        
                        <!-- Mostrar mensajes de éxito o error -->
                        <c:if test="${not empty sessionScope.mensaje}">
                            <div class="alert alert-success alert-dismissible" role="alert">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                                ${sessionScope.mensaje}
                            </div>
                            <c:remove var="mensaje" scope="session"/>
                        </c:if>
                        
                        <c:if test="${not empty sessionScope.error}">
                            <div class="alert alert-danger alert-dismissible" role="alert">
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                                ${sessionScope.error}
                            </div>
                            <c:remove var="error" scope="session"/>
                        </c:if>
                        
                        <!-- Para usuarios normales: mostrar información y botón de solicitar -->
                        <c:if test="${!esAdmin}">
                            <div class="row" style="margin-bottom: 20px;">
                                <div class="col-md-8">
                                    <c:choose>
                                        <c:when test="${puedeSolicitar}">
                                            <div class="info-box success">
                                                <strong>✓ Puede solicitar préstamos</strong><br>
                                                Préstamos activos: ${prestamosActivos} / ${limiteMaximo}
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="info-box danger">
                                                <strong>✗ No puede solicitar préstamos</strong><br>
                                                ${razonBloqueo}
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="col-md-4 text-right">
                                    <c:choose>
                                        <c:when test="${puedeSolicitar}">
                                            <a href="${pageContext.request.contextPath}/prestamos.do?op=buscarMaterial" class="btn btn-success btn-lg">
                                                <span class="glyphicon glyphicon-plus"></span> Solicitar Préstamo
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default btn-lg" disabled>
                                                <span class="glyphicon glyphicon-ban-circle"></span> Solicitar Préstamo
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>
                        
                        <!-- Filtros (solo para administrador) -->
                        <c:if test="${esAdmin}">
                            <div class="filter-section">
                                <form method="get" action="${pageContext.request.contextPath}/prestamos.do" class="form-inline">
                                    <input type="hidden" name="op" value="filtrar">
                                    <div class="form-group">
                                        <label>Estado:</label>
                                        <select name="estado" class="form-control">
                                            <option value="">Todos</option>
                                            <option value="Pendiente" ${filtroEstado == 'Pendiente' ? 'selected' : ''}>Pendiente</option>
                                            <option value="En_Curso" ${filtroEstado == 'En_Curso' ? 'selected' : ''}>En Curso</option>
                                            <option value="Devuelto" ${filtroEstado == 'Devuelto' ? 'selected' : ''}>Devuelto</option>
                                            <option value="Denegado" ${filtroEstado == 'Denegado' ? 'selected' : ''}>Denegado</option>
                                        </select>
                                    </div>
                                    <div class="form-group" style="margin-left: 10px;">
                                        <label>Tipo Material:</label>
                                        <select name="tipoMaterial" class="form-control">
                                            <option value="">Todos</option>
                                            <option value="Libro" ${filtroTipoMaterial == 'Libro' ? 'selected' : ''}>Libro</option>
                                            <option value="Revista" ${filtroTipoMaterial == 'Revista' ? 'selected' : ''}>Revista</option>
                                            <option value="Audiovisual" ${filtroTipoMaterial == 'Audiovisual' ? 'selected' : ''}>Audiovisual</option>
                                            <option value="Otro_Documento" ${filtroTipoMaterial == 'Otro_Documento' ? 'selected' : ''}>Otro Documento</option>
                                        </select>
                                    </div>
                                    <div class="form-group" style="margin-left: 10px;">
                                        <label class="checkbox-inline">
                                            <input type="checkbox" name="conMora" value="true" ${conMora ? 'checked' : ''}> Solo con mora
                                        </label>
                                    </div>
                                    <button type="submit" class="btn btn-primary" style="margin-left: 10px;">
                                        <span class="glyphicon glyphicon-filter"></span> Filtrar
                                    </button>
                                    <a href="${pageContext.request.contextPath}/prestamos.do?op=listar" class="btn btn-default" style="margin-left: 5px;">
                                        <span class="glyphicon glyphicon-refresh"></span> Limpiar
                                    </a>
                                </form>
                            </div>
                        </c:if>
                        
                        <!-- Tabla de préstamos -->
                        <div class="table-responsive">
                            <table id="tablaPrestamos" class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <c:if test="${esAdmin}">
                                            <th>Usuario</th>
                                        </c:if>
                                        <th>Material</th>
                                        <th>Tipo</th>
                                        <th>Fecha Solicitud</th>
                                        <th>Fecha Estimada</th>
                                        <th>Fecha Devolución</th>
                                        <th>Estado</th>
                                        <th>Mora</th>
                                        <c:if test="${esAdmin}">
                                            <th>Acciones</th>
                                        </c:if>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${prestamos}" var="prestamo">
                                        <tr>
                                            <td>${prestamo.idPrestamo}</td>
                                            <c:if test="${esAdmin}">
                                                <td>${prestamo.nombreUsuario}</td>
                                            </c:if>
                                            <td>${prestamo.tituloMaterial}</td>
                                            <td><span class="badge badge-info">${prestamo.tipoMaterial}</span></td>
                                            <td><fmt:formatDate value="${prestamo.fechaPrestamo}" pattern="dd/MM/yyyy"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty prestamo.fechaEstimada}">
                                                        <fmt:formatDate value="${prestamo.fechaEstimada}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty prestamo.fechaDevolucion}">
                                                        <fmt:formatDate value="${prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="badge badge-${prestamo.estado == 'Pendiente' ? 'pendiente' : prestamo.estado == 'En_Curso' ? 'en-curso' : prestamo.estado == 'Devuelto' ? 'devuelto' : 'denegado'}">
                                                    ${prestamo.estado}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${prestamo.moraTotal > 0}">
                                                        <span class="mora-warning">$<fmt:formatNumber value="${prestamo.moraTotal}" pattern="#,##0.00"/></span>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <c:if test="${esAdmin}">
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/prestamos.do?op=detalle&idPrestamo=${prestamo.idPrestamo}" 
                                                       class="btn btn-info btn-xs" title="Ver Detalle">
                                                        <span class="glyphicon glyphicon-eye-open"></span> Ver Detalle
                                                    </a>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal para aprobar préstamo -->
    <div class="modal fade" id="modalAprobar" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">Aprobar Préstamo</h4>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/prestamos.do" id="formAprobar">
                    <input type="hidden" name="op" value="aprobar">
                    <input type="hidden" name="idPrestamo" id="aprobar_idPrestamo">
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Fecha estimada de devolución:</label>
                            <input type="date" name="fechaEstimada" class="form-control" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-success">Aprobar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- Modal para abonar mora -->
    <div class="modal fade" id="modalAbonar" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">Abonar a Mora</h4>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/prestamos.do" id="formAbonar">
                    <input type="hidden" name="op" value="abonar">
                    <input type="hidden" name="idPrestamo" id="abonar_idPrestamo">
                    <div class="modal-body">
                        <p>Mora pendiente: <strong id="abonar_moraPendiente"></strong></p>
                        <div class="form-group">
                            <label>Monto a abonar:</label>
                            <input type="number" name="monto" class="form-control" step="0.01" min="0.01" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-warning">Abonar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/assets/js/jquery-1.12.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/dataTables.bootstrap.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#tablaPrestamos').DataTable({
                "language": {
                    "url": "//cdn.datatables.net/plug-ins/1.10.16/i18n/Spanish.json"
                },
                "order": [[0, "desc"]]
            });
        });
        
        function aprobarPrestamo(idPrestamo) {
            $('#aprobar_idPrestamo').val(idPrestamo);
            $('#modalAprobar').modal('show');
        }
        
        function denegarPrestamo(idPrestamo) {
            if (confirm('¿Está seguro de denegar este préstamo?')) {
                window.location.href = '${pageContext.request.contextPath}/prestamos.do?op=denegar&idPrestamo=' + idPrestamo;
            }
        }
        
        function devolverPrestamo(idPrestamo) {
            if (confirm('¿Confirma la devolución de este material?')) {
                window.location.href = '${pageContext.request.contextPath}/prestamos.do?op=devolver&idPrestamo=' + idPrestamo;
            }
        }
        
        function abonarMora(idPrestamo, moraPendiente) {
            $('#abonar_idPrestamo').val(idPrestamo);
            $('#abonar_moraPendiente').text('$' + moraPendiente.toFixed(2));
            $('#modalAbonar').modal('show');
        }
    </script>
</body>
</html>
