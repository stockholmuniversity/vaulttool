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
   
   function addWholeRowClasses(){
       var nodeId = sessionStorage.forceRowClass;
       $('#' + nodeId + '> div').addClass('jstree-wholerow-clicked');
       $('#' + nodeId + '> a').addClass('jstree-clicked');
   }


    $('#navTree').on('loaded.jstree', function(event){
       $('#root > div').addClass('jstree-wholerow-clicked');
       $('#root > a').addClass('jstree-clicked');
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
            window.location.href = '/';
        }

        //Clear user info menu since then user has left admin and navigated to a secret
        if($('#navTree').jstree(true).is_leaf(node)){
            var buttons = $('.activeAdminBtn');
            $.each(buttons, function(index,val){
                $(val).removeClass('activeAdminBtn');
            });
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
       
    });

    

});

