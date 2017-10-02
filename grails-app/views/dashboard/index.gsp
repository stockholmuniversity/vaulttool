<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - List</title>
    <asset:javascript src="inputrules.js"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
<div class="bottom-margin-xlarge">

    <div class="panel panel-default">
        <div class="panel-heading">
            <h3>
                New secret
            </h3>
        </div>

        <div class="panel-body">
            <div class="row bottom-margin-xsmall">
                <div class="col-sm-1">
                    <strong>Path</strong>
                </div>
                <div class="col-sm-2">
                    <g:form action="index">
                        <g:select class="form-control" onchange="submit();" name="selectedPath" from="${paths}" value="${selectedPath}" noSelection="${['':'Root']}"/>
                    </g:form>
                </div>
                <div class="col-sm-2">
                    Capabilities: <i>(${capabilities.join(", ")})</i>
                </div>
            </div>
            <g:form action="createSecret">
                <g:hiddenField name="selectedPath" value="${selectedPath}"/>
                <div class="row bottom-margin-xsmall">
                    <div class="col-sm-1"></div>
                    <div class="col-sm-11">
                        <input type="text" class="form-control allowonly7bit" maxlength="20" name="path" value="" placeholder="Enter path (optional)"/>
                    </div>
                </div>

                <div class="row bottom-margin-xsmall">
                    <div class="col-sm-1">
                        <strong>Secret</strong>
                    </div>
                    <div class="col-sm-11">
                        <input type="text" class="form-control allowonly7bit" maxlength="20" name="secret" value="" placeholder="Enter secret"/>
                    </div>
                </div>
                <div class="pull-right">
                    <g:submitButton class="btn btn-primary" name="submit" value="Create secret"/>
                </div>
                <div class="clearfix"></div>
            </g:form>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">
            <g:form action="search">
                <div class="input-group">
                    <input id="searchQueryInput" class="form-control search-query-input" type="text" maxlength="60" name="secret" value="" placeholder="Search by key, title or description"/>
                    <div class="input-group-btn">
                        <button class="btn search-query" name="submit" value="Search secret">
                            <span class="fa fa-search fa-lg"></span>
                        </button>
                    </div>
                </div>
            </g:form>
        </div>
        <div class="panel-body top-padding-none bottom-padding-none">
            <g:form action="index">
                <g:each in="${secrets}" var="secret" status="i">

                    <div class="row pointer">
                        <div class="col-sm-12 ${(i < secrets.size() - 1) ? 'itemListBorder':''} right-padding-none left-padding-none bottom-padding-none top-padding-none">
                            <g:link class="secretsListLink" action="secret" params='[key: "${selectedPath}${secret.secret}"]'>
                                <span>
                                    <strong>${secret.secret}</strong><br />
                                    ${secret.metadata?.title?:""}
                                </span>
                                
                            </g:link>
                        </div>
                    </div>
                </g:each>
            </g:form>
        </div>
    </div>
</div>



</body>
</html>