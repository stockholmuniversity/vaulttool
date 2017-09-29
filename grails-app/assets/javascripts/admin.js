$(document).ready(function(){
    $('#importZipInputFileId').on('change', function() {
        var fileName = $(this).val().replace(/C:\\fakepath\\/i, '');
        if($("#adminUploadFile").hasClass('hidden')){
            $("#adminUploadFile").removeClass('hidden');
        }
        $('#adminFileSelected').html(fileName);

    });

});