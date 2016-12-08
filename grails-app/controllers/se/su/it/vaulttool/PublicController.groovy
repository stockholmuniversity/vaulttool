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
}
