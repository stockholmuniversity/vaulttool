<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Unauthorized</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3>Unauthorized</h3>
    </div>
    <div class="panel-body">
        <span>Please contact someone nice who can give you access to ${session.applicationName?:'Vaulttool'}.</span>
    </div>
</div>
</body>
</html>