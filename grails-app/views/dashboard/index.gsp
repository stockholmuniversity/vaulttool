<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - List</title>
    <asset:javascript src="index.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <table style="border: none;">
        <tr>
            <td><g:form action="index"><g:select onchange="submit();" name="selectedPath" from="${paths}" value="${selectedPath}" noSelection="${['':'Root']}"/></g:form></td>
            <g:form action="createSecret">
                <g:hiddenField name="selectedPath" value="${selectedPath}"/>
                <td>Create new secret - Path</td>
                <td title="Only nonalphanumeric 7-bit ascii characters whitout whitespace allowed.">
                    <input type="text" class="allowonly7bit" maxlength="20" name="path" value=""/>
                </td>
                <td>Create new secret - Secret</td>
                <td title="Only nonalphanumeric 7-bit ascii characters whitout whitespace allowed.">
                    <input type="text" class="allowonly7bit" maxlength="20" name="secret" value=""/>
                </td>
                <td><g:submitButton class="btn btn-primary" name="submit" value="Create secret"/></td>
            </g:form>
        </tr>
    </table>

<g:form action="index">
    <ul>
        <g:each in="${secrets}" var="secret">
            <li><g:link action="secret" params='[key: "${selectedPath}${secret}"]'>${secret}</g:link></li>
        </g:each>
    </ul>
</g:form>
</body>
</html>