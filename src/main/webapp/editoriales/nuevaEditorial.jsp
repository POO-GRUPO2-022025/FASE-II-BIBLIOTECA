<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Nuevo editorial</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-7 col-md-offset-2">
                <div class="page-header">
                    <h3><i class="fa fa-plus-circle"></i> Nuevo Editorial</h3>
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
                    <form role="form" action="${contextPath}/editoriales.do" method="POST">
                        <input type="hidden"  id="op" name="op" value="insertar"/>
                        <div class="well well-sm"><strong><span class="glyphicon glyphicon-asterisk"></span>Campos requeridos</strong></div>
                        <input type="hidden"  id="codigo" name="codigo" value="${editorial.idEditorial}" />
                        <div class="form-group">
                            <label for="nombre"><i class="fa fa-building"></i> Nombre del Editorial:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" name="nombre" id="nombre"  value="${editorial.nombre}"  placeholder="Ingresa el nombre del editorial" >
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pais"><i class="fa fa-globe"></i> País:</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="pais"  value="${editorial.pais}" name="pais"  placeholder="Ingresa el nombre del país">
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        <input type="submit" class="btn btn-success btn-lg" value="Guardar" name="Guardar">
                        <a class="btn btn-default btn-lg" href="${contextPath}/editoriales.do">Cancelar</a>
                    </form>
                </div>
            </div>  
        </div>
    </body>
</html>

