<%@ page import="se.su.it.vaulttool.VaultRestService" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - AppRole(Entitlement) Administration</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h1>AppRole(Entitlement) Administration</h1>
    <div class="row">
        <div class="col-sm-12">
            <g:form action="createApprole">
                <label for="name">Create/Update approle - AppRole(Entitlement) name</label>
                <input class="allowonly7bit" type="text" id="name" name="name" value=""/>
                <label for="policies">Policies</label>
                <g:select from="${policies*.policy}"  multiple="multiple" id="policies" name="policies"/>
                <g:submitButton class="btn btn-primary" name="submit" value="Create/Update Approle"/>
            </g:form>
        </div>
    </div>
    <br/>
    <hr/>
    <g:each in="${approles}" var="approle">
        <div class="row">
            <div class="col-sm-2">
                ${approle.appRole}
            </div>
            <div class="col-sm-9">
                ${approle.policies.join(", ")}
            </div>
            <div class="col-sm-1">
                <g:link action="deleteApprole" params='[approle: "${approle.appRole}"]'>Delete</g:link></li>
            </div>
        </div>
    </g:each>
    <br/>
    <br/>
    <h1>Policy Information</h1>
    <hr/>
    <g:each in="${policies}" var="policy">
        <div class="row">
            <div class="col-sm-2">
                ${policy.policy}
            </div>
            <div class="col-sm-10">
                <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}")}">
                    <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/")}">
                        ${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/","")}
                    </g:if>
                    <g:else>
                        ${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}","")}
                    </g:else>
                </g:if>
            </div>
        </div>
    </g:each>
</body>
</html>