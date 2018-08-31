var utilityModule = (function ($) {

    var $messageDiv = $('#message');
    var $messageText = $('#messageText');


    var showMessage = function(type, message){

        if (type && type === "info") {
            $("#message").removeClass(function (index, css) {
                return (css.match(/(^|\s)alert\S+/g) || []).join(' ');
            }).addClass("alert alert-success");
        } else if (type && type === "warning") {
            $("#message").removeClass(function (index, css) {
                return (css.match(/(^|\s)alert\S+/g) || []).join(' ');
            }).addClass("alert alert-warning");
        } else if (type && type === "error") {
            $("#message").removeClass(function (index, css) {
                return (css.match(/(^|\s)alert\S+/g) || []).join(' ');
            }).addClass("alert alert-danger");
        } else {
            $("#message").removeClass(function (index, css) {
                return (css.match(/(^|\s)alert\S+/g) || []).join(' ');
            }).addClass("alert alert-success");
        }

        $messageText.text(message);
        if($messageDiv.hasClass('d-none')){
            $messageDiv.removeClass('d-none')
        }
    };

    var hideMessage = function(){
        if(!$messageDiv.hasClass('d-none')){
            $messageDiv.addClass('d-none')
        }
    };

    var modalDialog = function(options){
        
        var opts = $.extend({
            title: '', body: '', icon: 'info-circle', buttons: [
                {title: 'Ok', type: 'default', click: null}
            ]
        }, options);

        var $modal = $("<div class='modal' tabindex='-1' role='dialog'>" +
                "<div class='modal-dialog modal-dialog-center' role='document'>" +
                "<div class='modal-content'>" +
                "<div class='modal-header'>" +
                "<h3 class='modal-title'>&nbsp;&nbsp;" + opts.title + "</h3>" +
                "</div>" +
                "<div class='modal-body'>" + opts.body + "</div>" +
                "<div class='modal-footer'></div>" +
                "</div>" +
                "</div>" +
                "</div>");

        if (opts.icon !== null) {
            $("<span class='fa fa-" + opts.icon + "'></span>").prependTo($modal.find(".modal-title"));
        }

        var $footer = $modal.find(".modal-footer");

        $.each(opts.buttons, function(index, val){
            var b = $.extend({title: 'Ok', type: 'default', click: null}, opts.buttons[index]);
            var btn = $("<button type='button' class='btn btn-" + b.type + "' data-dismiss='modal'>" + b.title + "</button>");
            btn.on('click', b.click).appendTo($footer);
        });

        $modal.on('hidden.bs.modal', function () {
            $modal.remove();
        });

        $modal.appendTo("body").modal({backdrop: 'static', keyboard: false, show: true});

        return $modal;

    };

    return {
        modalDialog: modalDialog,
        showMessage: showMessage,
        hideMessage: hideMessage
    }

}(jQuery));










