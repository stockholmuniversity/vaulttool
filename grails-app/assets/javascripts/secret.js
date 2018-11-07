$(document).ready(function(){

    $(document).on('mouseup', function(event){
        var $userInfoMenu       = $('#userInfoMenu');
        var $userInfoToggle     = $("#userInfoToggle");
        var $userInfoToggleIcon = $('#userInfoToggleIcon');

        if($userInfoToggleIcon.is(event.target)){
            $userInfoMenu.toggleClass('d-none');
            $userInfoToggle.toggleClass('userInfoToggled');
        } else if (!$userInfoToggleIcon.is(event.target) && !$userInfoMenu.is(event.target) && $userInfoMenu.has(event.target).length === 0) {
            $userInfoMenu.addClass('d-none');
            $userInfoToggle.removeClass('userInfoToggled');
        }
    });

    //Scroll to top
    var $scrollToTop = $('.scrollTop');
    $(window).scroll(function(){
        if($(this).scrollTop() > 800){
            $scrollToTop.fadeIn();
        } else {
            $scrollToTop.fadeOut();
        }
    });

    $scrollToTop.click(function(event){
        event.preventDefault();
        $('html, body').animate({scrollTop:0},800);
    });


    $('body').on('change', '#pwdCheckbox', function(){
        
        // Check the checkbox state
        if($(this).is(':checked')){

            // Changing type attribute
            $("#password").attr("type","text");

            // Change the Text
            $("#toggleText").text("Hide password");
        }else{
            // Changing type attribute
            $("#password").attr("type","password");

            // Change the Text
            $("#toggleText").text("Show password");
        }

    });
    $(document).on('click', '#copyPwd', function(event) {
        event.preventDefault();
        event.stopPropagation();
        var pwd = $("#password").val();
        var tmpInput = '<input id="hiddenpwd" value="' + pwd + '" type="text"/>';
        $("body").append(tmpInput);

        $("#hiddenpwd").select();
        document.execCommand("copy");
        $("#hiddenpwd").remove();
        console.debug("hej");
    });
    
    $(document).on('click','#copyUsername', function(event) {
        event.preventDefault();
        event.stopPropagation();
        var userName = $("#username").val();
        var tmpInput = '<input id="hiddenusername" value="' + userName + '" type="text"/>';
        $("body").append(tmpInput);

        $("#hiddenusername").select();
        document.execCommand("copy");
        $("#hiddenusername").remove();
    });


    $(document).off('change','#attachment');
    $(document).on('change','#attachment', function() {
        var fileName = $(this).val().replace(/C:\\fakepath\\/i, '');
        if($("#uploadSecretFileButton").hasClass('d-none')){
            $("#uploadSecretFileButton").removeClass('d-none');
        }
        $('#fileSelected').html(fileName);

    });

    $(document).off('change', '#importZipInputFileId');
    $(document).on('change', '#importZipInputFileId', function() {
        var fileName = $(this).val().replace(/C:\\fakepath\\/i, '');
        if($("#adminUploadFile").hasClass('d-none')){
            $("#adminUploadFile").removeClass('d-none');
        }
        $('#adminFileSelected').html(fileName);

    });



    $("[name='description']").height($("[name='description']").prop('scrollHeight'));
    
    $("[name='description']").on('keyup focus', function (event) {
        $(this).css('height', 'auto');
        $(this).height(this.scrollHeight);
    });

    $(document).off('change', '[name=selectedPath]');
    $(document).on('change', '[name=selectedPath]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        $.ajax({
            type: "POST",
            url: "/dashboard/index",
            data: $('#selectPathForm').serialize(),
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }

        });
    });


    $(document).off('change', "[name=group]");
    $(document).on('change', "[name=group]", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        $.ajax({
            type: "POST",
            url: "/public/setGroup",
            data: $('#setGroupForm').serialize(),
            success: function (data) {
                window.location.href = '/';
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }

        });

    });


    $(document).off('click', "#cancelBtn");
    $(document).on('click', "#cancelBtn", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        $.ajax({
            type: "POST",
            url: "/dashboard/index",
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }

        });

    });



    $(document).off('click', "#searchQueryInputBtn");
    $(document).on('click', "#searchQueryInputBtn", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        $.ajax({
            type: "POST",
            url: "/dashboard/search",
            data: $('#searchForm').serialize(),
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }

        });

    });



    $(document).off('click', "#createSecretSubmitBtn");
    $(document).on('click', "#createSecretSubmitBtn", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var selectedPath = $('[name="selectedPath"]').val();
        var path = $('#path').val();
        var secret = $('#secret').val();

        $.ajax({
            type: "POST",
            url: "/dashboard/createSecret",
            data: { selectedPath    : selectedPath,
                    path            : path,
                    secret          : secret},
            success: function (data) {
                $('#dashboard').html(data);
                var key = $('#key').val();
                utilityModule.showMessage('info', 'Successfully created secret ' + key);

                //Deselect active node and then refresh to prevent active node from closing since jstree.select_node is fired on refresh.
                // (jstree makes node active again after refresh)
                var node = selectedPath.split("/")[0];
                var selected = $("#navTree").jstree(true).get_selected();

                $("#navTree").jstree(true).deselect_node(selected);
                $("#navTree").jstree(true).refresh_node(node);

            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }

        });

    });

    $(document).off('click', '#saveSecretSubmit');
    $(document).on('click', '#saveSecretSubmit', function(event){
            event.preventDefault();
            utilityModule.hideMessage();

            var key = $('[name="key"]').val();

            $.ajax({
                type: "POST",
                url: "/dashboard/updateSecret",
                data: $('#saveSecretForm').serialize(),
                success: function (data) {
                    $('#dashboard').html(data);
                    utilityModule.showMessage('info', 'Successfully updated secret ' + key);
                },
                error: function(data) {
                    utilityModule.showMessage('error', data.responseText);
                    console.log(data.responseText);
                }
            });

        });


    $(document).off('click', '#deleteSecretSubmit');
    $(document).on('click', '#deleteSecretSubmit', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var key = $('[name="key"]').val();

        utilityModule.modalDialog({
            icon: 'exclamation-triangle',
            title: 'Delete secret',
            body: 'Vill du ta bort ' + key + "?",
            buttons : [
                {
                    title: 'Avbryt',
                    type: 'primary',
                    click: null
                },
                {
                    title: 'Ja',
                    type: 'default',
                    click: function(){deleteSecret();}
                }
            ]
        });
    });
    
    $(document).off('click', '.jstree-leaf');
    $(document).on('click', '.jstree-leaf', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var key = $(this).find('a').data('secretkey');
        
        $.ajax({
            type: "POST",
            url: "/dashboard/secret",
            data: { key : key},
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
        
    });

    $(document).off('click', ".secretsListLink");
    $(document).on('click', ".secretsListLink", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var key = $(this).data('key');

        $.ajax({
            type: "POST",
            url: "/dashboard/secret",
            data: { key : key},
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
    });

    $(document).off('click', '#sudoButton');
    $(document).on('click', '#sudoButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        
        $.ajax({
            type: "POST",
            url: "/admin/sudo",
            data: $('#sudoForm').serialize(),
            success: function (data) {
                $('#dashboard').html(data);
                if($('#disableSudo').hasClass('d-none')){
                    $('#disableSudo').removeClass('d-none');
                }
                window.location.href = '/';
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
    });

    $(document).off('click', '[name=disableSudoModeLink]');
    $(document).on('click', '[name=disableSudoModeLink]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        
        $.ajax({
            type: "POST",
            url: "/public/disableSudo",
            success: function (data) {
                $('#dashboard').html(data);
                if(!$('#disableSudo').hasClass('d-none')){
                    $('#disableSudo').addClass('d-none');
                }
                window.location.href = '/';
            },
            error: function(data) { }
        });

    });

    $(document).off('click', '#adminUploadFile');
    $(document).on('click', '#adminUploadFile', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var formData = new FormData($('#importZip')[0]);
        $.ajax({
            type: "POST",
            url: "/admin/importZip",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                $('#dashboard').html(data);
                if($('#disableSudo').hasClass('d-none')){
                    $('#disableSudo').removeClass('d-none');
                }
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
    });

    $(document).off('click', '#uploadSecretFileButton');
    $(document).on('click', '#uploadSecretFileButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var formData = new FormData($('#uploadSecretFileForm')[0]);
        
        $.ajax({
            type: "POST",
            url: "/dashboard/upload",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
    });

    $(document).off('click', '#deleteFileButton');
    $(document).on('click', '#deleteFileButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var key = $('[name="key"]').val();

        $.ajax({
            type: "POST",
            url: "/dashboard/deleteFile",
            data: $('#deleteFileForm').serialize(),
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully updated secret ' + key);
            },
            error: function(data) {
                utilityModule.showMessage('error',data.responseText);
                console.log(data.responseText);
            }
        });
    });

    $(document).off('click', '#createUserButton');
    $(document).on('click', '#createUserButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var eppn    = $('#eppn').val();
        var sms     = $('#sms').val();
        
        $.ajax({
            type: "POST",
            url: "/admin/createUser",
            data: { eppn    : eppn,
                    sms     : sms},
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully created/updated user ' + eppn);
            },
            error: function(data) {
               utilityModule.showMessage('error', data.responseText);
               console.log(data.responseText);
            }
        });
       
    });

    $(document).off('click', '.deleteUserLink');
    $(document).on('click', '.deleteUserLink', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var key = $(this).data('key');

        $.ajax({
            type: "POST",
            url: "/admin/deleteUser",
            data: { key : key},
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully deleted user ' + key);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });

    });

    $(document).off('click', '#createUpdatePolicyButton');
    $(document).on('click', '#createUpdatePolicyButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var policy = $('#name').val();

        $.ajax({
            type: "POST",
            url: "/admin/createPolicy",
            data : $("#createPolicyForm").serialize(),
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully created policy ' + policy);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });

    });

    $(document).off('click', '.deletePolicyLink');
    $(document).on('click', '.deletePolicyLink', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var policy = $(this).data('policy');
        
        $.ajax({
            type: "POST",
            url: "/admin/deletePolicy",
            data : {policy:policy},
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info', 'Successfully deleted policy ' + policy);
            },
            error: function(data) {
                utilityModule.showMessage('error',data.responseText);
                console.log(data.responseText);
            }
        });

    });
    

    $(document).off('click', '#createUpdateApproleButton');
    $(document).on('click', '#createUpdateApproleButton', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var appRoleName = $('#name').val();

        $.ajax({
            type: "POST",
            url: "/admin/createApprole",
            data : $("#createApproleForm").serialize(),
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully created approle ' + appRoleName);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });

    });

    $(document).off('click', '.deleteApproleLink');
    $(document).on('click', '.deleteApproleLink', function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var approle = $(this).data('approle');
        $.ajax({
            type: "POST",
            url: "/admin/deleteApprole",
            data : {approle:approle},
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info','Successfully deleted approle ' + approle);
            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });

    });

    $(document).off('click', '#backToIndexLink');
    $(document).on('click', '#backToIndexLink', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        navigateViews('/dashboard/index', this);
    });

    $(document).off('click', '[name=admin]');
    $(document).on('click', '[name=admin]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        navigateViews('/admin/index', this);
    });

    $(document).off('click', '[name=user]');
    $(document).on('click', '[name=user]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        navigateViews('/admin/user', this);
    });

    $(document).off('click', '[name=policiesAdmin]');
    $(document).on('click', '[name=policiesAdmin]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        navigateViews('/admin/policies', this);
    });
    
    $(document).off('click', '[name=approles]');
    $(document).on('click', '[name=approles]', function(event){
        event.preventDefault();
        utilityModule.hideMessage();
        navigateViews('/admin/approles', this);
    });
    

    function navigateViews(url, btn){
        $.ajax({
            type: "POST",
            url: url,
            success: function (data) {
                $('#dashboard').html(data);

                var buttons = $('.activeAdminBtn');
                $.each(buttons, function(index,val){
                    $(val).removeClass('activeAdminBtn');
                });
                $(btn).addClass('activeAdminBtn');
            },
            error: function(data) {

            }
        });
    }

    function deleteSecret(){
        var key = $('[name="key"]').val();

        $.ajax({
            type: "POST",
            url: "/dashboard/delete",
            data: { key : key},
            success: function (data) {
                $('#dashboard').html(data);
                utilityModule.showMessage('info', 'Successfully deleted secret ' + key);

                //Deselect active node and then refresh to prevent active node from closing since jstree.select_node is fired on refresh.
                // (jstree makes node active again after refresh)
                var node = key.split("/")[0];
                var selected = $("#navTree").jstree(true).get_selected();

                $("#navTree").jstree(true).deselect_node(selected);
                $("#navTree").jstree(true).refresh_node(node);

            },
            error: function(data) {
                utilityModule.showMessage('error', data.responseText);
                console.log(data.responseText);
            }
        });
    }


    

});