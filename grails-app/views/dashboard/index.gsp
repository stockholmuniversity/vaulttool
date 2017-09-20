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


    <h3 style="color: #002e5f">
        New secret
    </h3>
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
            <input type="text" class="form-control allowonly7bit" maxlength="20" name="path" value="" placeholder="Enter path"/>
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

<hr>

</div>

<div class="row bottom-margin-medium">
    <g:form action="search">
        <div style="padding: 0; margin: 0">
            <div class="col-sm-12 input-group" style="padding-left: 15px">
                <input class="form-control search-query-input" type="text" maxlength="60" name="secret" value="" placeholder="Search secret by key, title or description"/>
                <span class="input-group-btn">
                    <button class="btn search-query" name="submit" value="Search secret">
                        <span class="fa fa-search fa-lg"></span>
                    </button>
                </span>
            </div>
        </div>
    </g:form>

</div>
<hr class="top-margin-small bottom-margin-small">
    <g:form action="index">
            <g:each in="${secrets}" var="secret">

                <div class="row pointer">
                   <div class="col-sm-12">
                       %{--<span class="fa fa-key"></span>--}%
                       <strong><g:link action="secret" params='[key: "${selectedPath}${secret.secret}"]'>${secret.secret} - ${secret.metadata?.title?:""}</g:link></strong>
                   </div>
                </div>
                <hr class="top-margin-small bottom-margin-small">
            </g:each>
    </g:form>
</body>
</html>