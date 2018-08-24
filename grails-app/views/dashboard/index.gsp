<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - List</title>
    <asset:javascript src="inputrules.js"/>
    <asset:javascript src="secret.js"/>
    <asset:javascript src="navigation.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<div id="dashboard" class="bottom-margin-xlarge">
    <g:render template="overview" model="[selectedPath: selectedPath, capabilities: capabilities, paths: paths, secrets: secrets]"/>
</div>

</body>
</html>