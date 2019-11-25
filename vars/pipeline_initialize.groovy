def call() {
def label = "jenkins-slave-${UUID.randomUUID().toString()}"
podTemplate(label: label, containers: [
    containerTemplate(name: 'slave1', image: 'gcr.io/sentrifugo/jenkins-slave:v1', ttyEnabled: true, command: 'cat')
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]) {
   node(label) {
 def (folder, job) = JOB_NAME.split("/")
 def (branching, pipeline) = job.split("_")
 def branchname = branching.replaceFirst('-','/')
    stage("SCM CHECKOUT") {
                container('slave1') {  
       checkout([$class: 'GitSCM',
                 branches: [[name: "*/${branchname}"]],
        doGenerateSubmoduleConfigurations: false,
        extensions: [],
        submoduleCfg: [],
        userRemoteConfigs: [[
            credentialsId: 'bitbucket_cred', 
          url: "https://tech-devops@bitbucket.org/sil-dev/${folder}.git"
             ]]])
                
     stage("LOADING PIPELINE_CONFIG") {
                container('slave1') {  
sh 'rm deployconfig -rf; mkdir deployconfig; chmod -R 777 deployconfig'
dir ('deployconfig') {
git branch: 'master',
credentialsId: 'bitbucket_cred',
url: 'https://tech-devops@bitbucket.org/sil-dev/deploy-configs.git'
}

   
   
   def p = pipelineCfg()

   switch(p.pipelineType) {
      case 'maven':
        // Instantiate and execute a maven pipeline
            mavenpipeline()
   }
   switch(p.pipelineType) {
      case 'node':
        // Instantiate and execute a node pipeline
            nodepipeline()
   }
   switch(p.pipelineType) {
      case 'python':
        // Instantiate and execute a Python pipeline
            pythonpipeline()
               }
   switch(p.pipelineType) {
      case 'mavenlibrary':
        // Instantiate and execute a Python pipeline
            mavenlibrarypipeline()
               }
   switch(p.pipelineType) {
      case 'nodelibrary':
        // Instantiate and execute a Python pipeline
            nodelibrarypipeline()
               }
                }
     }
   }
}
}
}
}