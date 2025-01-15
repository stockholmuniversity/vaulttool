#!/usr/bin/env groovy

suSetProperties(["github": "true"])

def projectName = 'vaulttool'

//env.JAVA_HOME="/local/jdk"
env.JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"

suNodeWithNexusCredentials {

    stage("Cleanup workspace")
    {
        cleanWs()
    }

    stage("Prepare docker environment")
    {
        suDockerBuildAndPull(projectName)
    }

    docker.image(projectName).inside('-v /local/jenkins/conf:/local/jenkins/conf -v /local/jenkins/libexec:/local/jenkins/libexec -v /etc/ssl/certs/java/cacerts:/local/jdk/jre/lib/security/cacerts:ro') {

        suGitHubBuildStatus {

            stage("Prepare build")
            {
                suCheckoutCode ([
                    projectName : projectName,
                ])
            }
            
            stage("Build")
            {
//                sh "./gradlew bootWar --scan --stacktrace --debug -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
                sh "./gradlew bootWar -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
            }

            stage("Deploy to Nexus")
            {
                if (suGradlewBuildIsDeployable()) {
                    sh "./gradlew publish -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
                }
            }
        }
    }
}
