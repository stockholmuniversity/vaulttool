<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VAULTTOOL - User Administration</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
    <h1>User Administration</h1>
    <div class="row">
        <div class="col-sm-12">
            <g:form action="createUser">
                <label for="eppn">Create/Update user - EPPN</label>
                <input class="eppnformat" type="text" id="eppn" name="eppn" value=""/>
                <label for="sms">Cellphone number</label>
                <input class="cellphone" type="text" id="sms" name="sms" value=""/>
                <g:submitButton class="btn btn-primary" name="submit" value="Create User"/>
            </g:form>
        </div>
    </div>
    <br/>
    <hr/>
    <div class="row">
        <div class="col-sm-12">
            <ul>
                <g:each in="${secrets}" var="secret">
                    <li>${secret.secret} &nbsp;(${secret.userdata?.smsNumber?:""}) <g:link action="deleteUser" params='[key: "${secret.secret}"]'>Delete</g:link></li>
                </g:each>
            </ul>
        </div>
    </div>
</body>
</html>