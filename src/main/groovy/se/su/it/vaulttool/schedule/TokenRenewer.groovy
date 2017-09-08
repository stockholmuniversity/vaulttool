package se.su.it.vaulttool.schedule

import grails.core.GrailsApplication
import org.apache.log4j.Logger
import se.su.it.vaulttool.VaultRestService

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import static java.util.concurrent.TimeUnit.MINUTES


class TokenRenewer {
    GrailsApplication grailsApplication
    VaultRestService vaultRestService
    private static final Logger log = Logger.getLogger(TokenRenewer.class.getName())
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1)

    void runTokenRenewer() {
        int renewInterval = 50

        final Runnable renewChecker = new Runnable() {
            void run() {
                try {
                    vaultRestService.checkAndRenewToken(grailsApplication.config.vault.vaulttoken)
                } catch (Exception ex) {
                    log.error("Exception when doing renew check. Message was: ${ex.message}")
                } finally {

                }
            }
        }

        log.info("### TokenRenewer running with an interval of ${renewInterval} minutes ###")

        scheduler.scheduleWithFixedDelay(renewChecker, 0, renewInterval, MINUTES)
    }
}