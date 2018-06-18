<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Search Results</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>
        <g:form action="index">
            <div class="card bottom-margin-medium">
                <div class="card-header">
                    <h3>Search Result</h3>
                </div>

                <div class="card-body top-padding-none bottom-padding-none">
                    <g:each in="${metadatas}" var="metadata" status="i">
                        <div class="row pointer">
                            <div class="col-sm-12 ${(i < metadatas.size() - 1) ? 'itemListBorder':''} right-padding-none left-padding-none bottom-padding-none top-padding-none">
                                <g:link class="secretsListLink" action="secret" params='[key: "${metadata.secretKey}"]'>
                                    <strong>${metadata.secretKey}</strong> <br />
                                     ${metadata?.title?:""}
                                </g:link>

                            </div>
                        </div>
                    </g:each>
                </div>
            </div>
            <div class="bottom-margin-large bottom-margin-small">
                <g:submitButton class="btn btn-default pull-right" name="Cancel" value="Cancel"/>
            </div>
            <div class="clearfix"></div>
            <div>&nbsp;</div>
        </g:form>
</body>
</html>