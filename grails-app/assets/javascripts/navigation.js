$(document).ready(function(){
    $("#userInfoToggle").on('click', function(e){
    console.log("foo");
        $("#userInfoMenu").toggleClass('hidden');
        $("#userInfoToggle").toggleClass('userInfoToggled');
    });
    

});