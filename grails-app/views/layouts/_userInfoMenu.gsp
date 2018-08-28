<div id="userInfoToggle" class="pull-right pointer">
    <span class="fa fa-user fa-2x fa2xSmaller"></span>
</div>
<div id="userInfoMenu" class="box d-none">
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
                <g:select onchange="submit();" class="form-control input-sm" name="group" from="${session.groups}" value="${session?.group?:""}"/>
            </g:form>
        </g:if>
    </div>


        <g:if test="${session.sudo != null}">
            <div class="btn-group-vertical btn-block btn-group">
                <g:link class="btn btn-default btn-sm btn-block" controller="public" action="disableSudo">
                    <span class="fa fa-user"></span>
                    Disable Sudo Mode
                </g:link>
            </div>
        </g:if>

        <g:if test="${session.group == 'sysadmin' || session.group == grailsApplication.config.vault.sysadmdevgroup}">
            <div class="btn-group-vertical btn-block btn-group">

                <g:link name="admin" class="btn btn-default btn-sm btn-block">
                    Administration
                </g:link>
                <g:link name="user" class="btn btn-default btn-sm btn-block">
                    Users
                </g:link>
                <g:link name="policies" class="btn btn-default btn-sm btn-block">
                    Policies
                </g:link>
                <g:link name="approles" class="btn btn-default btn-sm btn-block">
                    Application Roles
                </g:link>

            </div>

            <div class="btn-group-vertical btn-block btn-group">
                <g:if test="${controllerName == 'admin'}">
                    <g:link class="btn btn-default btn-sm btn-block" controller="dashboard" action="index">
                        <span class="fa fa-arrow-left"></span>
                        Tillbaka
                    </g:link>
                </g:if>
            </div>
        </g:if>

        <g:if test="${session.token != null}">
            <div class="btn-group-vertical btn-block btn-group">
                <g:link class="btn btn-default btn-sm btn-block" controller="public" action="logout">
                    <span class="fa fa-sign-out"></span>
                    Logout
                </g:link>
            </div>
        </g:if>
</div>