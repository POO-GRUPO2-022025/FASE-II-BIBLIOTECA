<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Nuevo autor</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-7 col-md-offset-2">
                <div class="page-header">
                    <h3><i class="fa fa-plus-circle"></i> Nuevo Autor</h3>
                </div>
            
                   
                    <c:if test="${not empty listaErrores}">
                    <div class="alert alert-danger">
                        <ul>
                            <c:forEach var="errores"  items="${requestScope.listaErrores}">
                                <li>${errores}</li>
                            </c:forEach>
                        </ul>
                    </div>
                    </c:if>
                    <form role="form" action="${contextPath}/autores.do" method="POST">
                        <input type="hidden"  id="op" name="op" value="insertar"/>
                        <div class="well well-sm"><strong><span class="glyphicon glyphicon-asterisk"></span>Campos requeridos</strong></div>
                        <input type="hidden"  id="codigo" name="codigo" value="${autor.idAutor}" />
                        <div class="form-group">
                            <label for="nombre"><i class="fa fa-building"></i> Nombre del Autor:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" name="nombre" id="nombre"  value="${autor.nombre}"  placeholder="Ingresa el nombre del autor" >
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="apellidos"><i class="fa fa-building"></i> Apellidos del Autor:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" name="apellidos" id="apellidos"  value="${autor.apellidos}"  placeholder="Ingresa los apellidos del autor" >
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pais"><i class="fa fa-globe"></i> País:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="pais"  value="${autor.pais}" name="pais"  placeholder="Ingresa el pais del autor" >
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <input type="submit" class="btn btn-success btn-lg" value="Guardar" name="Guardar">
                        <a class="btn btn-default btn-lg" href="${contextPath}/autores.do">Cancelar</a>
                    </form>
                </div>
            </div>  
        </div>
    </body>
</html>

