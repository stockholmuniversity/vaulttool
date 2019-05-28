var adminModule = (function ($) {
    "use strict";

    var initModule, $body;


    //TODO: All the code needs some serious refactoring and "DRYING"
    function initVariables($container){
        $body = $('body');

        $body.on('click', '#addPolicyLink', function(){
            if($("#policiesContainer").hasClass('d-none')){
                utilityModule.hideMessage();
                
                $("#policiesContainer").removeClass('d-none').addClass('d-block');
                $("#policyLinkLabel").html("Hide policies");
                $("#addPolicyLink").find("span").removeClass('fa-plus');
                $("#addPolicyLink").find("span").addClass('fa-times');
            } else {
                $("#policiesContainer").removeClass('d-block').addClass('d-none');
                $("#policyLinkLabel").html("Add policies");
                $("#addPolicyLink").find("span").removeClass('fa-times');
                $("#addPolicyLink").find("span").addClass('fa-plus');
            }
            
        });


    }

    function addPolicyToSelectedPolicies(){
        $body.on('click', '[id^="policy_"]', function(){
            //TODO: Styling should be made into css classes...

            var policyName      = $(this).data('policy');
            var $policies       = $("[name='policies']");
            var currentPolicies = $policies.val();
            var policyToRemove  = "#policy_"+policyName;

            if(currentPolicies){
                currentPolicies += "," + policyName;
            } else {
                currentPolicies += policyName
            }
            $policies.val(currentPolicies);

            var $policy = $("<span>" + policyName + "</span>");
            var $icon   = $("<span></span>").addClass('fa fa-times pointer');
            $icon.css({color: 'rgba(0, 47, 95, 1.0)'});
            $policy.append(" ").append($icon);
            $policy.attr('id', 'selectedPolicy_' + policyName);
            $policy.data('selectedpolicy', policyName);
            $policy.css({background: 'rgba(172, 222, 230, 0.6)', padding: '5px', color: 'rgba(0, 47 ,95 ,1.0)'});

            $("#selectedPolicies").append($policy).append(" ");

            if($("#selectedPolicies").children('span').length === 1) {
                $("#selectedPolicies").addClass('bottom-margin-medium');
            }
            $(policyToRemove).remove();
        });

        
        $body.on('click', '[id^="editableApproleSelectablePolicy_"]', function(){
            var policyName      = $(this).data('policy');
            var appRole         = $(this).data('approle');
            var $policies       = $("[name='editableApprolePolicies_" + appRole + "']" );
            var currentPolicies = $policies.val();
            var policyToRemove  = $("#policiesContainer_" + appRole).find("#editableApproleSelectablePolicy_" + policyName);

            if(currentPolicies){
                currentPolicies += "," + policyName;
            } else {
                currentPolicies += policyName
            }
            $policies.val(currentPolicies);
            console.log($policies.val());
            
            var $policy = $("<span>" + policyName + "</span>");
            var $icon   = $("<span></span>").addClass('fa fa-times pointer');
            $icon.css({color: 'rgba(0, 47, 95, 1.0)'});
            $policy.append(" ").append($icon);
            $policy.attr('id', 'editableApproleSelectedPolicy_' + policyName + "_" + appRole);
            $policy.attr('data-status', 'unsaved');
            $policy.data('edappselpolicy', policyName);
            $policy.data('edappselapprole', appRole);
            $policy.css({background: 'rgba(172, 222, 230, 0.6)', padding: '5px', color: 'rgba(0, 47 ,95 ,1.0)', display: 'inline-block'});
            $policy.css('bottom-margin-xsmall');

            $("#selectedPolicies_" + appRole).append($policy).append(" ");
            $(policyToRemove).remove();

        });

    }

    function removePoliciesFromSelectedPolicies(){
        $body.on('click', '[id^="selectedPolicy_"]', function(){

            var selectedPolicy = $(this).data('selectedpolicy');
            var policyToRemove = "#selectedPolicy_" + selectedPolicy;
            var $policies      = $("[name='policies']");
            var policiesValues = $policies.val();

            if(/,/g.test(policiesValues)){
                if(policiesValues.substr(-(selectedPolicy.length)) === selectedPolicy){
                    policiesValues = policiesValues.replace(selectedPolicy, "");
                } else {
                    policiesValues = policiesValues.replace(selectedPolicy + ',', "");
                }
            } else {
                policiesValues = policiesValues.replace(selectedPolicy, "");
            }
            $policies.val(policiesValues);
            $(policyToRemove).remove();

            var $policy = $("<div></div>");
            var $policySpan = $("<span></span>");
            var $policyStrong = $("<strong></strong>");
            $policy.attr('id', 'policy_'+ selectedPolicy);
            $policy.data('policy', selectedPolicy);
            $policy.addClass('col-3 pointer');
            $policySpan.css({color: 'rgba(0, 47, 95, 0.8)'});
            $policyStrong.append(selectedPolicy);
            $policySpan.append($policyStrong);
            $policy.append($policySpan);
            $("#selectablePolicies").append($policy);

            if($("#selectedPolicies").children('span').length === 0) {
                $("#selectedPolicies").removeClass('bottom-margin-medium');
            }
        });

        $body.on('click','[id^="editableApproleSelectedPolicy_"]', function(){
            var selectedPolicy  = $(this).data('edappselpolicy');
            var appRole         = $(this).data('edappselapprole');
            var policyToRemove  = "#editableApproleSelectedPolicy_" + selectedPolicy + "_" + appRole;
            var approleSel      = "editableApprolePolicies_" + appRole;
            var $policies       = $('[name="' + approleSel +'"]');
            var policiesValues  = $policies.val();
            
            if(/,/g.test(policiesValues)){
                if(policiesValues.substr(-(selectedPolicy.length)) === selectedPolicy){
                    policiesValues = policiesValues.replace(',' + selectedPolicy, "");
                } else {
                    policiesValues = policiesValues.replace(selectedPolicy + ',', "");
                }
            } else {
                policiesValues = policiesValues.replace(selectedPolicy, "");
            }
            $policies.val(policiesValues);
            $(policyToRemove).remove();

            var $policy         = $("<div></div>");
            var $policySpan     = $("<span></span>");
            var $policyStrong   = $("<strong></strong>");

            $policy.attr('id', 'editableApproleSelectablePolicy_'+ selectedPolicy);
            $policy.data('policy', selectedPolicy);
            $policy.data('approle', appRole);
            $policy.addClass('col-3 pointer');
            $policySpan.css({color: 'rgba(0, 47, 95, 0.8)'});
            $policyStrong.append(selectedPolicy);
            $policySpan.append($policyStrong);
            $policy.append($policySpan);

            $("#selectablePolicies_" + appRole).append($policy);
        })
    }

    function createPolicy() {
        $body.on('click', '#createUpdateApproleButton', function(event){
            event.preventDefault();
            utilityModule.hideMessage();

            var appRoleName = $('#name').val();
            var form        = $("#createApproleForm").serialize();
            var $policies   = $("[name='policies']");

            callServer(form, 'createApprole')
                    .done(function(data){
                        $('#dashboard').html(data);
                        utilityModule.showMessage('info','Successfully created approle ' + appRoleName);
                        $policies.val("");
                        
                    })
                    .fail(function(data){
                        utilityModule.showMessage('error', data.responseText);
                        console.log(data.responseText);
                        $policies.val("");
                    })

        });
    }

    function updateApprole(){
        $body.on('click', '#updateApproleButton', function(event){
            event.preventDefault();
            utilityModule.hideMessage();

            var appRoleName     = $(this).data('approle');
            var $policies       =  $("#editableApprolePolicies_" + appRoleName);
            var policiesValues  = $policies.val();

            callServer({name: appRoleName, policies: policiesValues }, 'createApprole')
                    .done(function(data){
                        $('#dashboard').html(data);
                        utilityModule.showMessage('info','Successfully created approle ' + appRoleName);
                        $policies.val("");

                    })
                    .fail(function(data){
                        utilityModule.showMessage('error', data.responseText);
                        console.log(data.responseText);
                        $policies.val("");
                    })

        })
    }

    function deleteApprole(){
        $(document).off('click', '.deleteApproleLink');
        $(document).on('click', '.deleteApproleLink', function(event){
            event.preventDefault();
            utilityModule.hideMessage();

            var approle = $(this).data('approle');
            $.ajax({
                type: "POST",
                url: "/admin/deleteApprole",
                data : {approle:approle},
                success: function (data) {
                    $('#dashboard').html(data);
                    utilityModule.showMessage('info','Successfully deleted approle ' + approle);
                },
                error: function(data) {
                    utilityModule.showMessage('error', data.responseText);
                    console.log(data.responseText);
                }
            });

        });
    }

    function editApprole(){
        $body.on('click', '.editApproleLink', function(event){
            event.preventDefault();
            utilityModule.hideMessage();
            var approle = $(this).data('approle');
            var $approleView = $("#editablePolicyListItemView_" + approle);
            var $approleEditLink = $("#editableApproleListItemLink_" + approle);
            var $approlePolicyContainer = $("#approlePolicyContainer_" + approle);

            if($approlePolicyContainer.hasClass('d-none')){
                $approlePolicyContainer.removeClass('d-none').addClass('d-block');
                $approleEditLink.removeClass('d-block').addClass('d-none');
                $approleView.removeClass('d-block').addClass('d-none');
            } 
        });
    }

    function editApproleItemPolicies(){
        $body.on('click', '.editableApprolePolicyLink', function(){
            var approle = $(this).data('approle');
            var $policiesContainer = $("#policiesContainer_" + approle);
            var $approlePoliciesLinkLabel = $("#approlePoliciesLinkLabel_" + approle);

            if($policiesContainer.hasClass('d-none')){
                $policiesContainer.removeClass('d-none').addClass('d-block');
                $approlePoliciesLinkLabel.html("Hide policies");
                $(this).find("span").removeClass('fa-plus').addClass('fa-times');
            } else {
                $policiesContainer.removeClass('d-block').addClass('d-none');
                $approlePoliciesLinkLabel.html("Add policies");
                $(this).find("span").removeClass('fa-times').addClass('fa-plus');
            }
        });
    }

    function cancelEditApprole(){
        $body.on('click', '.cancelEditApproleLink', function(event){
            event.preventDefault();
            utilityModule.hideMessage();
            var approle                     = $(this).data('approle');
            var $approleView                = $("#editablePolicyListItemView_" + approle);
            var $approleEditLink            = $("#editableApproleListItemLink_" + approle);
            var $approlePolicyContainer     = $("#approlePolicyContainer_" + approle);
            var $policiesContainer          = $("#policiesContainer_" + approle);
            var $approlePoliciesLinkLabel   = $("#approlePoliciesLinkLabel_" + approle);
            
            var $selectedPoliciesContainer = $("#selectedPolicies_" + approle);
            var policiesToRemove =  $selectedPoliciesContainer.find("span[data-status='unsaved']");
            var $selectedPolicies = $("[name='editableApprolePolicies_" + approle +"']");
            var selectedPoliciesValues = $selectedPolicies.val();

            $.each(policiesToRemove, function(i, val){
                var policy = $(val).data('edappselpolicy');

                if(/,/g.test(selectedPoliciesValues)){
                    if(selectedPoliciesValues.substr(-(policy.length))){
                        selectedPoliciesValues = selectedPoliciesValues.replace(',' + policy, "");
                    } else {
                        selectedPoliciesValues = selectedPoliciesValues.replace(policy + ',', "");
                    }
                } else {
                    selectedPoliciesValues = selectedPoliciesValues.replace(policy, "");
                }
                $selectedPolicies.val(selectedPoliciesValues);
                $(val).remove();

                var $policy         = $("<div></div>");
                var $policySpan     = $("<span></span>");
                var $policyStrong   = $("<strong></strong>");

                $policy.attr('id', 'editableApproleSelectablePolicy_'+ policy);
                $policy.data('policy', policy);
                $policy.data('approle', approle);
                $policy.addClass('col-3 pointer');
                $policySpan.css({color: 'rgba(0, 47, 95, 0.8)'});
                $policyStrong.append(policy);
                $policySpan.append($policyStrong);
                $policy.append($policySpan);

                $("#selectablePolicies_" + approle).append($policy);

            });

            if($approlePolicyContainer.hasClass('d-block')){
                $approlePolicyContainer.removeClass('d-block').addClass('d-none');
                $approleEditLink.removeClass('d-none').addClass('d-block');
                $approleView.removeClass('d-none').addClass('d-block');
                $policiesContainer.removeClass('d-block').addClass('d-none');
                $approlePoliciesLinkLabel.html("Add policies");
                $approlePoliciesLinkLabel.closest("span").find('.fa-times').removeClass('fa-times').addClass('fa-plus');

            }
        });
    }

    function callServer(data, url){
         return $.ajax({
             type: 'POST',
             url: '/admin/' + url,
             data: data
         });
    }

    initModule = function($container){
        initVariables($container);
        addPolicyToSelectedPolicies();
        removePoliciesFromSelectedPolicies();
        createPolicy();
        deleteApprole();
        editApprole();
        cancelEditApprole();
        editApproleItemPolicies();
        updateApprole();
    };

    return {
        initModule: initModule
    }
     
}(jQuery));

jQuery(document).ready(
        function () {
            adminModule.initModule(jQuery("body"));
        }
);

