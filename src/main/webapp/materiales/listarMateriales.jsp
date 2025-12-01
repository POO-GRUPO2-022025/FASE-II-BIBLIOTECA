<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Lista de materiales</title>
         <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-12">
                <div class="page-header">
                    <h3><i class="fa fa-book"></i> Lista de materiales - ${tipoFiltro}</h3>
                </div>
            
                <div class="row" style="margin-bottom: 20px;">
                    <div class="col-md-6">
                        <!-- Botón para nuevo material con modal -->
                        <button type="button" class="btn btn-primary btn-md" data-toggle="modal" data-target="#modalTipoMaterial">
                            <i class="fa fa-plus-circle"></i> Nuevo Material
                        </button>
                    </div>
                    <div class="col-md-6">
                        <!-- Filtro por tipo -->
                        <form method="GET" action="${contextPath}/materiales.do" class="form-inline pull-right">
                            <input type="hidden" name="op" value="filtrar">
                            <div class="form-group">
                                <label for="tipo">Filtrar por tipo: &nbsp;</label>
                                <select name="tipo" id="tipo" class="form-control" onchange="this.form.submit()">
                                    <option value="Todos" ${tipoFiltro == 'Todos' ? 'selected' : ''}>Todos</option>
                                    <option value="Libro" ${tipoFiltro == 'Libro' ? 'selected' : ''}>Libros</option>
                                    <option value="Revista" ${tipoFiltro == 'Revista' ? 'selected' : ''}>Revistas</option>
                                    <option value="Audiovisual" ${tipoFiltro == 'Audiovisual' ? 'selected' : ''}>Audiovisuales</option>
                                    <option value="Otro" ${tipoFiltro == 'Otro' ? 'selected' : ''}>Otros</option>
                                </select>
                            </div>
                        </form>
                    </div>
                </div>
                
                <table class="table table-striped table-bordered table-hover" id="tabla">
                    <thead class="bg-primary">
                        <tr>
                            <th><i class="fa fa-hashtag"></i> ID</th>
                            <th><i class="fa fa-tag"></i> Tipo</th>
                            <th><i class="fa fa-book"></i> Título</th>
                            <th><i class="fa fa-map-marker"></i> Ubicación</th>
                            <th><i class="fa fa-list-ol"></i> Total</th>
                            <th><i class="fa fa-check-circle"></i> Disponibles</th>
                            <th><i class="fa fa-hand-paper-o"></i> Prestados</th>
                            <th><i class="fa fa-exclamation-triangle"></i> Dañados</th>
                            <th><i class="fa fa-cogs"></i> Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.listaMateriales}" var="material">
                         <tr>
                                <td>${material.idMaterial}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${material.tipoMaterial == 'Libro'}">
                                            <span class="label label-primary">Libro</span>
                                        </c:when>
                                        <c:when test="${material.tipoMaterial == 'Revista'}">
                                            <span class="label label-info">Revista</span>
                                        </c:when>
                                        <c:when test="${material.tipoMaterial == 'Audiovisual'}">
                                            <span class="label label-warning">Audiovisual</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="label label-default">Otro</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${material.titulo}</td>
                                <td>${material.ubicacion}</td>
                                <td>${material.cantidadTotal}</td>
                                <td>${material.cantidadDisponible}</td>
                                <td>${material.cantidadPrestada}</td>
                                <td>${material.cantidadDaniada}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${material.tipoMaterial == 'Libro'}">
                                            <a class="btn btn-primary btn-sm" href="${contextPath}/materiales.do?op=obtener&tipo=Libro&id=${material.idMaterial}">
                                                <span class="glyphicon glyphicon-edit"></span> Editar
                                            </a>
                                        </c:when>
                                        <c:when test="${material.tipoMaterial == 'Revista'}">
                                            <a class="btn btn-primary btn-sm" href="${contextPath}/materiales.do?op=obtener&tipo=Revista&id=${material.idMaterial}">
                                                <span class="glyphicon glyphicon-edit"></span> Editar
                                            </a>
                                        </c:when>
                                        <c:when test="${material.tipoMaterial == 'Audiovisual'}">
                                            <a class="btn btn-primary btn-sm" href="${contextPath}/materiales.do?op=obtener&tipo=Audiovisual&id=${material.idMaterial}">
                                                <span class="glyphicon glyphicon-edit"></span> Editar
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="btn btn-primary btn-sm" href="${contextPath}/materiales.do?op=obtener&tipo=Otro&id=${material.idMaterial}">
                                                <span class="glyphicon glyphicon-edit"></span> Editar
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                    <a class="btn btn-danger btn-sm" href="javascript:eliminar('${material.idMaterial}')">
                                        <span class="glyphicon glyphicon-trash"></span> Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                </div>
            </div>                    
        </div>
        
        <!-- Modal para seleccionar tipo de material -->
        <div class="modal fade" id="modalTipoMaterial" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><i class="fa fa-plus-circle"></i> Seleccione el tipo de material</h4>
                    </div>
                    <div class="modal-body">
                        <p>Seleccione el tipo de material que desea crear:</p>
                        <div class="list-group">
                            <a href="${contextPath}/materiales.do?op=nuevo&tipo=Libro" class="list-group-item">
                                <h4 class="list-group-item-heading">
                                    <i class="fa fa-book"></i> Libro
                                </h4>
                                <p class="list-group-item-text">Agregar un nuevo libro a la biblioteca</p>
                            </a>
                            <a href="${contextPath}/materiales.do?op=nuevo&tipo=Revista" class="list-group-item">
                                <h4 class="list-group-item-heading">
                                    <i class="fa fa-newspaper-o"></i> Revista
                                </h4>
                                <p class="list-group-item-text">Agregar una nueva revista a la biblioteca</p>
                            </a>
                            <a href="${contextPath}/materiales.do?op=nuevo&tipo=Audiovisual" class="list-group-item">
                                <h4 class="list-group-item-heading">
                                    <i class="fa fa-film"></i> Audiovisual
                                </h4>
                                <p class="list-group-item-text">Agregar material audiovisual (DVD, CD, etc.)</p>
                            </a>
                            <a href="${contextPath}/materiales.do?op=nuevo&tipo=Otro" class="list-group-item">
                                <h4 class="list-group-item-heading">
                                    <i class="fa fa-file-text-o"></i> Otro Documento
                                </h4>
                                <p class="list-group-item-text">Agregar otro tipo de documento</p>
                            </a>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Modal de confirmación para eliminar -->
        <div class="modal fade" id="modalEliminar" tabindex="-1" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title">Confirmar eliminación</h4>
                    </div>
                    <div class="modal-body">
                        <p>¿Realmente desea eliminar este material?</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" id="btnConfirmarEliminar">Eliminar</button>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Alerta Template (hidden) -->
        <div id="alertTemplate" class="alert alert-dismissible" role="alert" style="display: none; position: fixed; top: 70px; right: 20px; z-index: 9999; min-width: 300px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
            <span id="alertMessage"></span>
        </div>
        
        <script>
            var idEliminar = null;
            
            $(document).ready(function(){
               $('#tabla').DataTable(); 
               
               // Mostrar alertas con Bootstrap
               <c:if test="${not empty exito}">
                   mostrarAlerta('success', '${exito}');
                   <c:set var="exito" value="" scope="session" />
               </c:if>
               <c:if test="${not empty fracaso}">
                   mostrarAlerta('danger', '${fracaso}');
                   <c:set var="fracaso" value="" scope="session" />
               </c:if>
            });
            
            function mostrarAlerta(tipo, mensaje){
                var $alert = $('#alertTemplate').clone();
                $alert.removeAttr('id');
                $alert.removeClass('alert-success alert-danger alert-warning alert-info');
                $alert.addClass('alert-' + tipo);
                $alert.find('#alertMessage').text(mensaje);
                $alert.show();
                
                $('body').append($alert);
                
                // Auto-cerrar después de 3 segundos
                setTimeout(function(){
                    $alert.fadeOut('slow', function(){
                        $(this).remove();
                    });
                }, 3000);
            }
            
            function eliminar(id){
                idEliminar = id;
                $('#modalEliminar').modal('show');
            }
            
            $('#btnConfirmarEliminar').click(function(){
                if(idEliminar){
                    location.href="materiales.do?op=eliminar&id=" + idEliminar;
                }
            });
        </script>
    </body>
</html>
