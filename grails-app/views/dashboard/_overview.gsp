<div class="card bottom-margin-large">
    <div class="card-header">
        <h3>
            New subpath and/or secret
        </h3>
    </div>

    <div class="card-body">
        <div class="row bottom-margin-large">
            <div class="col-sm-2">
                <h5>Path</h5>
            </div>
            <div class="col-sm-4">
                <h5><span id="selectedPathTitle">${(selectedPath) ?: 'Root/' }</span></h5>
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
                    <input type="text" class="form-control allowonly7bitpath" maxlength="150" id="path" name="path" value="" placeholder="Subpath name"/>
                </div>
            </div>

            <div class="row bottom-margin-xsmall">
                <div class="col-sm-2">
                    <label for="secret"><strong>New secret</strong><br />(optional)</label>
                </div>
                <div class="col-sm-10">
                    <input type="text" class="form-control allowonly7bit" name="secret" maxlength="150" id="secret" value="" placeholder="Secret name"/>
                </div>
            </div>
            <div class="pull-right">
                <button id="createSecretSubmitBtn" class="btn btn-primary" name="createSecretSubmitBtn" value="Create secret">Create</button>
            </div>
            <div class="clearfix"></div>
        </form>
    </div>
</div>

