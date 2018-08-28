    <div class="card bottom-margin-large">
        <div class="card-header">
            <h3>User Administration</h3>
        </div>
        <div class="card-body">
            <g:form action="createUser">
                <div class="row bottom-margin-small">
                    <div class="col-md-3">
                        <label for="eppn"><strong>EPPN</strong></label>
                    </div>
                    <div class="col-md-6">
                        <input class="eppnformat form-control" type="text" id="eppn" name="eppn" value=""/>
                    </div>
                </div>
                <div class="row bottom-margin-small">
                    <div class="col-md-3">
                        <label for="sms"><strong>Cellphone number</strong></label>
                    </div>
                    <div class="col-md-6">
                        <input class="cellphone form-control" type="text" id="sms" name="sms" value=""/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-9">
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
        <div class="card-body top-padding-none bottom-padding-none cardBodyItemsListPadding">
            <g:each in="${secrets}" var="secret" status="i">
                <div class="row ${(i < secrets.size() - 1) ? 'itemListBorder':''} top-padding-xsmall bottom-padding-xsmall">
                    <div class="col-md-8 col-sm-6 col-6">
                       <div class="row">
                           <div class="col-md-7 col-lg-8">
                               <strong class="cardBodyListItem">${secret.secret}</strong>
                           </div>
                           <div class="col-md-5 col-lg-4">
                               <span class="cardBodyListItem">${secret.userdata?.smsNumber?:""}</span>
                           </div>
                       </div>
                    </div>
                    <div class="col-md-4 col-sm-6 col-6">
                        <div class="pull-right">
                            <g:link class="btn btn-danger button-danger-small" action="deleteUser" params='[key: "${secret.secret}"]'>Delete</g:link>
                        </div>

                    </div>

                </div>
            </g:each>
        </div>
    </div>
