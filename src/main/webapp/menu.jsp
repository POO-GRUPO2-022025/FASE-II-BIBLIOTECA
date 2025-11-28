<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<style>
    .navbar-inverse {
        background-color: #337ab7;
        border-color: #2e6da4;
    }
    .navbar-inverse .navbar-brand {
        color: #fff;
        font-weight: bold;
    }
    .navbar-inverse .navbar-nav > li > a {
        color: #fff;
    }
    .navbar-inverse .navbar-nav > li > a:hover {
        background-color: #286090;
    }
</style>

<nav class="navbar navbar-inverse navbar-static-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" 
                    data-toggle="collapse" data-target="#navbar" 
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Desplegar navegación</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">
                <i class="fa fa-book"></i> Colegio Amigos de Don Bosco
            </a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="${contextPath}/index.do"><i class="fa fa-home"></i> Inicio</a></li>
                <%-- <c:if test="${tipoUsuario == 'ADMIN'}"> --%>
                  <li><a href="${contextPath}/editoriales.do"><i class="fa fa-building"></i> Editoriales</a></li>
                  <li><a href="${contextPath}/autores.do"><i class="fa fa-users"></i> Autores</a></li>
                  <li><a href="${contextPath}/generos.do"><i class="fa fa-tags"></i> Géneros</a></li>
                  <li><a href="${contextPath}/materiales.do"><i class="fa fa-archive"></i> Materiales</a></li>
                  <li><a href="${contextPath}/usuarios.do"><i class="fa fa-user"></i> Usuarios</a></li>
                  <li><a href="${contextPath}/moras.do"><i class="fa fa-clock-o"></i> Moras</a></li>
                  <li><a href="${contextPath}/tarifas.do"><i class="fa fa-dollar"></i> Tarifas</a></li>
                  <li><a href="${contextPath}/admin_prestamos.do"><i class="fa fa-exchange"></i> Préstamos</a></li>
                <%-- </c:if> --%>
                <c:if test="${tipoUsuario == 'profesor' || tipoUsuario == 'estudiante'}">
                  <li><a href="${contextPath}/prestamos.do"><i class="fa fa-exchange"></i> Préstamos</a></li>
                </c:if>
                <c:if test="${not empty tipoUsuario}">
                  <li><a href="${contextPath}/logout.do"><i class="fa fa-sign-out"></i> Cerrar sesión</a></li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>
        
