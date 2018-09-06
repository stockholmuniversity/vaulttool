    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Secret</h3>
        </div>
        <div class="card-body">
            <form name="saveSecretForm" id="saveSecretForm">
                <g:hiddenField name="key" value="${secret.key}"/>

                <div class="row bottom-margin-small">
                    <div class="col-1 col-sm-1">
                        <span class="fa fa-key text-muted"></span>
                    </div>
                    <div class="col-10 col-sm-11">
                        <span class="text-muted">
                            <strong>${secret.key}</strong>
                        </span>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                    <div class="col-md-3">
                        <label for="title"><strong>Title</strong></label>
                    </div>
                    <div class="col-md-9">
                        <input class="form-control" type="text" name="title" id="title" value="${metadata.title}"/>
                    </div>
                </div>

                <div class="row bottom-margin-small">
                    <div class="col-md-3">
                        <label for="description"><strong>Description</strong></label>
                    </div>
                    <div class="col-md-9">
                        <textarea class="form-control dynamicTextarea" name="description" id="description">${metadata.description}</textarea>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                    <div class="col-md-3 col-12">
                        <label for="username"><strong>Username</strong></label>
                    </div>
                    <div class="col-10 col-md-6 right-padding-none">
                        <input id="username" class="form-control" type="text" name="userName" value="${secret.userName}"/>
                    </div>
                    <div class="col-1 col-md-1 left-padding-none">
                        <button id="copyUsername" class="btn buttonNoBackground" value="Copy" title="Copy to clipboard">
                            <span class="fa fa-clipboard fa-lg"></span>
                        </button>
                    </div>
                </div>
                <div class="row bottom-margin-large">
                    <div class="col-12 col-md-3">
                        <label for="password"><strong>Password</strong></label>
                    </div>
                    <div class="col-10 col-md-6 right-padding-none">
                        <input id="password" class="form-control" type="password" name="password" value="${secret.pwd}" autocomplete="off"/>&nbsp;<input id="pwdCheckbox" type="checkbox" />&nbsp;
                        <span id="toggleText">Show password</span>&nbsp;
                    </div>
                    <div class="col-1 col-md-1 left-padding-none">
                        <button id="copyPwd" class="btn buttonNoBackground" value="Copy" title="Copy to clipboard">
                            <span class="fa fa-clipboard fa-lg"></span>
                        </button>
                    </div>

                </div>

                <div class="pull-right">
                    <button style="" class="btn btn-primary" id="saveSecretSubmit" value="Save secret">Save Secret</button>
                </div>

            </form>

            <div class="pull-left">
                <form name="deleteSecretForm">
                    <g:hiddenField name="key" value="${secret.key}"/>
                    <button id="deleteSecretSubmit" class="btn btn-danger pull-right" name="submit" value="Delete secret">Delete secret</button>
                </form>
            </div>

            %{--Not needed at this moment. Still keeping just in case--}%
            %{--<g:form action="index">
                <g:hiddenField name="selectedPath" value="${session.selectedPath}"/>
                <g:submitButton class="btn btn-default pull-right right-margin-small" name="submit" value="Close"/>
            </g:form>--}%

            <div class="clearfix"></div>

        </div>
    </div>
    <div class="card bottom-margin-large">
        <div class="card-body">
            <div class="bottom-margin-small">
                <h4>
                    <span class="fa fa-file-text"></span>
                    Upload file
                </h4>
            </div>
            <form id="uploadSecretFileForm" name="uploadSecretFileForm" action="javascript:" enctype="multipart/form-data" method="post">
                <g:hiddenField name="key" value="${secret.key}"/>
                <div class="row bottom-margin-small">
                    <div class="col-sm-12">
                        <span>
                            <label class="btn btn-default">
                                <input type="file" id="attachment" name="attachment" hidden/> <span class="fa fa-plus-square"></span>&nbsp;Choose file
                            </label>
                            <span id="fileSelected"></span>
                        </span>
                    </div>
                </div>

                <div class="row bottom-margin-medium">
                    <div class="col-sm-12">
                        <button id="uploadSecretFileButton" class="btn btn-primary d-none" name="uploadSecretFileButton" value="Upload file">Upload file</button>
                    </div>
                </div>
            </form>
            <g:if test="${metadata.fileName && secret.binaryData}">
                <div class="bottom-margin-small breakWithEllipsis">
                    <span class="fa fa-file"></span>&nbsp;${metadata.fileName}
                </div>
                <g:form action="download">
                    <g:hiddenField name="key" value="${secret.key}"/>
                    <div class="pull-right">
                        <button class="btn btn-primary" name="submit" value="Download ${metadata.fileName}">
                            <span class="fa fa-download"></span> Download file
                        </button>
                    </div>
                </g:form>
                <div class="pull-left">
                    <form id="deleteFileForm" name="deleteFileForm">
                        <g:hiddenField name="key" value="${secret.key}"/>
                        <button id="deleteFileButton" class="btn btn-danger" name="deleteFileButton" value="Delete file">Delete file</button>
                    </form>
                </div>
                <div class="clearfix"></div>
            </g:if>
        </div>
    </div>
