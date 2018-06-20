<%@ page import="se.su.it.vaulttool.VaultRestService" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - AppRole(Entitlement) Administration</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Application Roles Administration</h3>
        </div>
        <div class="card-body">
            <g:form action="createApprole">
                <div class="row bottom-margin-small">
                    <div class="col-md-2">
                        <label for="name"><strong>Role</strong></label>
                    </div>
                    <div class="col-md-10">
                        <input class="allowonly7bit form-control" type="text" id="name" name="name" value="" placeholder="Application role name (entitlement)"/>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                     <div class="col-md-2">
                         <label for="policies"><strong>Policies</strong></label>

                     </div>
                    <div class="col-md-10">
                        <g:select class="form-control" from="${policies*.policy}"  multiple="multiple" id="policies" name="policies"/>
                        <span class="text-muted small">Select one or more policies</span>
                    </div>
                </div>

                <div class="pull-right">
                    <g:submitButton class="btn btn-primary" name="submit" value="Create/Update Role"/>
                </div>


            </g:form>
        </div>
    </div>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>
             Roles
            </h3>
        </div>
        <div class="card-body top-padding-none bottom-padding-none">
            <g:each in="${approles}" var="approle" status="i">
                <div class="row ${(i < approles.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-lg-11 col-md-10 col-sm-9 col-9">
                        <div class="row">
                            <div class="col-md-3">
                                <strong>${approle.appRole}</strong>
                            </div>
                            <div class="col-md-9">
                                ${approle.policies.join(", ")}
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-1 col-md-2 col-sm-3 col-3">
                        <g:link class="btn btn-danger button-danger-small pull-right" action="deleteApprole" params='[approle: "${approle.appRole}"]'>Delete</g:link>
                    </div>
                </div>
            </g:each>
        </div>
    </div>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>
                Policy Information
            </h3>
        </div>
        <div class="card-body top-padding-none bottom-padding-none">
            <g:each in="${policies}" var="policy" status="i">
                <div class="row ${(i < policies.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-sm-2 col-md-2">
                        <strong>${policy.policy}</strong>
                    </div>
                    <div class="col-sm-10 col-md-10">
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
        </div>
   </div>
</body>
</html>