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
            'plugins': ['search', 'themes', 'contextmenu','types','wholerow']
        
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

                        showModal();
                    }
            },
            'item5' : {
                'label' : 'Administration',
                'icon'  : 'fa fa-cogs',
                'submenu' : {
                    'subItem1' : {
                        'label' : 'Administration',
                        'icon': 'fa fa-tasks',
                        'action' : function(){
                            window.location.href = '/admin/index';
                        }
                    },
                    'subItem2' : {
                        'label' : 'Users',
                        'icon': 'fa fa-users',
                        'action' : function(){
                            window.location.href = '/admin/user';
                        }
                    },
                    'subItem3' : {
                        'label' : 'Policies',
                        'icon': 'fa fa-file-text-o',
                        'action' : function(){
                            window.location.href = '/admin/policies';
                        }
                    },
                    'subItem4' : {
                        'label' : 'Application Roles',
                        'icon': 'fa fa-puzzle-piece',
                        'action' : function(){
                            window.location.href = '/admin/approles';
                        }
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

        if(node.type !== 'leafNode' && node.type !== 'rootNode'){
            if(sessionStorage.enablePaste){
                items.item3._disabled = false;
            } else {
                items.item3._disabled = true;
            }
        }

        items.item5._disabled = (!node.original.admin);

        return items;
    }

   function showModal(){
        $("<div class='modal' tabindex='-1' role='dialog' id='deleteModal'>" +
                "<div class='modal-dialog modal-dialog-center' role='document'>" +
                "<div class='modal-content'>" +
                "<div class='modal-header'>" +
                "<h3 class='modal-title'>" +
                "<span class='fa fa-exclamation-triangle'></span>&nbsp;&nbsp;" +
                "Delete path" +
                "</h3>" +
                "</div>" +
                "<div class='modal-body'>" +
                "<div class='bottom-margin-medium'>" + "Vill du ta bort " + sessionStorage.fromPath + "?" + "</div>" +
                "<div class='bottom-margin-medium'>"  + sessionStorage.fromPath + " samt alla paths och alla secrets under " + sessionStorage.fromPath + " kommer att tas bort." +"</div>"+
                "<div>" + "Vill du fortsätta?" + "</div>"+
                "</div>" +
                "<div class='modal-footer'>" +
                "<button type='button' class='btn btn-primary' data-dismiss='modal'>"+ "Avbryt" + "</button>"+
                "<button id='deleteButton' type='button' class='btn btn-default' data-dismiss='modal'>"+ "Ja" + "</button>"+
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>").modal('show');

        $('#deleteButton').on('click', function(){
           handlePaths();
        });

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

   function removeWholeRowClasses(){
        var $navTree    = $("#navTree");
        var el1         = $navTree.find('div.jstree-wholerow-clicked');
        var el2         = $navTree.find('a.jstree-clicked');

        $.each(el1, function(index, val){
           $(val).removeClass('jstree-wholerow-clicked');
        });

        $.each(el2, function(index, val){
           $(val).removeClass('jstree-clicked');
        });
   }
   
   function addWholeRowClasses(){
       var nodeId = sessionStorage.forceRowClass;

       $('#' + nodeId + '> div').addClass('jstree-wholerow-clicked');
       $('#' + nodeId + '> a').addClass('jstree-clicked');
   }


    $('#navTree').on('loaded.jstree', function(event){
       $('#root > div').addClass('jstree-wholerow-clicked');
       $('#root > a').addClass('jstree-clicked');

        //This works to open all nodes. However selecting the last node is not what wee want
        /*$('#navTree').jstree(true).load_node(['SchemaTool','SchemaTool_foo'], function(node, status){
            console.log(node);
            $('#navTree').jstree(true).open_node('SchemaTool_foo_vantar', function(){},false);
            $('#navTree').jstree(true).select_node('SchemaTool_foo_vantar');
        });*/
        
        if(sessionStorage.openItems){

            var nodeArr = JSON.parse(sessionStorage.openItems);
            
            //THis works to open parent nodes. don't delete until the rest of the function is fixed.
            $.each(nodeArr, function(index,val){
                $('#navTree').jstree(true).load_node(val);
                $('#navTree').jstree(true).open_node(val);
            });
            
        }
    });



    $('#navTree').on("click.jstree", function (event) {

        //Toggle folder icon when clicking on the arrow
        var node =  $('#navTree').jstree(true).get_node(event.target.parentNode.id);

        if(node.id !== 'root'){
            if($('#' + node.id).hasClass('jstree-open') || $('#' + node.id).hasClass('jstree-loading')){
                $("#navTree").jstree(true).set_icon(node.id, 'fa fa-folder-open');
            } else if($('#' + node.id).hasClass('jstree-closed')) {
                $("#navTree").jstree(true).set_icon(node.id, 'fa fa-folder');
            }

            removeWholeRowClasses();
            sessionStorage.setItem('forceRowClass',node.id);

        } else {
            window.location.href = '/dashboard/index';
        }
        
    });

    //Handle the display of active node
    $('#navTree').on('after_open.jstree', function(event){
        removeWholeRowClasses();
        addWholeRowClasses();
    });

    //Handle the display of active node 
    $('#navTree').on('after_close.jstree', function(event){
        removeWholeRowClasses();
        addWholeRowClasses();
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

        if(data.instance.is_leaf(data.node)){

           //Save open nodes to be opened later on reload after click secret
            var el = $("#navTree").find('li.jstree-open');
            var nodeArr = [];
            $.each(el, function(index, val){
                nodeArr.push($(val).attr('id'));
            });
            nodeArr = nodeArr.filter(function(item){
                return item !== 'root'
            });
            sessionStorage.setItem('openItems',JSON.stringify(nodeArr));


            //Load secrets view
            var key = $("#" + data.node.a_attr.id).data('secretkey');
            window.location.href = '/dashboard/secret?key='+ key;
        }


       if(data.node.type === 'rootNode'){
           window.location.href = '/dashboard/index';

           if(sessionStorage.openItems){
               sessionStorage.removeItem('openItems');
           }
       }

    });

});

