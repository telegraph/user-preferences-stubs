@Library('platform-jenkins-library') _

def sendMessage(message, channel, color = "good") {
    slackSend message: "$message", token: "${env.SLACK_PLATFORMS_RELEASES}", channel: channel, teamDomain: "telegraph", baseUrl: "https://hooks.slack.com/services/", color: color
}

def sendNotification( action, token, channel, shellAction ){
  try {
    slackSend message: "${action} Started - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", token: token, channel: channel, teamDomain: "telegraph", baseUrl: "https://hooks.slack.com/services/", color: "warning"
    sh shellAction  
  } catch (error) {
    slackSend message: "${action} Failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", token: token, channel: channel, teamDomain: "telegraph", baseUrl: "https://hooks.slack.com/services/", color: "danger"
    throw error
  }
  slackSend message: "${action} Finished - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)", token: token, channel: channel, teamDomain: "telegraph", baseUrl: "https://hooks.slack.com/services/", color: "good"
}

ansiColor('xterm') {
    lock("${env.PROJECT_NAME}"){
        node ("master"){

            def sbtFolder          = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin"
            def projectName        = "${env.PROJECT_NAME}"
            def github_token       = "${env.GITHUB_TOKEN}"
            def jenkins_github_id  = "${env.JENKINS_GITHUB_CREDENTIALS_ID}"
            def docker_account     = "${env.AWS_ECR_DOCKER_ACCOUNT}"
            def docker_registry    = "${env.AWS_ECR_DOCKER_REGISTRY}"
            def pipeline_version   = "1.0.0-b${env.BUILD_NUMBER}"
            def github_commit      = ""
            def slackToken         = "${env.SLACK_PLATFORMS_RELEASES}"


            checkoutCodeStage {
                project_name = projectName
                github_id    = jenkins_github_id
            }

            environment {
                DOCKER_IMAGE = "${projectName}:${pipeline_version}"
            }

            stage("Build"){
                sh """
                    echo "Build docker container"
                    ${sbtFolder}/sbt clean assembly
                """
                docker.build("${projectName}:${pipeline_version}", "--build-arg APP_NAME=${projectName} --build-arg APP_VERSION=${pipeline_version} .")
            }

            stage("Publish"){
                sh """
                    echo "Publish docker image"
                    ${sbtFolder}/sbt publish
                """
                docker.withRegistry("${docker_account}", "${docker_registry}") {
                    docker.image("${projectName}:${pipeline_version}").push()
                }
            }

            stage("Preprod Deploy"){
                def projEnv = "preprod"

                nodejs('Node7.7.2') {
                  sh """
                    nStack setup --app-name ${projectName} --app-version ${pipeline_version} --app-env ${projEnv}
                  """
                }
            }
        }
    }
    lock("${env.PROJECT_NAME}-prod") {
     node ("master"){

            def sbtFolder          = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin"
            def projectName        = "${env.PROJECT_NAME}"
            def github_token       = "${env.GITHUB_TOKEN}"
            def jenkins_github_id  = "${env.JENKINS_GITHUB_CREDENTIALS_ID}"
            def docker_account     = "${env.AWS_ECR_DOCKER_ACCOUNT}"
            def docker_registry    = "${env.AWS_ECR_DOCKER_REGISTRY}"
            def pipeline_version   = "1.0.0-b${env.BUILD_NUMBER}"
            def github_commit      = ""
            def slackToken         = "${env.SLACK_PLATFORMS_RELEASES}"


            validateProdDeployStage {
                project_name    = projectName
                github_token    = github_token
                slack_channel   = "#platforms-releases"
                commit_filter   = "PLAT"
                slack_token     = slackToken
            }
            
            
            stage("Prod Deploy"){
                def projEnv = "prod"

                nodejs('Node7.7.2') {
                  sh """
                    nStack setup --app-name ${projectName} --app-version ${pipeline_version} --app-env ${projEnv}
                  """
                }
            }

              generateReleaseNotesStage {
                project_name    = projectName
                github_token    = github_token
                release_version = pipeline_version
            }
        }
    }
}
