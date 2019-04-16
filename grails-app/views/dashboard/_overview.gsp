<div class="card bottom-margin-large">
    <div class="card-header">
        <h3>
            New subpath and/or secret
        </h3>
    </div>

    <div class="card-body">
        <div class="row bottom-margin-large">
            <div class="col-sm-2">
                %{--<label for="selectedPath"><strong>Path</strong></label>--}%
                <h5>Path</h5>

            </div>
            <div class="col-sm-4">
                <h5><span id="selectedPath_new">${(selectedPath) ?: 'Root/' }</span></h5>
                <form id="selectPathForm" name="selectPathForm">
                <g:hiddenField name="selectedPath" value="${selectedPath}"/>
                    %{--<g:select class="form-control" id="selectedPath" name="selectedPath" from="${paths}" value="${selectedPath}" noSelection="${['':'Root']}"/>--}%
                </form>
       
            </div>
            <div class="col-sm-6">
                Your capabilities on this path : <em>${capabilities.join(", ")}</em>
            </div>
        </div>
        <form id="createSecretForm" name="createSecretForm">
            <g:hiddenField name="selectedPath" value="${selectedPath}"/>
            <div class="row bottom-margin-xsmall">
                <div class="col-sm-2">
                    <label for="path"><strong>New subpath</strong> <br />(optional)</label>
                </div>
                <div class="col-sm-10">
                    <input type="text" class="form-control allowonly7bit" maxlength="20" id="path" name="path" value="" placeholder="Subpath name"/>
                </div>
            </div>

            <div class="row bottom-margin-xsmall">
                <div class="col-sm-2">
                    <label for="secret"><strong>New secret</strong><br />(optional)</label>
                </div>
                <div class="col-sm-10">
                    <input type="text" class="form-control allowonly7bit" maxlength="20" name="secret" id="secret" value="" placeholder="Secret name"/>
                </div>
            </div>
            <div class="pull-right">
                <button id="createSecretSubmitBtn" class="btn btn-primary" name="createSecretSubmitBtn" value="Create secret">Create</button>
            </div>
            <div class="clearfix"></div>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h3>Secrets</h3>
    </div>
    <div class="card-body top-padding-none bottom-padding-none cardBodyItemsListPadding">
        <g:if test="${secrets.empty}">
            <div class="left-padding-xsmall top-padding-xsmall bottom-padding-xsmall">No secrets added</div>
        </g:if>
        <g:else>
            <g:form action="index">
                <g:each in="${secrets}" var="secret" status="i">

                    <div class="row pointer">
                        <div class="col-sm-12 ${(i < secrets.size() - 1) ? 'itemListBorder':''} right-padding-none left-padding-none bottom-padding-none top-padding-none">
                            <g:link class="secretsListLink" data-key="${selectedPath}${secret.secret}">
                                <span>
                                    <strong>${secret.secret}</strong><br />
                                    ${secret.metadata?.title?:""}
                                </span>

                            </g:link>
                        </div>
                    </div>
                </g:each>
            </g:form>
        </g:else>

    </div>
</div>