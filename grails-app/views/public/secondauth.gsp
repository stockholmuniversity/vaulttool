<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${session.applicationName?:'Vaulttool'} - Authentication Confirmation</title>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const verificationInput = document.querySelector('input[name="verificationcode"]');
            if (verificationInput) {
                verificationInput.focus();
            }
        });
    </script>
</head>

<body>

<div class="card bottom-margin-large">
    <div class="card-header">
        <h3>Authentication Confirmation</h3>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-sm-12">
                <span>Please enter the code sent to your registered sms-number.</span>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <g:form action="secondauth">
                    <div class="row">
                        <div class="col-xs-8 col-sm-4 right-padding-none">
                            <input type="text" name="verificationcode" value="" class="form-control"/>
                        </div>
                        <div class="col-xs-2 col-sm-2">
                            <g:submitButton class="btn btn-primary" name="submit" value="Verify"/>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
