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
            'search': {
                'ajax' : {
                    'url' : '/dashboard/treeSearch',
                    'data' : function(str){
                    return {'mystring': str}
                    }
                }

                }
            },
            'plugins': ['search', 'themes']
        
    });


    //Search plugin
    $('#quickSearch').keyup(function () {
        var value = $('#quickSearch').val();
        console.log(value);
        //$('#navTree').jstree('search', value);
        $('#navTree').jstree(true).search(value, true);
        console.log("after");
        return false;

    });

    //Expand path with left click on node
    $('#navTree').on('select_node.jstree', function(e, data){
       data.instance.toggle_node(data.node);
    });

    //Click on secret to navigate to it's view
    $('#navTree').on('select_node.jstree', function(e, data){
        if(data.instance.is_leaf(data.node)){
            var key = $("#" + data.node.a_attr.id).data('secretkey');
            window.location.href = '/dashboard/secret?key='+ key;
        } else{
            var path = (data.node.id !== 'root') ? data.node.id.replace(/_/g,'/'):'';
            window.location.href = '/dashboard/index?selectedPath='+ path;

        }
    });

});

