package se.su.it.vaulttool

import grails.core.GrailsApplication

class ApplicationSettingsInterceptor {
    int order = 20
    GrailsApplication grailsApplication

    ApplicationSettingsInterceptor() {
        matchAll()
    }

    boolean before() {
        String logoUrl = grailsApplication.config.getProperty("vaulttool.logoUrl", String.class)
        String applicationName = grailsApplication.config.getProperty("vaulttool.applicationName", String.class)

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
