<div id="userInfoMenu" class="box hidden">
    <div class="bottom-margin-medium">
        <div>
            <strong>
                ${session?.displayname?:"Unknown User"}
            </strong>
        </div>
        <div>
            Current group: ${session?.group?:"Unknown Group"}
        </div>
   </div>


    <div class="bottom-margin-xsmall">

        <g:if test="${session.groups != null && session.groups.size() > 1 && !session.sudo}">
            <g:form style="" controller="public" action="setGroup" method="post">
                <g:select onchange="submit();" class="select-in-jumbo margin-right-1-char form-control input-sm" name="group" from="${session.groups}" value="${session?.group?:""}"/>
            </g:form>
        </g:if>
    </div>
    <div class="btn-group-vertical btn-block btn-group">
        <g:if test="${session.sudo != null}">
            <g:link class="disable-link-colors margin-right-1-char btn btn-default btn-sm btn-block" controller="public" action="disableSudo">
                <span class="fa fa-user"></span>
                Disable Sudo mode
            </g:link>
        </g:if>
        
            <g:if test="${session.group == 'sysadmin' || session.group == grailsApplication.config.vault.sysadmdevgroup}">
                <g:if test="${controllerName != 'admin'}">
                    <g:link class="disable-link-colors margin-right-1-char btn btn-default btn-sm btn-block" controller="admin" action="index">
                        Administration
                    </g:link>
                </g:if>
                <g:if test="${controllerName == 'admin'}">
                    <g:link class="disable-link-colors btn btn-default btn-sm btn-block" controller="dashboard" action="index">
                        <span class="fa fa-arrow-left"></span>
                        Tillbaka
                    </g:link>
                </g:if>
            </g:if>

            <g:if test="${session.token != null}">
                <g:link class="disable-link-colors btn btn-default btn-sm btn-block" controller="public" action="logout">
                    <span class="fa fa-sign-out"></span>
                    Logout
                </g:link>
            </g:if>
        
    </div>
</div>