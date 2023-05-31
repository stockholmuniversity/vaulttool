<div id="userInfoToggle" class="pull-right pointer">
    <span id="userInfoToggleIcon" class="fa fa-user fa-2x fa2xSmaller"></span>
</div>
<div id="userInfoMenu" class="box d-none">
    <div class="bottom-margin-medium">
        <div>
            <strong>
                ${session?.displayname?:"Unknown User"}
            </strong>
        </div>
        <div>
            Current group: <span id="currentGroup">${session?.group?:"Unknown Group"}</span>
        </div>
   </div>


    <div class="bottom-margin-xsmall">

        <g:if test="${session.groups != null && session.groups.size() > 1 && !session.sudo}">
            <form id="setGroupForm" name="setGroupForm">
                <g:select class="form-control input-sm" name="group" from="${session.groups}" value="${session?.group?:""}"/>
            </form>
        </g:if>
    </div>


            <div id="disableSudo" class="btn-group-vertical btn-block btn-group ${session.sudo != null?'':'d-none'}">
                <g:link name="disableSudoModeLink" class="btn btn-default btn-sm btn-block">
                    <span class="fa fa-user"></span>
                    Disable Sudo Mode
                </g:link>
            </div>
        
        <g:if test="${session.group == 'sysadmin' || session.group == grailsApplication.config.getProperty("vault.sysadmdevgroup", String.class)}">
            <div class="btn-group-vertical btn-block btn-group">

                <g:link name="admin" class="btn btn-default btn-sm btn-block">
                    Administration
                </g:link>
                <g:link name="user" class="btn btn-default btn-sm btn-block">
                    Users
                </g:link>
                <g:link name="policiesAdmin" class="btn btn-default btn-sm btn-block">
                    Policies
                </g:link>
                <g:link name="approles" class="btn btn-default btn-sm btn-block">
                    Application Roles
                </g:link>

            </div>


            <g:if test="${controllerName == 'admin'}">
                <div class="btn-group-vertical btn-block btn-group">
                    <g:link id="backToIndexLink" class="btn btn-default btn-sm btn-block">
                        <span class="fa fa-arrow-left"></span>
                        Tillbaka
                    </g:link>
                </div>
            </g:if>

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