<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Upload file</title>

    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Script-Type" content="text/javascript" />
    <meta http-equiv="Content-Style-Type" content="text/css" />

    <spring:url value="/resources/css/app.css" var="coreCss" />
    <spring:url value="/resources/css/bootstrap.css" var="bootstrapCss" />
    <spring:url value="/resources/css/bootstrap-theme.css" var="bootstrapThemeCss" />

    <link href="${bootstrapCss}" rel="stylesheet" />
    <link href="${coreCss}" rel="stylesheet" />
    <link href="${bootstrapThemeCss}" rel="stylesheet" />
</head>

<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="<c:url value='/' />">GCP demo project by spring 4 MVC with Gradle</a>
        </div>
    </div>
</nav>

<div class="space-30" ></div>

<div class="container">

    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <h3>Upload file sample</h3>
        </div>
    </div>

    <div class="space-15" ></div>

    <div class="row">
        <div class="col-md-8 col-md-offset-2">

            <form method="post" action="<c:url value='/upload' />" enctype="multipart/form-data">
              <div class="form-group">
                <label for="uploadFile">Select file to upload</label>
                <input type="file" id="uploadFile" name="uploadFile" />

                <p class="help-block">Upload some file for test.</p>
              </div>

              <div class="form-group">
                <label for="comment">Comment for this file</label>
                <input type="text" class="form-control" id="comment" name="comment" placeholder="Comment for this file." />
              </div>

              <input type="submit" class="btn btn-default" value="Submit" />
            </form>

        </div>
    </div>

    <hr>

    <footer>
        <p>&copy; SJI Inc. 2015</p>
    </footer>
</div>

<spring:url value="/resources/js/jquery-2.1.4.js" var="jqueryJs" />
<spring:url value="/resources/js/bootstrap.js" var="bootstrapJs" />
<spring:url value="/resources/js/app.js" var="coreJs" />

<script src="${jqueryJs}"></script>
<script src="${bootstrapJs}"></script>
<script src="${coreJs}"></script>

</body>
</html>
