$(document).ready(function(){

    $('#navTree').jstree({
        'core': {
            'check_callback': true,
            'data': {
                'url': function(node){
                    //console.log(node.id);
                    return node.id === '#' ? '/dashboard/loadRootPaths': '/dashboard/loadChildren'
                },
                'dataType': 'json',
                'data': function(node){
                    //console.log(node.id);
                    return {'id': node.id, 'foo':'foo'}
                }
            },
            'themes': {
                'theme': 'default'
            },
            'plugins': ['search', 'conditionalselect', 'themes']
        }
    });


    //Click secret
    $('body').off('click');
    $('body').on('click', '.jstree-anchor',function(event){
        var leaf = this;

        if($(leaf).closest('li').hasClass('jstree-leaf')){
            window.location.href = '/dashboard/secret?key='+$(leaf).data('secretkey');
        }

        /*if($(leaf).closest("li").hasClass("jstree-leaf")){
            window.location.href = '/dashboard/secret?key'+$(leaf).data('secretkey');
            $.ajax({
                type: "POST",
                url: "/dashboard/secret",
                data: {key: $(leaf).data('secretkey')},
                success: function (data) {
                   $("").html(data);
                },
                error: function(data) {
                    console.log(data.message);
                }
            });
        }
*/



    });

    //Search plugin
    var timeOut = 0;
    $('#quickSearch').keyup(function () {
        if(timeOut) {
            clearTimeout(timeOut);
        }
        timeOut = setTimeout(function () {
            var value = $('#quickSearch').val();
            $('#testTree').jstree(true).search(value);
        }, 250);
    });
});

/*'conditionalselect' : function(node, event){

            console.log(node);

            return (node.text.trim() !== 'Child node 1');

        },*/