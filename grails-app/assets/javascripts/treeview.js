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
        'contextmenu':{
            'select_node' : false,
            'items' : customMenu
        },
        'types' : {
            'pathNode' : {},
            'rootNode' : {},
            'leafNode' : {}
        },
            'plugins': ['search', 'themes', 'contextmenu','types']
        
    });


    //Customised context menu
    function customMenu(node){

        var items = {
            'item1' : {
                'label' : 'Copy Path'
            },
            'item2' : {
                'label' : 'Paste Path'
            },
            'item3' : {
                    'label' : 'Delete Path',
                    'separator_after' : true,
                    'action' : {}
            },
            'item4' : {
                'label' : 'Administration',
                'submenu' : {
                    'subItem1' : {
                        'label' : 'User'
                    },
                    'subItem2' : {
                        'label' : 'Policies'
                    },
                    'subItem3' : {
                        'label' : 'Application Roles'
                    }
                }
            }
        };

        if(node.type === 'leafNode'){
            items.item1._disabled = true;
            items.item2._disabled = true;
            items.item3._disabled = true;
        } else if (node.type === 'rootNode'){
            items.item1._disabled = true;
            items.item2._disabled = true;
            items.item3._disabled = true;
        }

        items.item4._disabled = (!node.original.admin);

        return items;
    }


    //Delete path
    function deletePath(path){
        
    }

    //Search plugin
    $('#quickSearch').keyup(function () {
        var value = $('#quickSearch').val();
        console.log(value);
        //$('#navTree').jstree('search', value);
        $('#navTree').jstree(true).search(value, true);
        console.log("after");
        return false;

    });

    //Expand and collapse path with left click on node
    $('#navTree').on('select_node.jstree', function(e, data){
       data.instance.toggle_node(data.node);

       if(!data.instance.is_leaf(data.node) && data.node.type !== 'rootNode'){
           if(data.instance.is_loading(data.node) || data.instance.is_open(data.node)){
               data.instance.set_icon(data.node.id, 'fa fa-folder-open');
           } else {
               data.instance.set_icon(data.node.id, 'fa fa-folder');
           }
       }
    });

    //Click on secret to navigate to it's view
    $('#navTree').on('select_node.jstree', function(e, data){
        if(data.instance.is_leaf(data.node)){
            var key = $("#" + data.node.a_attr.id).data('secretkey');
            window.location.href = '/dashboard/secret?key='+ key;
        } else{
            var path = (data.node.id !== 'root') ? data.node.id.replace(/_/g,'/'):'';
            //window.location.href = '/dashboard/index?selectedPath='+ path;

        }
    });

});

