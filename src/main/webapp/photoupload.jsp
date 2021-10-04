<%@ page language="java" pageEncoding="UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Upload</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
    <table class="table">
        <thead>
        <tr>
            <th>Фото кандидата</th>
        </tr>
        </thead>
        <tbody>
        <tr valign="top">
            <td>
                <img src="<c:url value='/photodownload?id=${candidate.id}'/>" width="100px" height="100px"/>
                <a href="<c:url value='/photodownload?id=${candidate.id}'/>">Загрузить</a>
            </td>
        </tr>
        </tbody>
    </table>
    <h2>Загрузить новое фото</h2>
    <form action="<c:url value='/photoupload?id=${candidate.id}'/>" method="post" enctype="multipart/form-data">
        <div class="checkbox">
            <input type="file" name="file">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
    </form>
</div>

</body>
</html>