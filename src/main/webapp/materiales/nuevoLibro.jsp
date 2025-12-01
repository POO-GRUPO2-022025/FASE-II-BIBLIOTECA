<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Nuevo Libro</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="page-header">
                        <h3><i class="fa fa-book"></i> Agregar Nuevo Libro</h3>
                    </div>
                    
                    <!-- Mostrar errores si existen -->
                    <c:if test="${not empty listaErrores}">
                        <div class="alert alert-danger alert-dismissible" role="alert">
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <strong>¡Error!</strong> Por favor corrija los siguientes errores:
                            <ul>
                                <c:forEach items="${listaErrores}" var="error">
                                    <li>${error}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    
                    <form action="${contextPath}/materiales.do" method="POST" class="form-horizontal">
                        <input type="hidden" name="op" value="insertar">
                        <input type="hidden" name="tipo" value="Libro">
                        
                        <div class="form-group">
                            <label for="titulo" class="col-sm-3 control-label">Título *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="titulo" name="titulo" 
                                       value="${titulo}" placeholder="Título del libro" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="isbn" class="col-sm-3 control-label">ISBN *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="isbn" name="isbn" 
                                       value="${isbn}" placeholder="ISBN del libro" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="editorial" class="col-sm-3 control-label">Editorial *</label>
                            <div class="col-sm-9">
                                <select class="form-control" id="editorial" name="editorial" required>
                                    <option value="">-- Seleccione una editorial --</option>
                                    <c:forEach items="${listaEditoriales}" var="editorial">
                                        <option value="${editorial.idEditorial}" 
                                            ${editorial.idEditorial == idEditorial ? 'selected' : ''}>
                                            ${editorial.nombre}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label class="col-sm-3 control-label">Autores *</label>
                            <div class="col-sm-9">
                                <div style="max-height: 150px; overflow-y: auto; border: 1px solid #ccc; padding: 10px; border-radius: 4px;">
                                    <c:forEach items="${listaAutores}" var="autor">
                                        <div class="checkbox">
                                            <label>
                                                <input type="checkbox" name="autores" value="${autor.idAutor}">
                                                ${autor.nombre} ${autor.apellidos} (${autor.pais})
                                            </label>
                                        </div>
                                    </c:forEach>
                                </div>
                                <small class="help-block">Seleccione uno o más autores</small>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="ubicacion" class="col-sm-3 control-label">Ubicación *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="ubicacion" name="ubicacion" 
                                       value="${ubicacion}" placeholder="Ej: Estante A3, Pasillo 2" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadTotal" class="col-sm-3 control-label">Cantidad Total *</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadTotal" name="cantidadTotal" 
                                       value="${cantidadTotal > 0 ? cantidadTotal : ''}" min="0" placeholder="0" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fa fa-save"></i> Guardar
                                </button>
                                <a href="${contextPath}/materiales.do" class="btn btn-default">
                                    <i class="fa fa-times"></i> Cancelar
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
