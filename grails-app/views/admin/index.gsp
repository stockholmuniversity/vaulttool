<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Administration</title>
    <asset:javascript src="index.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h3>Administration</h3>
    <div class="row">
        <div class="col-sm-12 col-lg-6">
            <div class="well">

                <h3>
                    <span class="fa fa-user"></span>
                    Sudo
                </h3>
                <g:form action="sudo">
                    <div>
                        <div class="bottom-margin-small">
                            <g:select class="form-control" from="${approles*.appRole}" id="sudo" name="sudo" noSelection="${['':'Select group']}"/>
                        </div>
                        <div class="pull-right">
                            <g:submitButton class="btn btn-primary" name="submit" value="Sudo now"/>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </g:form>
            </div>
        </div>
        <div class="col-sm-12 col-lg-6">
            <div class="well">
                <g:form action="importZip" method="post" enctype="multipart/form-data" useToken="false">
                    <h3>
                        <span class="fa fa-file-text fa-"></span>
                        Import secrets and users from zip-file
                    </h3>

                    <div class="bottom-margin-xsmall">
                        <label class="btn btn-default">
                            <input type="file" id="importZipInputFileId" name="importZipInputFileId" hidden/> <span class="fa fa-plus-square"></span>&nbsp;Browse
                        </label>
                        <span id="adminFileSelected"></span>
                    </div>
                    <div class="pull-right">
                        <g:submitButton id="adminUploadFile" class="btn btn-primary hidden" name="submit" value="Import zip-file"/>
                    </div>
                    <div class="clearfix"></div>

                </g:form>
            </div>

        </div>
        <div class="col-sm-12">
            <div class="well">
                <h3>
                    <span class="fa fa-download"></span>
                    <strong>Export secrets and users</strong>
                </h3>
                <g:link action="export" class="btn btn-primary">
                    Export secrets and users
                </g:link>
            </div>
        </div>
    </div>

</body>
</html>