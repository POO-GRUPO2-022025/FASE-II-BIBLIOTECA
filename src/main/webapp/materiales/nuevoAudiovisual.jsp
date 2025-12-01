<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Nuevo Audiovisual</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="page-header">
                        <h3><i class="fa fa-film"></i> Agregar Nuevo Material Audiovisual</h3>
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
                        <input type="hidden" name="tipo" value="Audiovisual">
                        
                        <div class="form-group">
                            <label for="titulo" class="col-sm-3 control-label">Título *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="titulo" name="titulo" 
                                       value="${titulo}" placeholder="Título del material audiovisual" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="formato" class="col-sm-3 control-label">Formato *</label>
                            <div class="col-sm-9">
                                <select class="form-control" id="formato" name="formato" required>
                                    <option value="">-- Seleccione un formato --</option>
                                    <option value="DVD" ${formato == 'DVD' ? 'selected' : ''}>DVD</option>
                                    <option value="Blu-ray" ${formato == 'Blu-ray' ? 'selected' : ''}>Blu-ray</option>
                                    <option value="CD" ${formato == 'CD' ? 'selected' : ''}>CD</option>
                                    <option value="VHS" ${formato == 'VHS' ? 'selected' : ''}>VHS</option>
                                    <option value="Digital" ${formato == 'Digital' ? 'selected' : ''}>Digital</option>
                                    <option value="Otro" ${formato == 'Otro' ? 'selected' : ''}>Otro</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="duracion" class="col-sm-3 control-label">Duración (minutos) *</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="duracion" name="duracion" 
                                       value="${duracion > 0 ? duracion : ''}" min="0" placeholder="0" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="ubicacion" class="col-sm-3 control-label">Ubicación *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="ubicacion" name="ubicacion" 
                                       value="${ubicacion}" placeholder="Ej: Estante AV1, Pasillo 4" required>
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
