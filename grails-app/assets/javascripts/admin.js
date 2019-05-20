var adminModule = (function ($) {
    "use strict";

    var initModule, $body;

    function initVariables($container){
        $body = $('body');


        $body.on('click', '#addPolicyLink', function(){
            if($("#policiesContainer").hasClass('d-none')){
                utilityModule.hideMessage();
                $("#policiesContainer").removeClass('d-none').addClass('d-block');
                $("#policyLinkLabel").html("Hide policies");
            } else {
                $("#policiesContainer").removeClass('d-block').addClass('d-none');
                $("#policyLinkLabel").html("Add policies");
            }
            
        });


    }

    function initEventHandlers() {


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


    function callServer(data, url){
         return $.ajax({
             type: 'POST',
             url: '/admin/' + url,
             data: data
         });
    }

    initModule = function($container){
       initVariables($container);
       initEventHandlers();
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

