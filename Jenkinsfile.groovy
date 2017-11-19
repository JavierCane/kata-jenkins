#!groovy

node {

    String SLACK_URL = "https://your-slack-account.slack.com/services/hooks/jenkins-ci/"
    String SLACK_CHANNEL = "#kata_jenkins"

    try {
        stage('Preparation') {
            deleteDir()
            checkout scm
            env.PATH = "${env.PATH}:${tool 'Maven 3.5.0'}/bin"
        }

        stage('Build') {
            sh "mvn clean package"
        }

        stage('Results') {
            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }

        stage('Upload to S3') {
            withAWS(region: 'eu-west-1', credentials: 'AWS_CREDENTIALS') {
                s3Upload(bucket: "kata-jenkins",
                        path: "my-name/kata-jenkins.jar",
                        includePathPattern: 'target/*.jar'
                )
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