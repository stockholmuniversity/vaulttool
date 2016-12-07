package se.su.it.vaulttool

import org.apache.commons.mail.SimpleEmail

class UtilityService {
    def grailsApplication
    def vaultRestService

    def sendEmail(String subject, String msg, String to) {
        SimpleEmail email = new SimpleEmail()
        email.setHostName("smtp.su.se")
        email.addTo(to)
        email.setFrom("noreply@su.se")
        email.setSubject(subject)
        email.setMsg(msg)
        //email.setAuthentication(username,password)
        //email.setSmtpPort(port)

        //email.send()
    }
}
