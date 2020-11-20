import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.net.SyslogAppender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
if(Environment.isDevelopmentMode() || Environment.currentEnvironment == Environment.TEST) {
    println("### Setting up logback for development/test mode ###")
    appender('STDOUT', ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d %level %logger - %msg%n"
        }
    }
    root(INFO, ['STDOUT'])
    logger("groovyx.net.http.ParserRegistry", ERROR, ['STDOUT'], false)
    logger("se.su.it.vaulttool", DEBUG, ['STDOUT'], false)
    println("### Finished setting up logback for development/test mode ###")
} else {
    Properties systemProps = System.properties
    String local = "/local"
    if(systemProps.get("vaulttool.localfilepath") && !systemProps.get("vaulttool.localfilepath").isEmpty()) {
        local = systemProps.get("vaulttool.localfilepath")
    }
    println("### Setting up logback for production mode ###")
    appender('TIME_BASED_FILE', RollingFileAppender) {
        file = "${local}/vaulttool/logs/vaulttool.log"
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "${local}/vaulttool/logs/vaulttool.log.%d{yyyy-MM-dd}"
            maxHistory = 365
        }
        encoder(PatternLayoutEncoder) {
            pattern = "%d %level %logger - %msg%n"
        }
    }
    appender('SYSLOG', SyslogAppender) {
        syslogHost = "127.0.0.1"
        facility = "USER"
        throwableExcluded = true
        suffixPattern = "vaulttool: %level [%thread] %logger - %msg%n%xException"
    }

    root(INFO, ['TIME_BASED_FILE', 'SYSLOG'])
    logger("org.grails.web.errors", DEBUG, ['TIME_BASED_FILE', 'SYSLOG'], false)
    logger("grails", INFO, ['TIME_BASED_FILE', 'SYSLOG'], false)
    logger("se.su.it.vaulttool", INFO, ['TIME_BASED_FILE', 'SYSLOG'], false)
    logger("groovyx.net.http.ParserRegistry", ERROR, ['TIME_BASED_FILE', 'SYSLOG'], false)
    println("### Finished setting up logback for production mode ###")
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%d %level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}