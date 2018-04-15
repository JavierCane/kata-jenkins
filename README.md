# Jenkins Kata
- If you enjoyed this kata you can continue with the [Second part](https://github.com/Marcut-Laurentiu/kata-jenkins-part-2.git).

## Requirements
##### Install [Docker](https://docs.docker.com/engine/installation/#supported-platforms)

##### Run Jenkins
```
$ # Run inside docker container
$ docker run --rm -d \
	-p 8080:8080 \
	-v jenkins_home:/var/jenkins_home \
	--name my-jenkins
	jenkins/jenkins:lts
	
$ # Get the admin password
$ docker exec -it my-jenkins cat /var/jenkins_home/secrets/initialAdminPassword

$ # Stop Jenkins
$ docker kill my-jenkins
```

##### Install Jenkins Plugins
- Recommended plugins.
- Blue Ocean.
- Pipeline: AWS Steps.
- Slack Notification Plugin.

##### Slack account for notifications
- Notifications channel **#jenkins**.

##### AWS account for artifact uploads
- S3 bucket **pipeline_kata/my-team-name**.

##### Fork this repository

## Kata steps:
1. Create a pipeline job. You can use the Scripted or Declarative Pipeline.
2. Download the source code from the forked repository (master branch).
3. Build the project and launch the unit tests. You can use the following command:
    ```
    $ mvn clean package
    ```
   Hint: you need to install **Maven 3.5.0** or newer.
4. Publish on Jenkins your test results using the **junit** step. <br />
   Get the build result (JAR) from the **/target** directory and save it in Jenkins for later retrieval. 
   You can use the **archive** step. 
5. Upload the JAR to AWS S3 bucket (create a directory with you team name).
   Take care how you store your credentials. <br />
   Hint: you need AWS credentials for this step.
6. Send Slack notification in case of successful or failed execution.
   You can send any information like job name, build number, message color etc...<br />
   Hint: you need the Slack URL and a token for this step.
7. Pipeline as code. Move the pipeline code in a **Jenkinsfile** and push it to the same repository.
   Pool the repository to detect any commit changes and start the build without manual steps.
   
## Documentation
- [Jenkins Documentation](https://jenkins.io/doc/)

- [Jenkins Pipeline](https://jenkins.io/doc/book/pipeline/)

- [Jenkinsfile](https://jenkins.io/doc/book/pipeline/jenkinsfile/)

- [Jenkins Plugins](https://jenkins.io/doc/book/managing/plugins/)

- [Jenkins Blue Ocean](https://jenkins.io/doc/book/blueocean/)

- [Pipeline: AWS Steps](https://github.com/jenkinsci/pipeline-aws-plugin)

- [Slack Notification Plugin](https://github.com/jenkinsci/slack-plugin)
