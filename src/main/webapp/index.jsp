<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file='/cabecera.jsp' %>
    </head>
    <body>
        <jsp:include page="/menu.jsp"/>
        
        <div class="container">
            <div class="jumbotron text-center">
                <h1><i class="fa fa-graduation-cap"></i> Colegio Amigos de Don Bosco</h1>
                <p>Administración de recursos bibliográficos</p>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="panel panel-primary">
                        <div class="panel-heading text-center">
                            <h3 class="panel-title"><i class="fa fa-book fa-2x"></i></h3>
                        </div>
                        <div class="panel-body text-center">
                            <h4>Gestión de Materiales</h4>
                            <p>Control completo de libros, revistas, audiovisuales y otros documentos de la biblioteca</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="panel panel-success">
                        <div class="panel-heading text-center">
                            <h3 class="panel-title"><i class="fa fa-exchange fa-2x"></i></h3>
                        </div>
                        <div class="panel-body text-center">
                            <h4>Control de Préstamos</h4>
                            <p>Sistema automatizado para gestionar préstamos, devoluciones y renovaciones de materiales</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="panel panel-info">
                        <div class="panel-heading text-center">
                            <h3 class="panel-title"><i class="fa fa-users fa-2x"></i></h3>
                        </div>
                        <div class="panel-body text-center">
                            <h4>Administración de Usuarios</h4>
                            <p>Gestión eficiente de estudiantes, profesores y personal del colegio</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-4">
                    <div class="panel panel-warning">
                        <div class="panel-heading text-center">
                            <h3 class="panel-title"><i class="fa fa-building fa-2x"></i></h3>
                        </div>
                        <div class="panel-body text-center">
                            <h4>Editoriales y Autores</h4>
                            <p>Catálogo completo de editoriales y autores para mejor organización</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="panel panel-danger">
                        <div class="panel-heading text-center">
                            <h3 class="panel-title"><i class="fa fa-clock-o fa-2x"></i></h3>
                        </div>
                        <div class="panel-body text-center">
                            <h4>Control de Moras</h4>
                            <p>Seguimiento automático de retrasos y cálculo de multas por devoluciones tardías</p>
                        </div>
                    </div>
                </div>
            </div>
    </body>
</html>
