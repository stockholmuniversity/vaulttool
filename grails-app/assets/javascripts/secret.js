$(document).ready(function(){
    $("#pwdCheckbox").change(function(){

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
    $("#copyPwd").click(function(event) {
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
    $("#copyUsername").click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        var userName = $("#username").val();
        var tmpInput = '<input id="hiddenusername" value="' + userName + '" type="text"/>';
        $("body").append(tmpInput);

        $("#hiddenusername").select();
        document.execCommand("copy");
        $("#hiddenusername").remove();
    });



    $(document).on('change','#attachment', function() {
        var fileName = $(this).val().replace(/C:\\fakepath\\/i, '');
        if($("#uploadFile").hasClass('d-none')){
            $("#uploadFile").removeClass('d-none');
        }
        $('#fileSelected').html(fileName);

    });

    $("[name='description']").height($("[name='description']").prop('scrollHeight'));
    
    $("[name='description']").on('keyup focus', function (event) {
        $(this).css('height', 'auto');
        $(this).height(this.scrollHeight);
    });


    $("#createSecretSubmit").bind('click', function(event){
        event.preventDefault();

        var selectedPath = $('[name="selectedPath"]').val();
        var path = $('#path').val();
        var secret = $('#secret').val();

        $.ajax({
            type: "POST",
            url: "/dashboard/createSecret",
            data: { selectedPath: selectedPath,
                path        : path,
                secret      : secret},
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {

            },
            complete: function(){
                saveSecret();
            }
        });

    });

    function saveSecret(){
        $('#saveSecretSubmit').bind('click', function(event){
            event.preventDefault();

            var key         = $('[name="key"]').val();
            var title       = $('#title').val();
            var description = $('#description').val();
            var userName    = $('#username').val();
            var password    = $('#password').val();

            $.ajax({
                type: "POST",
                url: "/dashboard/updateSecret",
                data: { key         : key,
                    path        : title,
                    description : description,
                    userName    : userName,
                    password    : password},
                success: function (data) {
                    $('#dashboard').html(data);
                },
                error: function(data) {

                }
            });

        });

    }

    showSecret();

    $(document).on('click', '#deleteSecretSubmit', function(){
        event.preventDefault();
        showModal();
    });


    function deleteSecret(){
        var key = $('[name="key"]').val();

        $.ajax({
            type: "POST",
            url: "/dashboard/delete",
            data: { key : key},
            success: function (data) {
                $('#dashboard').html(data);
            },
            error: function(data) {}
        });
    }

    function showSecret(){
        $(".secretsListLink").bind('click', function(event){
            event.preventDefault();
            var key = $(this).data('key');

            $.ajax({
                type: "POST",
                url: "/dashboard/secret",
                data: { key : key},
                success: function (data) {
                    $('#dashboard').html(data);
                },
                error: function(data) {

                }
            });
        });
    }


    function showModal(){

        var key = $('[name="key"]').val();

        $("<div class='modal' tabindex='-1' role='dialog' id='deleteModal'>" +
                "<div class='modal-dialog modal-dialog-center' role='document'>" +
                "<div class='modal-content'>" +
                "<div class='modal-header'>" +
                "<h3 class='modal-title'>" +
                "<span class='fa fa-exclamation-triangle'></span>&nbsp;&nbsp;" +
                "Delete secret" +
                "</h3>" +
                "</div>" +
                "<div class='modal-body'>" +
                "<div class='bottom-margin-medium'>" + "Vill du ta bort " + key + "?" + "</div>" +
                "</div>" +
                "<div class='modal-footer'>" +
                "<button type='button' class='btn btn-primary' data-dismiss='modal'>"+ "Avbryt" + "</button>"+
                "<button id='deleteButton' type='button' class='btn btn-default' data-dismiss='modal'>"+ "Ja" + "</button>"+
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>").modal('show');

        $('#deleteButton').on('click', function(){
            deleteSecret();
        });

    }




});