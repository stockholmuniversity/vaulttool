<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - User Administration</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>User Administration</h3>
        </div>
        <div class="card-body">
            <g:form action="createUser">
                <div class="row bottom-margin-small">
                    <div class="col-sm-3">
                        <strong>EPPN</strong>
                    </div>
                    <div class="col-sm-6">
                        <input class="eppnformat form-control" type="text" id="eppn" name="eppn" value=""/>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                    <div class="col-sm-3">
                         <strong>Cellphone number</strong>
                    </div>
                    <div class="col-sm-6">
                        <input class="cellphone form-control" type="text" id="sms" name="sms" value=""/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-9">
                        <div class="pull-right">
                            <g:submitButton class="btn btn-primary" name="submit" value="Create/Update User"/>
                        </div>
                    </div>
                </div>
            </g:form>
        </div>

    </div>
    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>Users</h3>
        </div>
        <div class="card-body top-padding-none bottom-padding-none">
            <g:each in="${secrets}" var="secret" status="i">
                <div class="row ${(i < secrets.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-sm-6 col-xs-6">
                       <div class="row">
                           <div class="col-sm-9">
                               <strong>${secret.secret}</strong>
                           </div>
                           <div class="col-sm-3">
                               <span>${secret.userdata?.smsNumber?:""}</span>
                           </div>
                       </div>
                    </div>
                    <div class="col-sm-6 col-xs-6">
                        <div class="pull-right">
                            <g:link class="btn btn-danger button-danger-small" action="deleteUser" params='[key: "${secret.secret}"]'>Delete</g:link>
                        </div>

                    </div>

                </div>
            </g:each>
        </div>
    </div>

</body>
</html>