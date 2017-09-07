package se.su.it.vaulttool

import grails.core.GrailsApplication

class ApplicationSettingsInterceptor {
    int order = 20
    GrailsApplication grailsApplication

    ApplicationSettingsInterceptor() {
        matchAll()
    }

    boolean before() {
        String logoUrl = grailsApplication.config.vaulttool.logoUrl
        String applicationName = grailsApplication.config.vaulttool.applicationName

        if(logoUrl && !logoUrl.empty) {
            session.logoUrl = logoUrl
        }
        if(applicationName && !applicationName.empty) {
            session.applicationName = applicationName
        }
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
