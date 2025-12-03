<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Edita Usuario</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-7 col-md-offset-2">
                <div class="page-header">
                    <h3><i class="fa fa-edit"></i> Editar Usuario</h3> 
                </div>
                    <c:if test="${not empty listaErrores}">
                    <div class="alert alert-danger">
                        <ul>
                            <c:forEach var="errores" items="${requestScope.listaErrores}">
                                <li>${errores}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    </c:if>
                    
                    <form role="form" action="${contextPath}/usuarios.do" method="POST">
                        <input type="hidden" id="op" name="op" value="modificar"/>
                        <div class="well well-sm"><strong><span class="glyphicon glyphicon-asterisk"></span>Campos requeridos</strong></div>
                        
                        <input type="hidden" name="idUsuario" readonly id="idUsuario" value="${usuario.idUsuario}">
 
                        <div class="form-group">
                            <label for="nombre"><i class="fa fa-user"></i> Nombre del Usuario:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" name="nombre" id="nombre" value="${usuario.nombre}" placeholder="Ingresa el nombre del usuario" required>
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="correo"><i class="fa fa-envelope"></i> Correo:</label>
                            <div class="input-group">
                                <input type="email" class="form-control" id="correo" value="${usuario.correo}" name="correo" placeholder="Ingresa el correo del usuario" required>
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="password"><i class="fa fa-lock"></i> Contraseña (Dejar vacío para no cambiar):</label>
                            <div class="input-group">
                                <%-- Debe ser tipo password y sin el valor del bean, pues es el hash --%>
                                <input type="password" class="form-control" id="password" name="password" placeholder="Ingresa nueva contraseña (opcional)">
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                   
                        <div class="form-group">
                            <label for="tipoUsuario"><i class="fa fa-user-circle"></i> Tipo de Usuario:</label>
                            <div class="input-group">
                                
                                <select class="form-control" name="tipoUsuario" id="tipoUsuario" required>
                                    <option value="">Seleccione un Tipo</option>
                                    
                                    <c:forEach items="${tiposUsuario}" var="tipo">
                                        
                                        <option value="${tipo}"
                                            <%-- Lógica para dejar seleccionado el valor actual del usuario --%>
                                            <c:if test="${usuario.tipoUsuario == tipo}">
                                                selected
                                            </c:if>
                                        >
                                            ${tipo}
                                        </option>
                                        
                                    </c:forEach>
                                </select>
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        
                        <input type="submit" class="btn btn-success btn-lg" value="Guardar Cambios" name="Guardar">
                        <a class="btn btn-default btn-lg" href="${contextPath}/usuarios.do">Cancelar</a>
                    </form>
                </div>
            </div>  
        </div>
    </body>
</html>
