$(document).ready(function(){
    $('#importZipInputFileId').on('change', function() {
        var fileName = $(this).val().replace(/C:\\fakepath\\/i, '');
        if($("#adminUploadFile").hasClass('d-none')){
            $("#adminUploadFile").removeClass('d-none');
        }
        $('#adminFileSelected').html(fileName);

    });

});