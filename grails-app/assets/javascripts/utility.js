var utilityModule = (function ($) {

    

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
           modalDialog: modalDialog
    }

}(jQuery));









