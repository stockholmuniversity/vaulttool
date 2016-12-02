$(document).ready(function() {
    var indexModule = (function() {
        var initEventListeners = function() {
            $('.allowonly7bit').on('input', function (event) {
                this.value = this.value.replace(/^[^a-zA-Z]*$/g, '');
            });
        }

        var initModule = function() {
            initEventListeners();
        };

        return {
            initModule: initModule()
        };
    })();

    indexModule.initModule;
});



