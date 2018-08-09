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
                'label' : 'Cut Path',
                'icon'  : 'fa fa-scissors',
                'action': function(){
                    sessionStorage.setItem('deletePath', 'true');
                    sessionStorage.setItem('fromPath', node.id.replace(/_/g,'/'));
                    sessionStorage.setItem('enablePaste','true');

                }
            },
            'item2' : {
                'label' : 'Copy Path',
                'icon'  : 'fa fa-files-o',
                'action': function(){
                    sessionStorage.removeItem('deletePath');
                    sessionStorage.setItem('fromPath', node.id.replace(/_/g,'/'));

                    sessionStorage.setItem('enablePaste','true');
                }
            },
            'item3' : {
                'label' : 'Paste Path',
                'icon'  : 'fa fa-clipboard',
                'action': function(){
                    sessionStorage.setItem('toPath', node.id.replace(/_/g,'/'));

                    handlePaths();
                    sessionStorage.removeItem('enablePaste');
                    }
            },
            'item4' : {
                    'label' : 'Delete Path',
                    'icon' : 'fa fa-trash-o',
                    'separator_after' : true,
                    'action' : function(){
                        sessionStorage.removeItem('toPath');
                        sessionStorage.removeItem('deletePath');
                        sessionStorage.setItem('fromPath', node.id.replace(/_/g,'/'));

                        handlePaths();
                    }
            },
            'item5' : {
                'label' : 'Administration',
                'icon'  : 'fa fa-cogs',
                'submenu' : {
                    'subItem1' : {
                        'label' : 'Users',
                        'icon': 'fa fa-users'
                    },
                    'subItem2' : {
                        'label' : 'Policies',
                        'icon': 'fa fa-file-text-o'
                    },
                    'subItem3' : {
                        'label' : 'Application Roles',
                        'icon': 'fa fa-puzzle-piece'
                    }
                }
            }
        };

        if(node.type === 'leafNode' || node.type === 'rootNode'){
            items.item1._disabled = true;
            items.item2._disabled = true;
            items.item3._disabled = true;
            items.item4._disabled = true;
        }

        if(node.type !== 'leafNode'){
            if(sessionStorage.enablePaste){
                items.item3._disabled = false;
            } else {
                items.item3._disabled = true;
            }
        }

        items.item5._disabled = (!node.original.admin);

        return items;
    }

   function handlePaths(){
        var fromPath = (sessionStorage.fromPath) ? sessionStorage.fromPath:'';
        var toPath = (sessionStorage.toPath) ? sessionStorage.toPath:'';
        var deletePath = (sessionStorage.deletePath) ? sessionStorage.deletePath:'';

       $.ajax({
           type    : "POST",
           url     : "/dashboard/handlePaths",
           data    : {  fromPath    :   fromPath,
                        toPath      :   toPath,
                        deletePath  :   deletePath},
           success: function (data) {
               var $navTree    =  $("#navTree");

               if(deletePath || (fromPath && !toPath)) {
                   var fromNode =   $navTree.jstree(true).get_node(fromPath.replace(/\//g,'_'));
                   var children = fromNode.children;
                   
                   if(children.length > 0){
                       $navTree.jstree(true).delete_node(children);
                   }
                   $navTree.jstree(true).delete_node(fromNode.id);
               }

               if(fromPath && toPath){
                  $navTree.jstree(true).load_node(toPath.replace(/\//g,'_'));
                  $navTree.jstree(true).open_node(toPath.replace(/\//g,'_'));
               }
           },
           error: function(data) {
               console.log(data.message);
           }
       });

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

