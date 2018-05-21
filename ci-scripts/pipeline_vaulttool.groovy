#!/usr/bin/env groovy

def projectName = 'vaulttool'

env.JAVA_HOME="/local/jdk"

suNodeWithNexusCredentials {

    stage("Cleanup workspace")
    {
        cleanWs()
    }

    stage("Prepare docker environment")
    {
        suDockerBuildAndPull(projectName)
    }

    docker.image(projectName).inside('-v /local/jenkins/conf:/local/jenkins/conf -v /local/jenkins/libexec:/local/jenkins/libexec') {

        stage("Prepare build")
        {
            suCheckoutCode ([
                projectName : projectName,
            ])
        }

        stage("Build")
        {
            sh "./gradlew bootRepackage -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
        }

        stage("Deploy to Nexus")
        {
            sh "./gradlew publish -PnexusUsername=${nexusUsername} -PnexusPassword=${nexusPassword}"
        }
    }
}
