def createDeploymentJob(jobName, repourl) {
  pipelineJob(jobName) {
    definition {
      cps {
        script('''
          jsl = library(
            identifier: "jenkins-shared-library@develop",
            retriever: modernSCM(
              [
                $class: 'GitSCMSource',
                remote: 'https://tech-devops@bitbucket.org/sil-dev/dsl-jobs.git',
                credentialsId: 'bitbucket_cred'
              ]
            )
          )
          pipeline_initialize()    
        '''.stripIndent())               
        sandbox()     
      }
    }
  }
}

def createrollbackJob(jobName, repourl) {
  pipelineJob(jobName) {
    definition {
      cps {
        script('''
          jsl = library(
            identifier: "jenkins-shared-library@develop",
            retriever: modernSCM(
              [
                $class: 'GitSCMSource',
                remote: 'https://tech-devops@bitbucket.org/sil-dev/dsl-jobs.git',
                credentialsId: 'bitbucket_cred'
              ]
            )
          )
          pipeline {
    agent any

    parameters {
        choice(choices: ['dev', 'uat', 'prod'], description: 'select environment', name: 'ENVIRONMENT')
        string(name: 'Image_tag', defaultValue: '', description: 'Enter the docker image tag for rollback')
    }
    stages {
        stage("PARAMETERS") {
            steps {
               sh """
               echo parameters
               """
            }
        }
    }
}
rollbackJob()
              
        '''.stripIndent())               
        sandbox()     
      }
    }
  }
}


def buildPipelineJobs() {
    def reponame = repourl.split("/")[4][0..-5]
    def branchname = branch.replaceAll('/','-')
    def deployName = reponame + "/" + branchname + "_pipeline"
    def rollback = reponame + "/" + "rollbackJob" + "_pipeline"
    
    folder(reponame) {
       displayName(reponame)
       description(reponame)
    }     
    createDeploymentJob(deployName, reponame)
    createrollbackJob(rollback, reponame)
}

buildPipelineJobs()