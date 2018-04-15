pipeline {

    agent any

    tools {
        maven 'Maven 3.5.0'
    }

    environment {
        SLACK_URL = "https://pipeline-kata.slack.com/services/hooks/jenkins-ci/"
    }

    stages {
        stage('Build and launch tests') {
            steps {
                sh "mvn clean package -Drevision=v-${env.BUILD_NUMBER}"
            }
        }

        stage('Publish results') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                archive 'target/*.jar'
            }
        }

        stage('Upload to S3 repository') {
            steps {
                withAWS(region: 'eu-west-3', credentials: 'AWS_CREDENTIALS') {
                    s3Upload(bucket: "pipeline-kata",
                            path: "my-name/kata-jenkins.jar",
                            includePathPattern: 'target/*.jar'
                    )
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }

        success {
            slackSend baseUrl: SLACK_URL,
                tokenCredentialId: "SLACK_TOKEN",
                channel: "#general",
                color: '#00FF00',
                message: """
                    SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'
                """
        }

        failure {
            slackSend baseUrl: SLACK_URL,
                tokenCredentialId: "SLACK_TOKEN",
                channel: "#general",
                color: '#FF0000',
                message: """
                    FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'
                """
        }
    }
}