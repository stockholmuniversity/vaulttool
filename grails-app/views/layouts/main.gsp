<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="cache-control" content="max-age=0" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
    <meta http-equiv="pragma" content="no-cache" />
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>
    <g:layoutHead/>
</head>
<body>
    <div class="container">
        <div class="jumbotron su-jumbotron">
            <span>Welcome ${session?.displayname?:"Unknown User"} - Your current group is ${session?.group?:"Unknown Group"}</span>
            <g:if test="${session.token != null}">
                <g:link class="pull-right disable-link-colors" controller="public" action="logout">Logout</g:link>
            </g:if>
            <g:if test="${session.group == 'sysadmin' || session.group == grailsApplication.config.vault.sysadmdevgroup}">
                <g:link class="pull-right disable-link-colors margin-right-1-char" controller="admin" action="index">Administration</g:link>
            </g:if>
            <g:if test="${session.sudo != null}">
                <g:link class="pull-right disable-link-colors margin-right-1-char" controller="public" action="disableSudo">Disable Sudo-Mode</g:link>
            </g:if>
            <g:if test="${session.groups != null && session.groups.size() > 1 && !session.sudo}">
                <g:form style="float: right;" controller="public" action="setGroup" method="post">
                    <g:select onchange="submit();" class="select-in-jumbo pull-right margin-right-1-char" name="group" from="${session.groups}" value="${session?.group?:""}"/>
                </g:form>
            </g:if>
            <a class="disable-link-colors" href="http://su.se">
                <asset:image class="pull-left" alt="Stockholms universitet" src="logo_su_se_big_dark_blue.gif"/>
            </a>
            <g:link class="disable-link-colors" controller="dashboard" action="index"><h1 class="jumbo-appname">Vaulttool</h1></g:link>
        </div>
        <g:if test="${flash.error}">
            <div class="row btn-danger">
                <div class="col-sm-12">
                    ${flash.error}
                </div>
            </div>
        </g:if>
        <g:if test="${flash.message}">
            <div class="row btn-success">
                <div class="col-sm-12">
                    ${flash.message}
                </div>
            </div>
        </g:if>
        <div class="row">
            <div class="col-sm-12">
                <g:layoutBody/>
            </div>
        </div>
        <div class="footer" role="contentinfo">Vaulttool version&nbsp;${grailsApplication.metadata.getApplicationVersion()}&nbsp;${InetAddress?.getLocalHost()?.getHostName()}</div>
    </div>
</body>
</html>
