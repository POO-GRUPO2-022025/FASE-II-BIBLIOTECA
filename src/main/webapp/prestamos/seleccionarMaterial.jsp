<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Seleccionar Material para Préstamo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dataTables.bootstrap.min.css"/>
    <style>
        .filter-section { 
            margin-bottom: 20px; 
            padding: 20px; 
            background-color: #f5f5f5; 
            border-radius: 5px; 
        }
        .material-badge {
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
        }
        .badge-libro { background-color: #5bc0de; color: white; }
        .badge-revista { background-color: #f0ad4e; color: white; }
        .badge-audiovisual { background-color: #5cb85c; color: white; }
        .badge-otro { background-color: #777; color: white; }
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
                            <span class="glyphicon glyphicon-book"></span> Seleccionar Material para Préstamo
                        </h3>
                    </div>
                    <div class="panel-body">
                        
                        <!-- Botón para regresar -->
                        <div style="margin-bottom: 15px;">
                            <a href="${pageContext.request.contextPath}/prestamos.do?op=listar" class="btn btn-default">
                                <span class="glyphicon glyphicon-arrow-left"></span> Regresar a Mis Préstamos
                            </a>
                        </div>

                        <!-- Sección de filtros -->
                        <div class="filter-section">
                            <form method="get" action="${pageContext.request.contextPath}/prestamos.do" id="formBuscar">
                                <input type="hidden" name="op" value="buscarMaterial">
                                <div class="row">
                                    <div class="col-md-5">
                                        <div class="form-group">
                                            <label>Buscar por título:</label>
                                            <input type="text" name="titulo" class="form-control" 
                                                   placeholder="Ingrese el título del material" 
                                                   value="${param.titulo}">
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="form-group">
                                            <label>Tipo de material:</label>
                                            <select name="tipo" class="form-control">
                                                <option value="">-- Todos --</option>
                                                <option value="Libro" ${param.tipo == 'Libro' ? 'selected' : ''}>Libro</option>
                                                <option value="Revista" ${param.tipo == 'Revista' ? 'selected' : ''}>Revista</option>
                                                <option value="Audiovisual" ${param.tipo == 'Audiovisual' ? 'selected' : ''}>Audiovisual</option>
                                                <option value="Otro_Documento" ${param.tipo == 'Otro_Documento' ? 'selected' : ''}>Otro Documento</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <label>&nbsp;</label>
                                        <button type="submit" class="btn btn-primary btn-block">
                                            <span class="glyphicon glyphicon-search"></span> Buscar
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>

                        <!-- Tabla de materiales -->
                        <c:choose>
                            <c:when test="${not empty materiales}">
                                <div class="alert alert-info">
                                    <strong>Resultados:</strong> Se encontraron ${materiales.size()} materiales.
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-striped table-bordered">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Tipo</th>
                                                <th>Título</th>
                                                <th>Ubicación</th>
                                                <th>Disponibles</th>
                                                <th>Acción</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${materiales}" var="material">
                                                <tr>
                                                    <td>${material.idMaterial}</td>
                                                    <td>
                                                        <span class="material-badge badge-${material.tipoMaterial == 'Libro' ? 'libro' : material.tipoMaterial == 'Revista' ? 'revista' : material.tipoMaterial == 'Audiovisual' ? 'audiovisual' : 'otro'}">
                                                            ${material.tipoMaterial}
                                                        </span>
                                                    </td>
                                                    <td><strong>${material.titulo}</strong></td>
                                                    <td>${material.ubicacion}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${material.cantidadDisponible > 0}">
                                                                <span class="badge badge-success" style="background-color: #5cb85c;">
                                                                    ${material.cantidadDisponible}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge badge-danger" style="background-color: #d9534f;">
                                                                    No disponible
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${material.cantidadDisponible > 0}">
                                                                <form method="post" action="${pageContext.request.contextPath}/prestamos.do" style="display: inline;">
                                                                    <input type="hidden" name="op" value="solicitar">
                                                                    <input type="hidden" name="idMaterial" value="${material.idMaterial}">
                                                                    <button type="submit" class="btn btn-success btn-sm">
                                                                        <span class="glyphicon glyphicon-ok"></span> Solicitar
                                                                    </button>
                                                                </form>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button type="button" class="btn btn-default btn-sm" disabled>
                                                                    <span class="glyphicon glyphicon-ban-circle"></span> No disponible
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:when test="${param.titulo != null || param.tipo != null}">
                                <div class="alert alert-warning">
                                    <strong>Sin resultados:</strong> No se encontraron materiales con los criterios especificados.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info">
                                    <strong>Información:</strong> Utilice los filtros de búsqueda para encontrar materiales disponibles.
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/assets/js/jquery-1.12.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
</body>
</html>
