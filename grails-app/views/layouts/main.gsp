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
    <asset:stylesheet src="font-awesome.min.css"/>
    <asset:stylesheet src="default/style.min.css"/>
    <asset:javascript src="application.js"/>
    %{--<asset:javascript src="jstree.min.js"/>--}%
    %{--<asset:javascript src="treeview.js"/>--}%

    <g:layoutHead/>
</head>
<body>
    <header class="bottom-margin-large">
        <div class="row">
            <div class="col-10">
                <div class="d-none d-md-block">
                    <a class="disable-link-colors" href="http://su.se">
                        <g:if test="${(session.logoUrl && session.logoUrl == 'internal') || !session.logoUrl}">
                            <asset:image id="logoBig" class="pull-left" alt="Stockholms universitet" src="su-logo.png"/>
                        </g:if>
                        <g:else>
                            <img class="pull-left" alt="Stockholms universitet" src="${session.logoUrl}" width="156" height="130"/>
                        </g:else>
                    </a>
                </div>
                <div id="logoSmall" class="d-block d-md-none">
                    <asset:image src="su-logo-small.png" alt="Stockholms universitet, startsida"/>
                </div>
            </div>
            <div class="col-2">
                <div class="pull-right">
                    <g:render template="/layouts/userInfoMenu"/>
                </div>
                <div class="clearfix"></div>
                <div class="pull-right d-none d-md-block">
                    <g:link class="disable-link-colors" controller="dashboard" action="index">
                        <h1>${session.applicationName?:'Vaulttool'}</h1>
                    </g:link>
                </div>
            </div>
        </div>
        <div class="d-block d-md-none" style="text-align: center">
            <g:link class="disable-link-colors" controller="dashboard" action="index">
                <h1>${session.applicationName?:'Vaulttool'}</h1>
            </g:link>
        </div>
    </header>

    <div class="container">


        <div class="row">
             <g:if test="${controllerName != 'public'}">
                 <div id="nav-column" class="col-lg-auto bottom-margin-large" style="/*background-color: #99ACBF*/ background-color: #33597F; padding-left: 0; padding-right: 0; padding-top: 0;">
                     <div class="bottom-margin-medium" style="margin-bottom: 10px; padding-top: 5px; padding-bottom: 5px; background-color: #E0E0E0;">
                         <g:form action="search" controller="dashboard">
                             <div class="input-group" style="padding-left: 5px; padding-right: 5px">
                                 <input id="searchQueryInput" class="form-control search-query-input" type="text" maxlength="60" name="secret" value="" placeholder="Path, key, title or description"/>
                                 %{--<div class="input-group-btn">--}%
                                 <button class="btn search-query input-group-append" name="submit" value="Search secret" style="background-color: #1B95E0; border: 1px solid #1474b0;">
                                     <span class="fa fa-search fa-lg"></span>
                                 </button>
                                 %{--</div>--}%
                             </div>
                         </g:form>

                     %{--<input id="quickSearch" type="text" style="width: 100%"/>--}%
                     </div>
                     %{--<div><span class="fa fa-home"></span>&nbsp;Root</div>--}%
                     <div id="navTree">
                     </div>
                 </div>
             </g:if>

            <div id="main-column" class="col">
                <g:if test="${flash.error}">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="alert alert-danger">${flash.error}</div>
                        </div>
                    </div>
                </g:if>
                <g:if test="${flash.warning}">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="alert alert-warning">
                                ${flash.warning}
                            </div>

                        </div>
                    </div>
                </g:if>
                <g:if test="${flash.message}">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="alert alert-success">
                                ${flash.message}
                            </div>

                        </div>
                    </div>
                </g:if>
                <div class="row">
                    <div class="col-sm-12">
                        <g:render template="/layouts/scrollTop"/>
                        <g:layoutBody/>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <footer>
        <p class="footer" role="contentinfo">${session.applicationName?:'Vaulttool'} | version&nbsp;${grailsApplication.metadata.getApplicationVersion()} | ${InetAddress?.getLocalHost()?.getHostName()}</p>
    </footer>
</body>
</html>
