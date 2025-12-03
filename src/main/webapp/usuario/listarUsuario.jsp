<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Lista de Usuarios</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-10 col-md-offset-1">
                <div class="page-header">
                    <h3><i class="fa fa-users"></i> Lista de Usuarios</h3>
                </div>
           
                
                <a type="button" class="btn btn-primary btn-md" href="${contextPath}/usuarios.do?op=nuevo">
                    <i class="fa fa-plus-circle"></i> Nuevo Usuario
                </a>
                <br><br>
                
                <table class="table table-striped table-bordered table-hover" id="tabla">
                    <thead class="bg-primary">
                        <tr>
                            <th><i class="fa fa-hashtag"></i> Código</th>
                            <th><i class="fa fa-user"></i> Nombre</th>
                            <th><i class="fa fa-envelope"></i> Correo</th>
                            <th><i class="fa fa-user-circle"></i> Tipo de Usuario</th> 
                            <th><i class="fa fa-cogs"></i> Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${requestScope.listaUsuarios}" var="usuario">
                            <tr>
                                <td>${usuario.idUsuario}</td>
                                <td>${usuario.nombre}</td>
                                <td>${usuario.correo}</td>
                                <td>${usuario.tipoUsuario}</td> 
                                
                                <td>
                                    <a class="btn btn-primary" href="${contextPath}/usuarios.do?op=obtener&id=${usuario.idUsuario}"><span class="glyphicon glyphicon-edit"></span> Editar</a>
                                   
                                    <a class="btn btn-danger" href="javascript:confirmarEliminarUsuario('${usuario.idUsuario}')"><span class="glyphicon glyphicon-trash"></span> Eliminar</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                </div>
            </div>      
        </div>
        
        
        <div class="modal fade" id="modalEliminarUsuario" tabindex="-1" role="dialog" aria-labelledby="modalEliminarUsuarioLabel">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="modalEliminarUsuarioLabel">Confirmar Eliminación de Usuario</h4>
                    </div>
                    <div class="modal-body">
                        <p>¿Realmente desea eliminar este usuario? Esta acción es irreversible.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                
                        <button type="button" class="btn btn-danger" id="btnConfirmarEliminarUsuario">Eliminar</button> 
                    </div>
                </div>
            </div>
        </div>
        
        <script>
            var idUsuarioEliminar = null; 
            $(document).ready(function(){
                
                $('#tabla').DataTable({
                     "language": {
                        "url": "//cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json" 
                    }
                }); 
                
              
                <c:if test="${not empty exito}">
                    mostrarAlerta('success', '${exito}');
                    <c:set var="exito" value="" scope="session" />
                </c:if>
                <c:if test="${not empty fracaso}">
                    mostrarAlerta('danger', '${fracaso}');
                    <c:set var="fracaso" value="" scope="session" />
                </c:if>
            });
            
            
            function confirmarEliminarUsuario(id){
                idUsuarioEliminar = id;
                $('#modalEliminarUsuario').modal('show'); 
            }
            
          
            $('#btnConfirmarEliminarUsuario').click(function(){ 
                if(idUsuarioEliminar){
                    // CORRECCIÓN: Apunta a usuarios.do, no a generos.do
                    location.href="${contextPath}/usuarios.do?op=eliminar&id=" + idUsuarioEliminar;
                }
                $('#modalEliminarUsuario').modal('hide');
            });
            
            
        </script>
    </body>
</html>

