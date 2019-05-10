$(document).ready(function(){
    var $tree = $('#navTree');

    $tree.jstree({
        'core': {
            'check_callback': true,
            'data': {
                'url': function(node){return node.id === '#' ? '/dashboard/loadRootPaths': '/dashboard/loadChildren'},
                'dataType': 'json',
                'data': function(node){return {'id': node.id}
                }
            },
            'themes': {
                'theme': 'default'
            }},
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

                    overWriteCheck();
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
            }
        };

        if(node.type === 'leafNode' || node.type === 'rootNode'){
            items.item1._disabled = true;
            items.item2._disabled = true;
            items.item3._disabled = true;
            items.item4._disabled = true;
        }

        if(node.type !== 'leafNode' && node.type !== 'rootNode'){
            items.item3._disabled = (!sessionStorage.enablePaste)
        }

        //TODO:The cut function should be disabled for now. We need to check the capabilities of the user on the recipient path when cutting and pasting.
        items.item1._disabled = true;

        return items;
    }

   function showModal(){

         utilityModule.modalDialog({
             icon: 'exclamation-triangle',
             title: 'Delete path',
             body:  "<div class='bottom-margin-medium'>" + "Vill du ta bort " +"<strong>" + sessionStorage.fromPath +"</strong>"+ "?" + "</div>" +
                    "<div class='bottom-margin-medium'>" + sessionStorage.fromPath + " samt alla paths och alla secrets under " + sessionStorage.fromPath + " kommer att tas bort." +"</div>"+
                    "<div>" + "Vill du fortsätta?" + "</div>",
             buttons : [
                 {
                     title: 'Avbryt',
                     type: 'primary',
                     click: null
                 },
                 {
                     title: 'Ja',
                     type: 'default',
                     click: function(){
                         handlePaths();}
                 }
             ]

         });

   }

   function overWriteCheck(){
       var fromPath = (sessionStorage.fromPath) ? sessionStorage.fromPath:'';
       var toPath = (sessionStorage.toPath) ? sessionStorage.toPath:'';
       
       $.ajax({
           type: 'POST',
           url: '/dashboard/overWriteCheck',
           data: {fromPath : fromPath,
           toPath : toPath},
           success: function(data){
               console.log(data);
               if(data === 'danger'){

                   utilityModule.modalDialog({
                       icon: 'exclamation-triangle',
                       title: 'Copy path',
                       body:  "<div class='bottom-margin-medium'>" + "Det finns redan en path med samma namn. "  + "Secrets under denna path kan komma att skrivas över." + "</div>" +
                       "<div>" + "Vill du fortsätta?" + "</div>",
                       buttons : [
                           {
                               title: 'Avbryt',
                               type: 'primary',
                               click: null
                           },
                           {
                               title: 'Ja',
                               type: 'default',
                               click: function(){
                                   handlePaths();}
                           }
                       ]

                   });

               } else {
                   handlePaths();
               }
           },
           error: function(){}
       })
   }

   function handlePaths(){
        utilityModule.hideMessage();
        var fromPath = (sessionStorage.fromPath) ? sessionStorage.fromPath:'';
        var toPath = (sessionStorage.toPath) ? sessionStorage.toPath:'';
        var deletePath = (sessionStorage.deletePath) ? sessionStorage.deletePath:'';

        callServer({fromPath:fromPath, toPath:toPath, deletePath:deletePath}, 'handlePaths')
                .done(function(data){


                    if(deletePath || (fromPath && !toPath)) {
                        var fromNode = $tree.jstree(true).get_node(fromPath.replace(/\//g,'_'));
                        var children = fromNode.children;

                        if(children.length > 0){
                            $tree.jstree(true).delete_node(children);
                        }
                        var parent = $tree.jstree(true).get_parent(fromNode.id);
                        $tree.jstree(true).select_node(parent);
                        $tree.jstree(true).open_node(parent);
                        $tree.jstree(true).delete_node(fromNode.id);


                        callServer({selectedPath: parent.replace(/_/g,'/') + '/'}, 'index')
                                .done(function(data){
                                    $("#dashboard").html(data);
                                });
                    }

                    if(fromPath && toPath){
                        $tree.jstree(true).load_node(toPath.replace(/\//g,'_'));
                        $tree.jstree(true).open_node(toPath.replace(/\//g,'_'));
                    }
                }).fail(function(data){
                    //TODO: Show message to user?
                    console.log(data.message);
                });
   }

   function removeWholeRowClasses(){
        var el1         = $tree.find('div.jstree-wholerow-clicked');
        var el2         = $tree.find('a.jstree-clicked');

        $.each(el1, function(index, val){
           $(val).removeClass('jstree-wholerow-clicked');
        });

        $.each(el2, function(index, val){
           $(val).removeClass('jstree-clicked');
        });
   }
   
   function addWholeRowClasses(node){
        var $selDiv = $('#' + node.id + '> div');
        var $selLink = $('#' + node.id + '> a');
        
        //Use indexOf instead of startsWith since startsWith isn't supported in IE.
        if(node.id.indexOf('leaf') === 0 || (node.a_attr != null) && (node.a_attr.class === 'path-no-children')){
            $selDiv.addClass('jstree-wholerow-leaf');
            $selLink.addClass('jstree-clicked-leaf');
        } else {
            $selDiv.addClass('jstree-wholerow-clicked');
            $selLink.addClass('jstree-clicked');
        }
   }

    function navigatePaths(node){
        utilityModule.hideMessage();
        
        if(node.type === 'pathNode' || !$tree.jstree(true).is_leaf(node)){
            var selectedPath = node.id.replace(/_/g,"/") + "/";

            callServer({selectedPath: selectedPath}, 'index')
                    .done(function(data){
                        $("#dashboard").html(data);
                    });
        } else {
            var key = node.id.substring(5).replace(/_/g, "/");

            callServer({key : key}, 'secret')
                    .done(function(data){
                        $('#dashboard').html(data);
                    }).fail(function(data){
                        utilityModule.showMessage('error', data.responseText);
                        console.log(data.responseText);
                    });
        }

    }

    function callServer(data, url){
        return $.ajax({
            type: "POST",
            url: "/dashboard/" + url,
            data: data
        })
    }
    
    function nodeInMemory(id, text, icon, type){
        return {id  : id,
                text: text,
                icon: icon,
                type: type};
    }

    function createPathAndSecret(selectedPath, path, secret) {
        var pt          = (path) ? path.split('/'):"";
        var parentId    = selectedPath.replace(/\//g,"_").replace(/_$/,"");
        var selPath     = selectedPath;
        var fullPath    = selectedPath + path;
        var msg         = "";
        var exists      = false;

        if(path && secret){
            selPath = 'leaf/' + selPath + path + '/' + secret
        } else {
            selPath = 'leaf/' + selPath + secret
        }
        
        msg = 'Failed when trying to create secret ' + selPath.replace('leaf/',"") + ' . Error was: Secret already exist';
        selPath = selPath.replace(/\//g,"_");

        exists = $tree.jstree(true).get_node(selPath);
        if(exists){
            utilityModule.showMessage('error', msg);
            return;
        }

        exists = $tree.jstree(true).get_node(fullPath.replace(/\//g, "_"));
        if(!exists){
            if(pt.length > 0){

                var nodeId = parentId;

                $.each(pt, function(i, val){
                    var nId     = nodeId += '_' + val;
                    var ptNode  = nodeInMemory(nId, val,'fa fa-folder', 'pathNode');
                    parentId    = $tree.jstree(true).create_node(parentId, ptNode , 0, false, true);
                    nodeId      = parentId;
                });
            }

        } else {
            parentId = fullPath.replace(/\//g, "_")
        }

        var leafId = 'leaf_' + parentId + '_' + secret;
        exists = $tree.jstree(true).get_node(leafId);
        if(!exists){
            var leafNode = nodeInMemory(leafId, secret, 'fa fa-lock', 'leafNode');
            var createdLeafNode = $tree.jstree(true).create_node(parentId, leafNode , 0, false, true);
        }
        
        callServer({selectedPath: selectedPath, path: path, secret: secret}, 'createSecret')
                .done(function(data){
                    $('#dashboard').html(data);
                    var key = $('#key').val();
                    utilityModule.showMessage('info', 'Successfully created secret ' + key);

                    var selectedNodes = $tree.jstree(true).get_selected();
                    $.each(selectedNodes, function(i, val){
                        $tree.jstree(true).deselect_node(val);
                    });
                    $tree.jstree(true).select_node(createdLeafNode);

                }).fail(function(data){
                    //$("#navTree").jstree(true).refresh();
                    utilityModule.showMessage('error', data.responseText);
                    console.log(data.responseText);
                });
    }

    function createOnlyPath(selectedPath, path){
        callServer({selectedPath:selectedPath, path:path}, 'createPath')
                .done(function(data){
                    utilityModule.showMessage('info', data.success);
                    console.log(data.success);
                })
                .fail(function(data){
                    utilityModule.showMessage('error', data.responseText);
                    console.log(data.responseText);
                }).always(function(){
                    $tree.jstree(true).refresh_node(selectedPath.replace(/\//g,'_').replace(/_$/,''));
                });
    }


    //Triggered after the root node is loaded for the first time
    $tree.on('loaded.jstree', function(e){
        var node = e.target.children[0].childNodes[0];
        addWholeRowClasses(node);
    });

    $tree.on("click.jstree", function (event) {

        //Toggle folder icon when clicking on the arrow
        var node =  $tree.jstree(true).get_node(event.target.parentNode.id);

        if(node.id !== 'root'){
            if($('#' + node.id).hasClass('jstree-open') || $('#' + node.id).hasClass('jstree-loading')){
                $tree.jstree(true).set_icon(node.id, 'fa fa-folder-open');
            } else if($('#' + node.id).hasClass('jstree-closed')) {
                $tree.jstree(true).set_icon(node.id, 'fa fa-folder');
            }
            removeWholeRowClasses();
        } else {
            window.location.href = '/';
        }

        //Clear user info menu since then user has left admin and navigated to a secret
        if($tree.jstree(true).is_leaf(node)){
            var buttons = $('.activeAdminBtn');
            $.each(buttons, function(index,val){
                $(val).removeClass('activeAdminBtn');
            });
        }

        //Set the path in view
        navigatePaths(node);
    });

    //Handle the display of active node except leaf
    $tree.on('after_open.jstree', function(e, data){
        removeWholeRowClasses();
        if(sessionStorage.forceRowClass){
            //Get correct selected path after creating secret
            var node = $tree.jstree(true).get_node(sessionStorage.forceRowClass, true)[0];
            addWholeRowClasses(node);
            sessionStorage.removeItem('forceRowClass');
        } else {
            addWholeRowClasses(data.node);
        }

        if(data.node.type !== 'rootNode'){
            data.instance.set_icon(data.node.id,'fa fa-folder-open');
        }
    });

    //Handle the display of active node except leaf
    $tree.on('after_close.jstree', function(e, data){
        removeWholeRowClasses();
        addWholeRowClasses(data.node);
    });

    
    //Expand and collapse path with left click on node
    $tree.on('select_node.jstree', function(e, data){
        data.instance.toggle_node(data.node);

        removeWholeRowClasses();
        $tree.find('div.jstree-wholerow-leaf').removeClass('jstree-wholerow-leaf');
        $tree.find('a.jstree-clicked-leaf').removeClass('jstree-clicked-leaf');

        
        if(!data.instance.is_leaf(data.node) && data.node.type !== 'rootNode'){
           if(data.instance.is_loading(data.node) || data.instance.is_open(data.node)){
               data.instance.set_icon(data.node.id, 'fa fa-folder-open');
           } else {
               data.instance.set_icon(data.node.id, 'fa fa-folder');
           }
        } else if(data.instance.is_leaf(data.node)){
           addWholeRowClasses(data.node);
        }
       
    });

    $tree.on('load_node.jstree', function(e, data){
        //Force wholerow styling on root node after delete secret
        if(data.node.id === "#"){
            var node = $tree.jstree(true).get_node('root', true);
            $(node[0].children[0]).addClass("jstree-wholerow-leaf");
            $(node[0].children[2]).addClass("jstree-clicked-leaf");
        }
    });

    $(document).off('click', "#createSecretSubmitBtn");
    $(document).on('click', "#createSecretSubmitBtn", function(event){
        event.preventDefault();
        utilityModule.hideMessage();

        var selectedPath = $('[name="selectedPath"]').val();
        var path = $('#path').val();
        var secret = $('#secret').val();

        if(path.endsWith("/")) {
            path = path.substr(0, path.length -1);
        }

        if((path && secret) || (!path && secret)) {
            createPathAndSecret(selectedPath, path, secret);
        } else if(path && !secret) {
            createOnlyPath(selectedPath, path);
        }  else {
            utilityModule.showMessage('error', 'No path or secret supplied.');
            console.log("No path or secret supplied");
        }
    });

});

