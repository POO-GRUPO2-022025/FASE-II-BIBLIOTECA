<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Editar Otro Documento</title>
        <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        <div class="container" style="margin-top: 20px;">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="page-header">
                        <h3><i class="fa fa-edit"></i> Editar Otro Documento</h3>
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
                        <input type="hidden" name="tipo" value="Otro">
                        <input type="hidden" name="idMaterial" value="${otroDocumento.idMaterial}">
                        
                        <div class="form-group">
                            <label for="titulo" class="col-sm-3 control-label">Título *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="titulo" name="titulo" 
                                       value="${otroDocumento.titulo}" placeholder="Título del documento" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="descripcion" class="col-sm-3 control-label">Descripción *</label>
                            <div class="col-sm-9">
                                <textarea class="form-control" id="descripcion" name="descripcion" 
                                          rows="4" placeholder="Descripción del documento" required>${otroDocumento.descripcion}</textarea>
                                <small class="help-block">Describa el tipo de documento (mapas, folletos, tesis, etc.)</small>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="ubicacion" class="col-sm-3 control-label">Ubicación *</label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" id="ubicacion" name="ubicacion" 
                                       value="${otroDocumento.ubicacion}" placeholder="Ej: Estante D1, Pasillo 5" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadTotal" class="col-sm-3 control-label">Cantidad Total *</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadTotal" name="cantidadTotal" 
                                       value="${otroDocumento.cantidadTotal}" min="0" required>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadDisponible" class="col-sm-3 control-label">Cantidad Disponible</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadDisponible" name="cantidadDisponible" 
                                       value="${otroDocumento.cantidadDisponible}" min="0">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadPrestada" class="col-sm-3 control-label">Cantidad Prestada</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadPrestada" name="cantidadPrestada" 
                                       value="${otroDocumento.cantidadPrestada}" min="0">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="cantidadDaniada" class="col-sm-3 control-label">Cantidad Dañada</label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" id="cantidadDaniada" name="cantidadDaniada" 
                                       value="${otroDocumento.cantidadDaniada}" min="0">
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
