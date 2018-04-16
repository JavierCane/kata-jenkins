node {
    def mvnHome
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        git 'https://github.com/JavierCane/kata-jenkins.git'
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'Maven 3.5.0'
    }
    stage('Build') {
        sh "'${mvnHome}/bin/mvn' clean package -Drevision=${BUILD_NUMBER}"
    }
    stage('Results') {
        junit '**/target/surefire-reports/TEST-*.xml'
        archive 'target/*.jar'
    }
    stage('Upload result') {
        withAWS(region:'eu-west-3', credentials:'aws_key') {
            s3Upload(file:"target/kata-jenkins-${BUILD_NUMBER}.jar", bucket:'pipeline-kata', path:"alejandroNjavi/target/kata-jenkins-${BUILD_NUMBER}.jar")
        }
    }
}