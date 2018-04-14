#!groovy

node {

    String SLACK_URL = "https://your-slack-account.slack.com/services/hooks/jenkins-ci/"
    String SLACK_CHANNEL = "#general"

    try {
        stage('Checkout') {
            deleteDir()
            checkout scm
            // env.PATH = "${env.PATH}:${tool 'Maven 3.5.0'}/bin"
        }

        stage('Unit tests') {
            docker.image("maven:3.5.3").inside {
                sh "./infrastructure/unit-tests.sh"
            }

            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }

        stage('Build') {
            docker.image("maven:3.5.3").inside {
                sh "./infrastructure/build-mvn.sh"
            }

            sh "./infrastructure/build-image.sh"
        }

        stage('Upload image') {
            docker.image("google/cloud-sdk:196.0.0").inside {
                sh "./infrastructure/push-image.sh"
            }
        }

        stage('Deploy') {
            docker.image("google/cloud-sdk:196.0.0").inside {
                sh "./infrastructure/deploy.sh"
                sh "./infrastructure/smoke-test.sh"
            }
        }

        stage('Notification') {
            withCredentials([string(credentialsId: 'SLACK_TOKEN', variable: 'SLACK_TOKEN')]) {
                slackSend baseUrl: SLACK_URL,
                        token: SLACK_TOKEN,
                        channel: SLACK_CHANNEL,
                        color: '#00FF00',
                        message: """
                        SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'
                    """
            }
        }
    } catch (e) {
        withCredentials([string(credentialsId: 'SLACK_TOKEN', variable: 'SLACK_TOKEN')]) {
            slackSend baseUrl: SLACK_URL,
                    token: SLACK_TOKEN,
                    channel: SLACK_CHANNEL,
                    color: '#FF0000',
                    message: """
                    FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'\n
                    Error: $e
                """
        }

        currentBuild.result = "FAILED"
        throw e
    }
}