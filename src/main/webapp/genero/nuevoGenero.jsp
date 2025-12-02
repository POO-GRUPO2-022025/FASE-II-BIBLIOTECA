<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Nuevo Género</title> 
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-7 col-md-offset-2">
                    <div class="page-header">
                        <h3><i class="fa fa-plus-circle"></i> Nuevo Género</h3>
                    </div>
                
                    <c:if test="${not empty listaErrores}">
                        <div class="alert alert-danger">
                            <ul>
                                <c:forEach var="error" items="${requestScope.listaErrores}">
                                    <li>${error}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    
                    <form role="form" action="generos.do" method="POST"> 
                        <input type="hidden" id="op" name="op" value="insertar"/>
                        
                        <div class="well well-sm">
                            <strong><span class="glyphicon glyphicon-asterisk"></span>Campos requeridos</strong>
                        </div>
                        
                        <input type="hidden" id="codigo" name="codigo" value="" /> 
                        
                        <div class="form-group">
                            <label for="nombre"><i class="fa fa-tag"></i> Nombre del Género:</label> 
                            <div class="input-group">
                                <input type="text" class="form-control" name="nombre" id="nombre"  
                                       value="${genero.nombre}"  placeholder="Ingresa el nombre del género" >
                                <span class="input-group-addon"><span class="glyphicon glyphicon-asterisk"></span></span>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="descripcion"><i class="fa fa-file-text-o"></i> Descripción:</label> 
                            <div class="input-group">
                                <textarea class="form-control" id="descripcion" name="descripcion" 
                                          placeholder="Ingresa una breve descripción del género">${genero.descripcion}</textarea>
                                <span class="input-group-addon"><span class="glyphicon glyphicon-tag"></span></span>
                            </div>
                        </div>
                        
                        <input type="submit" class="btn btn-success btn-lg" value="Guardar" name="Guardar">
                        <a class="btn btn-default btn-lg" href="generos.do">Cancelar</a>
                    </form>
                </div>
            </div>  
        </div>
    </body>
</html>
