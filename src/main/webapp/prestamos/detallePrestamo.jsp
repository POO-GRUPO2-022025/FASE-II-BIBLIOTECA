<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Detalle del Préstamo - Biblioteca UDB</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
        <style>
            .info-section {
                background-color: #f9f9f9;
                border: 1px solid #ddd;
                border-radius: 4px;
                padding: 15px;
                margin-bottom: 20px;
            }
            .info-section h4 {
                margin-top: 0;
                color: #337ab7;
                border-bottom: 2px solid #337ab7;
                padding-bottom: 10px;
                margin-bottom: 15px;
            }
            .info-row {
                margin-bottom: 10px;
            }
            .info-label {
                font-weight: bold;
                color: #555;
            }
            .estado-badge {
                display: inline-block;
                padding: 5px 10px;
                border-radius: 3px;
                font-size: 14px;
                font-weight: bold;
            }
            .estado-pendiente { background-color: #f0ad4e; color: white; }
            .estado-en-curso { background-color: #5bc0de; color: white; }
            .estado-devuelto { background-color: #5cb85c; color: white; }
            .estado-denegado { background-color: #d9534f; color: white; }
            .alert-mora {
                background-color: #fcf8e3;
                border-color: #faebcc;
                color: #8a6d3b;
                padding: 15px;
                border-radius: 4px;
                margin-bottom: 20px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/cabecera.jsp"></jsp:include>
        <jsp:include page="/menu.jsp"></jsp:include>

        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title">
                        <span class="glyphicon glyphicon-list-alt"></span> Detalle del Préstamo #${detalle.prestamo.idPrestamo}
                    </h3>
                </div>
                <div class="panel-body">
                    <!-- Botón para regresar -->
                    <div class="row" style="margin-bottom: 20px;">
                        <div class="col-md-12">
                            <a href="${pageContext.request.contextPath}/prestamos.do?op=listar" class="btn btn-default">
                                <span class="glyphicon glyphicon-arrow-left"></span> Volver al Listado
                            </a>
                        </div>
                    </div>

                    <!-- Mensajes de éxito/error -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <span class="glyphicon glyphicon-ok-sign"></span> ${success}
                        </div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <span class="glyphicon glyphicon-exclamation-sign"></span> ${error}
                        </div>
                    </c:if>

                    <!-- Información del Préstamo -->
                    <div class="info-section">
                        <h4><span class="glyphicon glyphicon-file"></span> Información del Préstamo</h4>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">ID Préstamo:</span> ${detalle.prestamo.idPrestamo}
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Fecha Préstamo:</span> 
                                    <fmt:formatDate value="${detalle.prestamo.fechaPrestamo}" pattern="dd/MM/yyyy"/>
                                </div>
                                <c:if test="${not empty detalle.prestamo.fechaEstimada}">
                                    <div class="info-row">
                                        <span class="info-label">Fecha Estimada Devolución:</span> 
                                        <fmt:formatDate value="${detalle.prestamo.fechaEstimada}" pattern="dd/MM/yyyy"/>
                                    </div>
                                </c:if>
                            </div>
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">Estado:</span> 
                                    <c:choose>
                                        <c:when test="${detalle.prestamo.estado == 'Pendiente'}">
                                            <span class="estado-badge estado-pendiente">Pendiente</span>
                                        </c:when>
                                        <c:when test="${detalle.prestamo.estado == 'En_Curso'}">
                                            <span class="estado-badge estado-en-curso">En Curso</span>
                                        </c:when>
                                        <c:when test="${detalle.prestamo.estado == 'Devuelto'}">
                                            <span class="estado-badge estado-devuelto">Devuelto</span>
                                        </c:when>
                                        <c:when test="${detalle.prestamo.estado == 'Denegado'}">
                                            <span class="estado-badge estado-denegado">Denegado</span>
                                        </c:when>
                                    </c:choose>
                                </div>
                                <c:if test="${not empty detalle.prestamo.fechaDevolucion}">
                                    <div class="info-row">
                                        <span class="info-label">Fecha Devolución:</span> 
                                        <fmt:formatDate value="${detalle.prestamo.fechaDevolucion}" pattern="dd/MM/yyyy"/>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <!-- Información del Usuario -->
                    <div class="info-section">
                        <h4><span class="glyphicon glyphicon-user"></span> Información del Usuario</h4>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">Nombre:</span> ${detalle.usuario.nombre}
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Email:</span> ${detalle.usuario.correo}
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">Tipo:</span> ${detalle.usuario.tipoUsuario}
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Información del Material -->
                    <div class="info-section">
                        <h4><span class="glyphicon glyphicon-book"></span> Información del Material</h4>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">Título:</span> ${detalle.material.titulo}
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Tipo:</span> ${detalle.material.tipoMaterial}
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-row">
                                    <span class="info-label">Ubicación:</span> ${detalle.material.ubicacion}
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Cantidad Disponible:</span> ${detalle.material.cantidadDisponible}
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Alerta de Mora -->
                    <c:if test="${detalle.tieneRetraso}">
                        <div class="alert-mora">
                            <h4>
                                <span class="glyphicon glyphicon-alert"></span> 
                                Préstamo con Retraso
                            </h4>
                            <div class="row">
                                <div class="col-md-4">
                                    <strong>Días de Retraso:</strong> ${detalle.diasRetraso}
                                </div>
                                <div class="col-md-4">
                                    <strong>Mora por Día:</strong> $${detalle.tarifa.tarifaDiaria}
                                </div>
                                <div class="col-md-4">
                                    <strong>Mora Calculada:</strong> 
                                    <span style="font-size: 18px; color: #d9534f;">$${detalle.moraOriginal}</span>
                                </div>
                            </div>
                            <hr style="margin: 10px 0;">
                            <c:if test="${detalle.prestamo.estado != 'En_Curso'}">
                                <div class="row">
                                    <div class="col-md-12">
                                        <strong>Mora Registrada en el Sistema:</strong> 
                                        <span style="font-size: 18px; color: ${detalle.prestamo.moraTotal > 0 ? '#d9534f' : '#5cb85c'};">
                                            $<fmt:formatNumber value="${detalle.prestamo.moraTotal}" minFractionDigits="2" maxFractionDigits="2"/>
                                        </span>
                                        <c:if test="${detalle.prestamo.moraTotal == 0}">
                                            <span class="label label-success" style="margin-left: 10px;">Pagada</span>
                                        </c:if>
                                        <c:if test="${detalle.prestamo.moraTotal > 0}">
                                            <span class="label label-danger" style="margin-left: 10px;">Pendiente</span>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </c:if>

                    <!-- Acciones Disponibles -->
                    <div class="row">
                        <div class="col-md-12">
                            <h4 style="margin-bottom: 20px;">Acciones Disponibles</h4>
                            
                            <!-- Aprobar Préstamo -->
                            <c:if test="${detalle.puedeAprobar}">
                                <button type="button" class="btn btn-success" data-toggle="modal" data-target="#modalAprobar" style="margin-right: 10px;">
                                    <span class="glyphicon glyphicon-ok"></span> Aprobar Préstamo
                                </button>
                            </c:if>

                            <!-- Denegar Préstamo -->
                            <c:if test="${detalle.puedeDenegar}">
                                <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalDenegar" style="margin-right: 10px;">
                                    <span class="glyphicon glyphicon-remove"></span> Denegar Préstamo
                                </button>
                            </c:if>

                            <!-- Registrar Devolución -->
                            <c:if test="${detalle.puedeDevolver}">
                                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#modalDevolver" style="margin-right: 10px;">
                                    <span class="glyphicon glyphicon-share-alt"></span> Registrar Devolución
                                </button>
                            </c:if>

                            <!-- Abonar Mora -->
                            <c:if test="${detalle.puedeAbonarMora}">
                                <button type="button" class="btn btn-warning" data-toggle="modal" data-target="#modalAbonarMora">
                                    <span class="glyphicon glyphicon-usd"></span> Abonar Mora
                                </button>
                            </c:if>

                            <!-- Mensaje si no hay acciones disponibles -->
                            <c:if test="${!detalle.puedeAprobar && !detalle.puedeDenegar && !detalle.puedeDevolver && !detalle.puedeAbonarMora}">
                                <div class="alert alert-info">
                                    <span class="glyphicon glyphicon-info-sign"></span> 
                                    No hay acciones disponibles para este préstamo en su estado actual.
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Aprobar Préstamo -->
        <div class="modal fade" id="modalAprobar" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/prestamos.do" method="POST">
                        <input type="hidden" name="op" value="aprobar">
                        <input type="hidden" name="idPrestamo" value="${detalle.prestamo.idPrestamo}">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">
                                <span class="glyphicon glyphicon-ok-sign"></span> Aprobar Préstamo
                            </h4>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-info">
                                <strong>Préstamo #${detalle.prestamo.idPrestamo}</strong>
                            </div>
                            <p><strong>Usuario:</strong> ${detalle.usuario.nombre}</p>
                            <p><strong>Material:</strong> ${detalle.material.titulo}</p>
                            <hr>
                            <div class="form-group">
                                <label for="fechaDevolucionEstimada">Fecha de Devolución Estimada:</label>
                                <input type="date" class="form-control" id="fechaDevolucionEstimada" 
                                       name="fechaEstimada" required>
                                <p class="help-block">Seleccione la fecha en que el usuario debe devolver el material.</p>
                            </div>
                            <p class="text-muted">
                                <small>
                                    <span class="glyphicon glyphicon-info-sign"></span> 
                                    Al aprobar, la fecha de préstamo se establecerá en la fecha actual.
                                </small>
                            </p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success">
                                <span class="glyphicon glyphicon-ok"></span> Aprobar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para Denegar Préstamo -->
        <div class="modal fade" id="modalDenegar" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/prestamos.do" method="POST">
                        <input type="hidden" name="op" value="denegar">
                        <input type="hidden" name="idPrestamo" value="${detalle.prestamo.idPrestamo}">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">
                                <span class="glyphicon glyphicon-remove-sign"></span> Denegar Préstamo
                            </h4>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-warning">
                                <strong>Préstamo #${detalle.prestamo.idPrestamo}</strong>
                            </div>
                            <p><strong>Usuario:</strong> ${detalle.usuario.nombre}</p>
                            <p><strong>Material:</strong> ${detalle.material.titulo}</p>
                            <hr>
                            <p>¿Está seguro que desea denegar este préstamo?</p>
                            <p class="text-danger">
                                <small><strong>Advertencia:</strong> Esta acción no se puede deshacer.</small>
                            </p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-danger">
                                <span class="glyphicon glyphicon-remove"></span> Denegar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para Registrar Devolución -->
        <div class="modal fade" id="modalDevolver" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/prestamos.do" method="POST">
                        <input type="hidden" name="op" value="devolver">
                        <input type="hidden" name="idPrestamo" value="${detalle.prestamo.idPrestamo}">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">
                                <span class="glyphicon glyphicon-share-alt"></span> Registrar Devolución
                            </h4>
                        </div>
                        <div class="modal-body">
                            <div class="alert alert-info">
                                <strong>Préstamo #${detalle.prestamo.idPrestamo}</strong>
                            </div>
                            <p><strong>Usuario:</strong> ${detalle.usuario.nombre}</p>
                            <p><strong>Material:</strong> ${detalle.material.titulo}</p>
                            <hr>
                            <c:if test="${detalle.tieneRetraso}">
                                <div class="alert alert-warning">
                                    <span class="glyphicon glyphicon-alert"></span> 
                                    <strong>Préstamo con retraso</strong><br>
                                    Días de retraso: ${detalle.diasRetraso}<br>
                                    Mora calculada: $${detalle.moraCalculada}
                                </div>
                            </c:if>
                            <p>¿Confirma que el material ha sido devuelto?</p>
                            <p class="text-muted">
                                <small>
                                    <c:choose>
                                        <c:when test="${detalle.tieneRetraso}">
                                            Se registrará la devolución y se aplicará una mora de $${detalle.moraCalculada} por ${detalle.diasRetraso} días de retraso.
                                        </c:when>
                                        <c:otherwise>
                                            Se registrará la devolución del material y estará disponible nuevamente.
                                        </c:otherwise>
                                    </c:choose>
                                </small>
                            </p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-primary">
                                <span class="glyphicon glyphicon-share-alt"></span> Registrar Devolución
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal para Abonar Mora -->
        <div class="modal fade" id="modalAbonarMora" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/prestamos.do" method="POST">
                        <input type="hidden" name="op" value="abonar">
                        <input type="hidden" name="idPrestamo" value="${detalle.prestamo.idPrestamo}">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title">Abonar Mora</h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label>Mora Total: $${detalle.prestamo.moraTotal}</label>
                                <input type="number" class="form-control" name="montoAbono" 
                                       step="0.01" min="0.01" max="${detalle.prestamo.moraTotal}" 
                                       placeholder="Ingrese el monto a abonar" required>
                                <p class="help-block">Ingrese el monto que el usuario está abonando a la mora.</p>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-warning">
                                <span class="glyphicon glyphicon-usd"></span> Abonar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/jquery-1.12.0.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
        <script>
            // Establecer fecha por defecto al abrir el modal de aprobar (5 días desde hoy)
            $('#modalAprobar').on('show.bs.modal', function () {
                var hoy = new Date();
                hoy.setDate(hoy.getDate() + 5); // Agregar 5 días
                
                var año = hoy.getFullYear();
                var mes = String(hoy.getMonth() + 1).padStart(2, '0');
                var dia = String(hoy.getDate()).padStart(2, '0');
                
                var fechaFormateada = año + '-' + mes + '-' + dia;
                $('#fechaDevolucionEstimada').val(fechaFormateada);
                
                // Establecer fecha mínima como mañana
                var mañana = new Date();
                mañana.setDate(mañana.getDate() + 1);
                var minAño = mañana.getFullYear();
                var minMes = String(mañana.getMonth() + 1).padStart(2, '0');
                var minDia = String(mañana.getDate()).padStart(2, '0');
                var fechaMinima = minAño + '-' + minMes + '-' + minDia;
                $('#fechaDevolucionEstimada').attr('min', fechaMinima);
            });
        </script>
    </body>
</html>
