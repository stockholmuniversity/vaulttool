    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Create new role</h3>
        </div>
        <div class="card-body">
            <form id="createApproleForm" name="createApproleForm">
                <div class="row bottom-margin-small">
                    <div class="col-md-2">
                        <label for="name"><strong>Role</strong></label>
                    </div>
                    <div class="col-md-10">
                        <input class="allowonly7bit form-control" type="text" id="name" name="name" value="" placeholder="Role name"/>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                     <div class="col-md-2">
                         <label for="policies"><strong>Policies</strong></label>

                     </div>
                    <div class="col-md-10">
                        <div class="row">
                            <div id="selectedPolicies" class="col-12">
                                
                            </div>
                        </div>
                        <div class="row">
                            <div id="policiesContainer" class="col-12 d-none">
                                <div class="card">
                                    <div class="card-body">
                                        <div id="selectablePolicies" class="row">
                                            <g:each in="${policies*.policy}" var="policy">
                                                <div id="policy_${policy}" class="col-3 pointer" data-policy="${policy}">
                                                    <span style="color: rgba(0,47,95,0.8)">
                                                        <strong>${policy}</strong>
                                                    </span>
                                                </div>
                                            </g:each>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-12">
                                <g:hiddenField name="policies"/>
                                <span id="addPolicyLink" style="color: rgba(0,47,95,0.8);" class="pointer">
                                    <span class="fa fa-plus"></span> <strong id="policyLinkLabel">Add policies</strong>
                                </span>
                            </div>
                        </div>
                    </div>
                    
                </div>

                <div class="pull-right">
                    <button id="createUpdateApproleButton" class="btn btn-primary" name="createUpdateApproleButton" value="Create/Update Role">Create</button>
                </div>
            </form>
        </div>
    </div>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>
             Roles
            </h3>
        </div>
        <div class="card-body top-padding-none bottom-padding-none cardBodyItemsListPadding">
            <g:each in="${approles}" var="approle" status="i">
                <div class="row ${(i < approles.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-lg-11 col-md-10 col-sm-9 col-9">
                        <div class="row">
                            <div class="col-md-3">
                                <strong class="cardBodyListItem">${approle.appRole}</strong>
                            </div>
                            <div class="col-md-9">
                                <span class="cardBodyListItem">${approle.policies.join(", ")}</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-1 col-md-2 col-sm-3 col-3">
                        <g:link class="btn btn-danger button-danger-small pull-right deleteApproleLink" params='[approle: "${approle.appRole}"]' data-approle="${approle.appRole}">Delete</g:link>
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
        <div class="card-body top-padding-none bottom-padding-none cardBodyItemsListPadding">
            <g:each in="${policies}" var="policy" status="i">
                <div class="row ${(i < policies.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-sm-2 col-md-3">
                        <strong class="cardBodyListItem">${policy.policy}</strong>
                    </div>
                    <div class="col-sm-10 col-md-9">
                        <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}")}">
                            <g:if test="${policy.rules.contains("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/")}">
                                <span class="cardBodyListItem">${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}/","")}</span>
                            </g:if>
                            <g:else>
                                <span class="cardBodyListItem">${policy.rules.replace("secret/${se.su.it.vaulttool.VaultRestService.VAULTTOOLSECRETSPATHNAME}","")}</span>
                            </g:else>
                        </g:if>
                    </div>
                </div>
            </g:each>
        </div>
   </div>