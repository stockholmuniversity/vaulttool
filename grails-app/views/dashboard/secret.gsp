<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Secret (${metadata.title})</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <asset:javascript src="secret.js"/>
</head>

<body>

    <h3 style="color: #002e5f">Secret</h3>
    <g:form action="updateSecret">
        <g:hiddenField name="key" value="${secret.key}"/>

        <div class="row bottom-margin-small">
            <div class="col-sm-1">
                <strong>Key</strong>
            </div>
            <div class="col-sm-11">
                <strong>${secret.key}</strong>
            </div>
        </div>
        <div class="row bottom-margin-small">
            <div class="col-sm-1">
               <strong>Title</strong>
            </div>
            <div class="col-sm-11">
                <input class="form-control" type="text" name="title" value="${metadata.title}"/>
            </div>
        </div>

        <div class="row bottom-margin-small">
            <div class="col-sm-1">
                 <strong>Description</strong>
            </div>
            <div class="col-sm-11">
                <textarea class="form-control dynamicTextarea" name="description">${metadata.description}</textarea>
            </div>
        </div>
        <div class="row bottom-margin-small">
            <div class="col-sm-1">
                 <strong>Username</strong>
            </div>
            <div class="col-sm-6 right-padding-none">
                <input id="username" class="form-control" type="text" name="userName" value="${secret.userName}"/>
            </div>
            <div class="col-sm-1 left-padding-none">
                <button id="copyUsername" class="btn" value="Copy" style="background: none; color: #002e5f; border: none;">
                    <span class="fa fa-clipboard fa-lg"></span>
                </button>
            </div>
        </div>
        <div class="row bottom-margin-large">
            <div class="col-sm-1">
                <strong>Password</strong>
            </div>
            <div class="col-sm-6 right-padding-none">
                <input id="password" class="form-control" type="password" name="password" value="${secret.pwd}" autocomplete="off"/>&nbsp;<input id="pwdCheckbox" type="checkbox" />&nbsp;
                <span id="toggleText">Show password</span>&nbsp;
            </div>
            <div class="col-sm-1 left-padding-none">
                <button id="copyPwd" class="btn btn-primary" value="Copy" style="background: none; color: #002e5f; border: none;">
                    <span class="fa fa-clipboard fa-lg"></span>
                </button>
            </div>

        </div>

        <div class="pull-right">
            <g:submitButton style="" class="btn btn-primary" name="submit" value="Save secret"/>
        </div>

    </g:form>

    <div class="pull-left">
        <g:form action="delete"><g:hiddenField name="key" value="${secret.key}"/>
            <g:submitButton class="btn btn-danger pull-right" name="submit" value="Delete secret"/>
        </g:form>
    </div>

    <g:form action="index">
        <g:submitButton style="margin-right: 10px;" class="btn btn-default pull-right" name="submit" value="Close"/>
    </g:form>

    <div class="clearfix"></div>
    
    <hr>


<g:form action="upload" method="post" enctype="multipart/form-data" useToken="false">
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

    <div class="row bottom-margin-small">
        <div class="col-sm-12">
             <g:submitButton id="uploadFile" class="btn btn-primary hidden" name="submit" value="Upload file"/>
        </div>
    </div>


</g:form>



<g:if test="${metadata.fileName && secret.binaryData}">

    <div class="row bottom-margin-large">
        <div class="col-sm-12">
            <div class="pull-left" style="margin-right: 5px;">
                <g:form action="download">
                    <g:hiddenField name="key" value="${secret.key}"/>
                    <button class="btn btn-default box" name="submit" value="Download ${metadata.fileName}">
                        <span class="fa fa-file"></span>&nbsp;<span>${metadata.fileName}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <span class="fa fa-download"></span>
                    </button>
                </g:form>
            </div>
            <div style="padding-top: 2px;">
                <g:form action="deleteFile">
                    <g:hiddenField name="key" value="${secret.key}"/>
                    <g:submitButton class="btn btn-danger" name="submit" value="Delete file"/>

                    
                </g:form>
            </div>

        </div>
    </div>
</g:if>



</body>
</html>