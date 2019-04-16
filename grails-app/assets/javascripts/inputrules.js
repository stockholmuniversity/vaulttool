$(document).ready(function() {
    var inputRules = (function() {
        var initEventListeners = function() {
            $('.allowonly7bit').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');
            });
            $('.allowonly7bitpath').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z/0-9]/g, '');
                if(this.value.startsWith("/")) {
                    this.value = this.value.substr(1);
                }
                this.value = this.value.replace("//", "/")
            });
            $('.eppnformat').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z0-9@.]/g, '');
            });
            $('.cellphone').on('input', function (event) {
                this.value = this.value.replace(/[^0-9+]/g, '');
            });
        }

        var initModule = function() {
            initEventListeners();
        };

        return {
            initModule: initModule()
        };
    })();

    inputRules.initModule;
});



