package vaulttool

import se.su.it.vaulttool.VaultRestService

class BootStrap {
    def grailsApplication
    def vaultRestService

    def init = { servletContext ->
        initializeVault()
    }
    def destroy = {
    }

    def initializeVault() {
        vaultRestService.enableApproleAuth(grailsApplication.config.vault.vaulttoken)
        vaultRestService.putDefaultVaultToolPolicy(grailsApplication.config.vault.vaulttoken)
    }
}
