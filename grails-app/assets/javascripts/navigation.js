$(document).ready(function(){

    $(document).off('click', '#userInfoToggle');
    $(document).on('click', '#userInfoToggle',function(e){
        $("#userInfoMenu").toggleClass('d-none');
        $("#userInfoToggle").toggleClass('userInfoToggled');
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

});