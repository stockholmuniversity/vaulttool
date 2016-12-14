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

        redirect(controller: "dashboard", action: "index")
    }
}
