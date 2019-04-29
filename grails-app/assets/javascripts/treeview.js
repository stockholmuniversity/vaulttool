$(document).ready(function(){

    $('#navTree').jstree({
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
            if(sessionStorage.enablePaste){
                items.item3._disabled = false;
            } else {
                items.item3._disabled = true;
            }
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
        
        if(node.type === 'pathNode' || !$('#navTree').jstree(true).is_leaf(node)){
            var selectedPath = node.id.replace(/_/g,"/") + "/";

            $.ajax({
                type: 'POST',
                url: "/dashboard/index",
                data: {selectedPath: selectedPath},
                success: function(data){
                    $("#dashboard").html(data);
                },
                error: function(){

                }
            })
        } else {
            var key = node.id.substring(5).replace(/_/g, "/");

            $.ajax({
                type: "POST",
                url: "/dashboard/secret",
                data: { key : key},
                success: function (data) {
                    $('#dashboard').html(data);
                },
                error: function(data) {
                    utilityModule.showMessage('error', data.responseText);
                    console.log(data.responseText);
                }
            });
        }

    }

    //Triggered after the root node is loaded for the first time
    $('#navTree').on('loaded.jstree', function(e){
        var node = e.target.children[0].childNodes[0];
        addWholeRowClasses(node);
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
        } else {
            window.location.href = '/';
        }

        //Clear user info menu since then user has left admin and navigated to a secret
        if($('#navTree').jstree(true).is_leaf(node)){
            var buttons = $('.activeAdminBtn');
            $.each(buttons, function(index,val){
                $(val).removeClass('activeAdminBtn');
            });
        }

        //Set the path in view
        navigatePaths(node);
    });

    //Handle the display of active node except leaf
    $('#navTree').on('after_open.jstree', function(e, data){
        removeWholeRowClasses();
        if(sessionStorage.forceRowClass){
            //Get correct selected path after creating secret
            var node = $("#navTree").jstree(true).get_node(sessionStorage.forceRowClass, true)[0];
            addWholeRowClasses(node);
            sessionStorage.removeItem('forceRowClass');
        } else {
            addWholeRowClasses(data.node);
        }

    });

    //Handle the display of active node except leaf
    $('#navTree').on('after_close.jstree', function(e, data){
        removeWholeRowClasses();
        addWholeRowClasses(data.node);
    });

    
    //Expand and collapse path with left click on node
    $('#navTree').on('select_node.jstree', function(e, data){
        data.instance.toggle_node(data.node);

        console.log('select_node');
        removeWholeRowClasses();
        var $navTree = $("#navTree");
        $navTree.find('div.jstree-wholerow-leaf').removeClass('jstree-wholerow-leaf');
        $navTree.find('a.jstree-clicked-leaf').removeClass('jstree-clicked-leaf');

        
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

    

});

