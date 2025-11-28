<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Lista de editoriales</title>
         <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-10 col-md-offset-1">
                <div class="page-header">
                    <h3><i class="fa fa-building"></i> Lista de Editoriales</h3>
                </div>
            
                <a type="button" class="btn btn-primary btn-md" href="${contextPath}/editoriales.do?op=nuevo">
                    <i class="fa fa-plus-circle"></i> Nuevo Editorial
                </a>
                <br><br>
                <table class="table table-striped table-bordered table-hover" id="tabla">
                    <thead class="bg-primary">
                        <tr>
                            <th><i class="fa fa-hashtag"></i> Código</th>
                            <th><i class="fa fa-building"></i> Nombre del Editorial</th>
                            <th><i class="fa fa-globe"></i> País</th>
                            <th><i class="fa fa-cogs"></i> Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.listaEditoriales}" var="editorial">
                         <tr>
                                <td>${editorial.idEditorial}</td>
                                <td>${editorial.nombre}</td>
                                <td>${editorial.pais}</td>
                                <td>
                                    <a class="btn btn-primary" href="${contextPath}/editoriales.do?op=obtener&id=${editorial.idEditorial}"><span class="glyphicon glyphicon-edit"></span> Editar</a>
                                    <a  class="btn btn-danger" href="javascript:eliminar('${editorial.idEditorial}')"><span class="glyphicon glyphicon-trash"></span> Eliminar</a>
                                </td>
                            </tr>
                    </c:forEach>
                    </tbody>
                </table>
                </div>
            </div>                    
        </div>
        
        <!-- Modal de confirmación -->
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
                        <p>¿Realmente desea eliminar este editorial?</p>
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
                    location.href="editoriales.do?op=eliminar&id=" + idEliminar;
                }
            });
        </script>
    </body>
</html>


