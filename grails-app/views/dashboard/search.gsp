<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Search Results</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<h3 class="bottom-margin-medium" style="color: #002e5f;">Search result</h3>
<g:form action="index">

        <g:each in="${metadatas}" var="metadata">
            <div class="row pointer">
                <div class="col-sm-12">
                    <strong><g:link action="secret" params='[key: "${metadata.secretKey}"]'>${metadata.secretKey} - ${metadata?.title?:""}</g:link></strong>
                </div>
            </div>
            <hr class="top-margin-small bottom-margin-small">
        </g:each>

    <g:submitButton class="btn btn-default pull-right" name="Cancel" value="Cancel"/>
</g:form>
</body>
</html>