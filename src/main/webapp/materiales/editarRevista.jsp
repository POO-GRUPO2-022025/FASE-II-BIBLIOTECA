<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Editar Revista</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="page-header">
                        <h3><i class="fa fa-edit"></i> Editar Revista</h3>
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
                        <input type="hidden" name="op" value="modificar">
                        <input type="hidden" name="tipo" value="Revista">
                        <input type="hidden" name="idMaterial" value="${revista.idMaterial}">
                        
                        <div class="form-group">
                            <label for="titulo" class="col-sm-3 control-label">Título *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="titulo" name="titulo" 
                                       value="${revista.titulo}" placeholder="Título de la revista" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="volumen" class="col-sm-3 control-label">Volumen *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="volumen" name="volumen" 
                                       value="${revista.volumen}" placeholder="Ej: Vol. 5" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="numero" class="col-sm-3 control-label">Número *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="numero" name="numero" 
                                       value="${revista.numero}" placeholder="Ej: No. 12" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="fechaPublicacion" class="col-sm-3 control-label">Fecha de Publicación *</label>
                            <div class="col-sm-9">
                                <input type="date" class="form-control" id="fechaPublicacion" name="fechaPublicacion" 
                                       value="${revista.fechaPublicacion}" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="ubicacion" class="col-sm-3 control-label">Ubicación *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="ubicacion" name="ubicacion" 
                                       value="${revista.ubicacion}" placeholder="Ej: Estante R1, Pasillo 3" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadTotal" class="col-sm-3 control-label">Cantidad Total *</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadTotal" name="cantidadTotal" 
                                       value="${revista.cantidadTotal}" min="0" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadDisponible" class="col-sm-3 control-label">Cantidad Disponible</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadDisponible" name="cantidadDisponible" 
                                       value="${revista.cantidadDisponible}" min="0">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadPrestada" class="col-sm-3 control-label">Cantidad Prestada</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadPrestada" name="cantidadPrestada" 
                                       value="${revista.cantidadPrestada}" min="0">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadDaniada" class="col-sm-3 control-label">Cantidad Dañada</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadDaniada" name="cantidadDaniada" 
                                       value="${revista.cantidadDaniada}" min="0">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-9">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fa fa-save"></i> Guardar Cambios
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
