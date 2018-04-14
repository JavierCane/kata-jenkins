#!groovy

node {

    String APP_NAME = "sample-app"
    String VERSION = "v-${env.BUILD_NUMBER}"
    int PORT = 8086
    String SLACK_URL = "https://your-slack-account.slack.com/services/hooks/jenkins-ci/"
    String SLACK_CHANNEL = "#general"

    try {
        stage('Checkout') {
            deleteDir()
            checkout scm
            sh "chmod +x infrastructure/*"
        }

        stage('Unit tests') {
            docker.image("maven:3.5.3").inside("-v $HOME/.m2:/root/.m2") {
                sh "./infrastructure/unit-tests.sh"
            }

            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }

        stage('Build') {
            docker.image("maven:3.5.3").inside("-v $HOME/.m2:/root/.m2") {
                sh "./infrastructure/build-mvn.sh"
            }

            sh """
                export APP_NAME=${APP_NAME}
                export VERSION=${VERSION}
                ./infrastructure/build-image.sh
            """
        }

        stage('Upload image') {
            withCredentials([string(credentialsId: 'REPOSITORY_KEY', variable: 'REPOSITORY_KEY')]) {
                docker.image("google/cloud-sdk:196.0.0").inside {
                    sh """
                    export REPOSITORY_KEY=${REPOSITORY_KEY}
                    export APP_NAME=${APP_NAME}
                    export VERSION=${VERSION}
                    ./infrastructure/push-image.sh
                """
                }
            }
        }

        stage('Deploy') {
            docker.image("google/cloud-sdk:196.0.0").inside {
                sh """
                    export APP_NAME=${APP_NAME}
                    export VERSION=${VERSION}
                    export PORT=${PORT}
                    ./infrastructure/deploy.sh
                """

                sh """
                   export APP_NAME=${APP_NAME}
                   export PORT=${PORT}
                    ./infrastructure/smoke-test.sh
                """
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