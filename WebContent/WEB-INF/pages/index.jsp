<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- Material Design Lite -->
<link rel="stylesheet" href="./resources/material.min.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<script src="./resources/material.min.js"></script>
<!-- CodeMirror -->
<script src="./resources/codemirror.js"></script>
<link rel="stylesheet" href="./resources/css_docs.css">
<!-- JQuery -->
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<!-- MUI framework design-->
<script src="./resources/mui.js"></script>
<link rel="stylesheet" href="./resources/mui.css">
<!-- Custom design, AJAX requests -->
<link rel="stylesheet" href="./resources/custom.css">
<script src="./resources/custom.js"></script>
<!-- Timer -->
<link rel="stylesheet" href="./resources/bootstrap.min.css">
<script src="./resources/jquery.timer.js"></script>
<script src="./resources/counter.js"></script>
<title>Oracle Query request</title>
</head>
<body onload="Counter.resetStopwatch();hide();">

<div id="sidedrawer" class="mui--no-user-select" style="padding-top: 7px; padding-left: 7px;">
	<c:forEach var="item" items="${list}" >
		<a href='<c:out value='${item.link}'></c:out>'>${item.name}</a><br/>
	</c:forEach>
</div>
<header id="header">
  <div class="mui-appbar mui--appbar-line-height">
    <div class="mui-container-fluid" style="padding-top: 12px;">
      <a class="sidedrawer-toggle mui--hidden-xs mui--hidden-sm js-hide-sidedrawer">&#10148; samples</a>
    </div>
  </div>
</header>

<div id="content-wrapper"> 
	<form id="ajaxRequestForm" name = "request" action="./do" method="POST">
		<div style="border-top: 1px solid black; border-bottom: 1px solid black;">
			<textarea cols="10" rows="30" id="code" name="code" placeholder="SQL request">${sql}</textarea>
		</div>
		<br>
		<button class="mui-btn mui-btn--raised mui-btn--primary" id="inquiry" style="margin-top: -10px;">Inquiry</button>
		<button class="mui-btn mui-btn--raised mui-btn--primary" id="save" style="margin-top: -10px;">Save</button>
		<span id="stopwatch" style="margin-left: 25px; ">00:00:00</span>
	</form>
	<div class="mui--text-dark mui--text-subhead">
        <div id="ajaxResponse"></div> 
	</div>
</div> 
<br>

<!-- sql highlighting -->  
<script type="text/javascript">
  var editor = CodeMirror.fromTextArea('code', {
    height: "250px",
    parserfile: "./contrib_sql_js_parsesql.js",
    stylesheet: "./resources/css_sqlcolors.css",
    path: "./resources/js/",
    textWrapping: false
  });
</script>
</body>
</html>