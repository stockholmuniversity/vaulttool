<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Authentication Confirmation</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>

<body>

<div class="panel panel-default">
  <div class="panel-heading">
      <h3>Authentication Confirmation</h3>
  </div>
  <div class="panel-body">
      <div class="row">
          <div class="col-sm-12">
              <span>Please enter the code sent to your registered sms-number.</span>
          </div>
      </div>
      <div class="row">
          <div class="col-sm-12">
              <g:form action="secondauth">
                  <input type="text" name="verificationcode" value=""/>
                  <g:submitButton class="btn btn-primary" name="submit" value="Verify"/>
              </g:form>
          </div>
      </div>
  </div>
</div>

</body>
</html>