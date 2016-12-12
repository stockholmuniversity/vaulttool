<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - List</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <table class="table" style="border: none;">
        <tr>
            <td><g:form action="index"><g:select onchange="submit();" name="selectedPath" from="${paths}" value="${selectedPath}" noSelection="${['':'Root']}"/></g:form></td>
            <td><i>(${capabilities.join(", ")})</i></td>
            <g:form action="createSecret">
                <g:hiddenField name="selectedPath" value="${selectedPath}"/>
                <td>Create new secret - Path</td>
                <td>
                    <input type="text" class="allowonly7bit" maxlength="20" name="path" value=""/>
                </td>
                <td>Create new secret - Secret</td>
                <td>
                    <input type="text" class="allowonly7bit" maxlength="20" name="secret" value=""/>
                </td>
                <td><g:submitButton class="btn btn-primary" name="submit" value="Create secret"/></td>
            </g:form>
        </tr>
        <tr>
            <td></td><td></td><td></td><td></td>
            <g:form action="search">
                <td>Search - Secret</td>
                <td>
                    <input type="text" maxlength="60" name="secret" value=""/>
                </td>
                <td><g:submitButton class="btn btn-primary" name="submit" value="Search secret"/></td>
            </g:form>
        </tr>
    </table>

<g:form action="index">
    <ul>
        <g:each in="${secrets}" var="secret">
            <li><g:link action="secret" params='[key: "${selectedPath}${secret.secret}"]'>${secret.secret} (${secret.metadata?.title?:""})</g:link></li>
        </g:each>
    </ul>
</g:form>
</body>
</html>