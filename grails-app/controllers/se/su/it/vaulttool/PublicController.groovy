package se.su.it.vaulttool

class PublicController {

    def index() {

    }

    def secondauth() {
        String enteredCode = params?.verificationcode?:null
        if(enteredCode && enteredCode == session.secondauthkey) {
            session.secondauth = true
            redirect(controller: "dashboard", action: "index")
        } else if(enteredCode){
            flash.error = "Code Mismatch, please try again."
        }
        [dummy: "dummy"]
    }

    def logout() {
        session.uid = null
        session.displayname     = null
        session.group           = null
        session.eppn            = null
        session.email           = null
        session.secondauth      = null
        session.secondauthkey   = null
        session.token           = null
        session.sudo            = null
        session.groups          = null
    }

    def disableSudo() {
        session.sudo = null
        session.token = null
        session.group = null
        session.groups = null

        redirect(controller: "dashboard", action: "index")
    }

    def setGroup() {
        String group = params?.group?:null
        if(!group) {
            String errorMsg = "Failed when trying to change group. Error was: No group supplied"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "index")
        }
        if(!session.groups?.contains(group)) {
            String errorMsg = "Failed when trying to change group. Error was: Group does not exist"
            log.error(errorMsg)
            flash.error = errorMsg
            return redirect(action: "index")
        }
        session.group = group
        session.token = null
        return redirect(controller: "dashboard", action: "index")
    }
}
