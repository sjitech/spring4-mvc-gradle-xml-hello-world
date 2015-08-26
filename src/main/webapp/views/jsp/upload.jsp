<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Upload file</title>

<spring:url value="/resources/css/hello.css" var="coreCss" />
<spring:url value="/resources/css/bootstrap.css" var="bootstrapCss" />
<spring:url value="/resources/css/bootstrap-theme.css" var="bootstrapThemeCss" />

<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${coreCss}" rel="stylesheet" />
<link href="${bootstrapThemeCss}" rel="stylesheet" />

</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Upload file sample</a>
        </div>
    </div>
</nav>


<div class="container">

    <div class="row">
        <div class="col-md-12">

            <form method="post" action="upload" enctype="multipart/form-data">
              <div class="form-group">
                <label for="uploadFile">Select file to upload</label>
                <input type="file" id="uploadFile" name="uploadFile" />

                <p class="help-block">Upload some file for test.</p>
              </div>

              <div class="form-group">
                <label for="comment">Comment for this file</label>
                <input type="text" class="form-control" id="comment" name="comment" placeholder="Comment for this file." />
              </div>

              <button type="submit" class="btn btn-default">Submit</button>
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
<spring:url value="/resources/js/hello.js" var="coreJs" />

<script src="${jqueryJs}"></script>
<script src="${bootstrapJs}"></script>
<script src="${coreJs}"></script>

</body>
</html>
