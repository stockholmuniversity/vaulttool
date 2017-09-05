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
            <g:form action="sudo">
                <label for="sudo">Sudo</label>
                <g:select from="${approles*.appRole}" id="sudo" name="sudo" noSelection="${['':'Select group']}"/>
                <g:submitButton class="btn btn-primary" name="submit" value="Sudo now"/>
            </g:form>
        </div>
    </div>
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
    <div class="row">
        <div class="col-sm-12">
            <g:link action="export">Export secrets and users</g:link>
        </div>
    </div>
</body>
</html>