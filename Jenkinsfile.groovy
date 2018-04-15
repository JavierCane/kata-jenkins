#!groovy

node {

    def SLACK_URL = "https://your-slack-account.slack.com/services/hooks/jenkins-ci/"

    try {
        stage('Initialisation') {
            deleteDir()
            checkout scm
            env.PATH = "${env.PATH}:${tool 'Maven 3.5.0'}/bin"
        }

        stage('Build and launch tests') {
            def version = "v-${env.BUILD_NUMBER}"
            sh "mvn clean package -Drevision=$version"
        }

        stage('Publish results') {
            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }

        stage('Upload to S3 repository') {
            withAWS(region: 'eu-west-3', credentials: 'AWS_CREDENTIALS') {
                s3Upload(bucket: "pipeline-kata",
                        path: "my-name/kata-jenkins.jar",
                        includePathPattern: 'target/*.jar'
                )
            }
        }

        stage('Notification') {
            slackSend baseUrl: SLACK_URL,
                    tokenCredentialId: "SLACK_TOKEN",
                    channel: "#general",
                    color: '#00FF00',
                    message: """
                        SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'
                    """
        }
    } catch (e) {
        slackSend baseUrl: SLACK_URL,
                tokenCredentialId: "SLACK_TOKEN",
                channel: "#general",
                color: '#FF0000',
                message: """
                    FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'\n
                    Error: $e
                """

        currentBuild.result = "FAILED"
        throw e
    }
}