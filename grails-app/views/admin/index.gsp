<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - Administration</title>
    <asset:javascript src="index.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h1>Administration</h1>
    <div class="row">
        <div class="col-sm-12">
            <g:link action="user">Administrate users</g:link>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <g:link action="policies">Administrate policies</g:link>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <g:link action="approles">Administrate approles (using sukat entitlements)</g:link>
        </div>
    </div>
</body>
</html>