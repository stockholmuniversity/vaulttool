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
                                                    <span class="selectableAppRole">
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
                                <span id="addPolicyLink" class="pointer selectableAppRole">
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
            <h3>Roles</h3>
        </div>
        <div class="card-body top-padding-none bottom-padding-none">
            <g:each in="${approles}" var="approle" status="i">
                <div class="row bottom-padding-xsmall top-padding-xsmall ${(i < approles.size() - 1) ? 'itemListBorder appRoleBorder':''}">
                    <div class="col-9 col-lg-3">
                        <strong class="cardBodyListItem">${approle.appRole}</strong>
                    </div>
                    <div id="editableApproleListItemLink_${approle.appRole}" class="col-3 col-lg-1 order-lg-5">
                        <g:link class="btn btn-primary button-danger-small pull-right editApproleLink" params='[approle: "${approle.appRole}"]' data-approle="${approle.appRole}">Edit</g:link>
                    </div>
                    <div id="editablePolicyListItemView_${approle.appRole}" class="col-lg-8 order-lg-4">
                        <span class="cardBodyListItem">${approle.policies.join(", ")}</span>
                    </div>

                    <div id="approlePolicyContainer_${approle.appRole}" class="col-12 col-lg-9 d-none top-padding-small bottom-padding-small" style="/*border: 1px solid #eee;*/">
                         <div class="bottom-margin-medium">
                            <div id="selectedPolicies_${approle.appRole}">
                                <g:each in="${approle.policies}" var="policy">
                                    <span  id="editableApproleSelectedPolicy_${policy}_${approle.appRole}" class="bottom-margin-xsmall selectedAppRole" data-edappselpolicy="${policy}" data-edappselapprole="${approle.appRole}">
                                        ${policy}
                                        <span class="fa fa-times pointer"></span>
                                    </span>&nbsp;
                                </g:each>
                            </div>
                            <span class="pointer editableApprolePolicyLink selectableAppRole" data-approle="${approle.appRole}">
                                <span class="fa fa-plus"></span> <strong id="approlePoliciesLinkLabel_${approle.appRole}">Add policies</strong>
                            </span>
                        </div>

                        <div id="policiesContainer_${approle.appRole}" class="card bottom-margin-medium d-none">
                            <div class="card-body">
                                <div id="selectablePolicies_${approle.appRole}" class="row">
                                    <g:each in="${policies*.policy.minus(approle.policies)}" var="policy">
                                        <div id="editableApproleSelectablePolicy_${policy}" class="col-3 pointer" data-policy="${policy}" data-approle="${approle.appRole}">
                                            <span class="selectableAppRole">
                                                <strong>${policy}</strong>
                                            </span>
                                        </div>
                                    </g:each>
                                </div>
                            </div>
                        </div>
                        <div>
                            <div class="pull-left">
                                <g:link class="btn btn-danger deleteApproleLink" params='[approle: "${approle.appRole}"]' data-approle="${approle.appRole}">Delete</g:link>
                            </div>
                            <div class="pull-right">
                                <g:link class="btn btn-primary right-margin-xsmall cancelEditApproleLink" data-approle="${approle.appRole}">Cancel</g:link>
                                <g:hiddenField name="editableApprolePolicies_${approle.appRole}" value="${approle.policies.join(',')}"/>
                                <button id="updateApproleButton" name="updateApproleButton" class="btn btn-primary" data-approle="${approle.appRole}">Save</button>
                            </div>
                            <div class="clearfix"></div>
                        </div>
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