<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Administration</title>
    <asset:javascript src="index.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h1>Policies</h1>
    <hr/>
    <g:each in="${policies}" var="policy">
        <div class="row">
            <div class="col-sm-2">
                ${policy.policy}
            </div>
            <div class="col-sm-10">
                <pre>${policy.rules}</pre>
            </div>
        </div>
    </g:each>
    <hr/>
    <br/>
    <h1>AppRoles</h1>
    <hr/>
    <g:each in="${approles}" var="approle">
        <div class="row">
            <div class="col-sm-2">
                ${approle.appRole}
            </div>
            <div class="col-sm-10">
                <pre>${approle.policies.join(", ")}</pre>
            </div>
        </div>
    </g:each>
    <hr/>
</body>
</html>