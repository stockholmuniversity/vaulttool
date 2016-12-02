<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - Secret (${secret.title})</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <asset:javascript src="secret.js"/>
</head>

<body>
<div style="padding: 20px;">
    <h1>Secret - ${secret.key}</h1>
    <g:form action="updateSecret">
        <g:hiddenField name="key" value="${secret.key}"/>
        <table style="border: none;">
            <tr>
                <td>Title</td>
                <td><input type="text" name="title" value="${secret.title}"/></td>
            </tr>
            <tr>
                <td>Description</td>
                <td><textarea name="description" rows="4" cols="100">${secret.description}</textarea></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input id="password" type="password" name="password" value="${secret.pwd}"/>&nbsp;<input id="pwdCheckbox" type="checkbox" />&nbsp;<span id="toggleText">show</span></td>
            </tr>
        </table>
        <g:submitButton style="float:left;" class="btn btn-primary" name="submit" value="Save ${secret.key}"/>
    </g:form>
    <g:form action="delete"><g:hiddenField name="key" value="${secret.key}"/><g:submitButton class="btn btn-danger pull-right" name="submit" value="Delete ${secret.key}"/></g:form>
    <g:form action="index"><g:submitButton style="margin-right: 10px;" class="btn btn-warning pull-right" name="submit" value="Cancel"/></g:form>
    <div class="clear-float"></div>
    <br/>
    <hr>
    <table style="border: none;">
        <tr>
            <g:if test="${secret.fileName && secret.binaryData}">
                <td>
                    <g:form action="download">
                        <g:hiddenField name="key" value="${secret.key}"/>
                        <g:submitButton class="btn btn-primary" name="submit" value="Download ${secret.fileName}"/>
                    </g:form>
                </td>
            </g:if>
            <td>
                <g:form action="upload" method="post" enctype="multipart/form-data" useToken="false">
                    <g:hiddenField name="key" value="${secret.key}"/>
                    <table style="border: none;width: 30%;">
                        <tr>
                            <td><input type="file" id="attachment" name="attachment" /></td>
                            <td><g:submitButton class="btn btn-primary" name="submit" value="Upload file"/></td>
                        </tr>
                    </table>
                </g:form>
            </td>
        </tr>
    </table>
</div>
</body>
</html>