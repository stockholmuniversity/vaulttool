<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Logged out</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Logged out</h3>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-12 bottom-margin-small">
                    <span>You have been logged out.</span>
                </div>
                <div class="col-12">
                    <g:link class="btn btn-primary" controller="dashboard" action="index">Login again</g:link>
                </div>
            </div>
        </div>
    </div>
</body>
</html>