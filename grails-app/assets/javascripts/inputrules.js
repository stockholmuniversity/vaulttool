$(document).ready(function() {
    var inputRules = (function() {
        var initEventListeners = function() {
            $(".allowonly7bit").off('paste');
            $(".allowonly7bit").on('paste', function(event){
                var element = this;
                setTimeout(function () {
                    element.value = element.value.replace(/[^a-zA-Z0-9]/g, '');
                    // do something with text
                }, 100);
            });
            $(".allowonly7bitpath").off('paste');
            $(".allowonly7bitpath").on('paste', function(event){
                var element = this;
                setTimeout(function () {
                    element.value = element.value.replace(/[^a-zA-Z0-9]/g, '');
                    // do something with text
                }, 100);
            });
            $(".allowonly7bit").off('input');
            $('.allowonly7bit').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');
            });
            $(".allowonly7bitpath").off('input');
            $('.allowonly7bitpath').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z/0-9]/g, '');
                if(this.value.startsWith("/")) {
                    this.value = this.value.substr(1);
                }
                this.value = this.value.replace("//", "/")
            });
            $(".eppnformat").off('input');
            $('.eppnformat').on('input', function (event) {
                this.value = this.value.replace(/[^a-zA-Z0-9@.]/g, '');
            });
            $(".cellphone").off('input');
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



