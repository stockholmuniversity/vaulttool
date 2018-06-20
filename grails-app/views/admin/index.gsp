<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Administration</title>
    <asset:javascript src="index.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Administration</h3>
        </div>
        <div class="card-body">
            <g:form action="sudo">
                <div class="row">
                    <div class="col-sm-12">
                        <h4>
                            <span class="fa fa-user"></span>
                            Sudo
                        </h4>
                        <div class="bottom-margin-small">
                            <g:select class="form-control" from="${approles*.appRole}" id="sudo" name="sudo" noSelection="${['':'Select group']}"/>
                        </div>
                        <div class="pull-right">
                            <g:submitButton class="btn btn-primary" name="submit" value="Sudo now"/>
                        </div>
                    </div>

                </div>
            </g:form>
        </div>
    </div>
<div class="row bottom-margin-large">
    <div class="col-sm-12 col-lg-7 bottom-margin-medium">
        <div class="card">
            <div class="card-body">
                <g:form action="importZip" method="post" enctype="multipart/form-data" useToken="false">
                    <div class="bottom-margin-small">
                        <h4>
                            <span class="fa fa-file-text"></span>
                            Import secrets and users from zip-file
                        </h4>
                    </div>
                    <div class="bottom-margin-xsmall">
                        <label class="btn btn-default">
                            <input type="file" id="importZipInputFileId" name="importZipInputFileId" hidden/> <span class="fa fa-plus-square"></span>&nbsp;Browse
                        </label>
                        <span id="adminFileSelected"></span>
                    </div>
                    <div class="pull-right">
                        <g:submitButton id="adminUploadFile" class="btn btn-primary d-none" name="submit" value="Import zip-file"/>
                    </div>
                    <div class="clearfix"></div>
                </g:form>
            </div>
        </div>
    </div>
    <div class="col-sm-12 col-lg-5">
        <div class="card">
            <div class="card-body">
                <div class="bottom-margin-small">
                    <h4>
                        <span class="fa fa-download"></span>
                        Export secrets and users
                    </h4>
                </div>
                <g:link action="export" class="btn btn-primary">
                    Export secrets and users
                </g:link>
            </div>
        </div>

    </div>
</div>



</body>
</html>