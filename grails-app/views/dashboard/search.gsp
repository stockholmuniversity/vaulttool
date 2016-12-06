<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - Search Results</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<g:form action="index">
    <ul>
        <g:each in="${metadatas}" var="metadata">
            <li><g:link action="secret" params='[key: "${metadata.secretKey}"]'>${metadata.secretKey} (${metadata?.title?:""})</g:link></li>
        </g:each>
    </ul>
    <g:submitButton class="btn btn-warning pull-right" name="Cancel" value="Cancel"/>
</g:form>
</body>
</html>