<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Logged out</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<h1>VAULTTOOL - Logged out</h1>
<span>You have been logged out.</span>
<br/>
<g:link controller="dashboard" action="index">Login again</g:link>
</body>
</html>