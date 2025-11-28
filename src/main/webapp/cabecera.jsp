<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Sistema de Biblioteca - Colegio Amigos de Don Bosco</title>
<link href="${contextPath}/assets/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<script src="${contextPath}/assets/js/jquery-1.12.0.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/js/bootstrap.min.js"></script>
<script src="${contextPath}/assets/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="${contextPath}/assets/js/dataTables.bootstrap.min.js" type="text/javascript"></script>

<style>
    body {
        margin: 0;
        padding: 0;
        background-color: #f5f5f5;
    }
    .page-header {
        border-bottom: 3px solid #337ab7;
    }
</style>