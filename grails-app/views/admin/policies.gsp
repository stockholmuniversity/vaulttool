<%@ page import="se.su.it.vaulttool.VaultRestService" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - Policy Administration</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h1>Policy Administration</h1>
    <div class="row">
        <div class="col-sm-12">
            <g:form action="createPolicy">
                <label for="name">Create/Update policy - Policy name</label>
                <input class="allowonly7bit" type="text" id="name" name="name" value=""/>
                <label for="path">Path</label>
                <g:select id="path" name="path" from="${paths}" value="" noSelection="${['':'Root']}"/>
                <label for="create">Create</label>
                <g:checkBox name="create" id="read"/>
                <label for="read">Read</label>
                <g:checkBox name="read" id="read"/>
                <label for="update">Update</label>
                <g:checkBox name="update" id="read"/>
                <label for="delete">Delete</label>
                <g:checkBox name="delete" id="read"/>
                <label for="list">List</label>
                <g:checkBox name="list" id="read"/>
                <g:submitButton class="btn btn-primary" name="submit" value="Create Policy"/>
            </g:form>
        </div>
    </div>
    <br/>
    <hr/>
    <g:each in="${policies}" var="policy">
        <div class="row">
            <div class="col-sm-2">
                ${policy.policy}
            </div>
            <div class="col-sm-9">
                <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}")}">
                    <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/")}">
                        ${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/","")}
                    </g:if>
                    <g:else>
                        ${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}","")}
                    </g:else>
                </g:if>
            </div>
            <div class="col-sm-1">
                <g:link action="deletePolicy" params='[policy: "${policy.policy}"]'>Delete</g:link></li>
            </div>
        </div>
    </g:each>
</body>
</html>