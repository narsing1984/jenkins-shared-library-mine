import com.customized.GlobalVars

def call() {
def label = "jenkins-slave-${UUID.randomUUID().toString()}"
podTemplate(label: label, containers: [
    containerTemplate(name: 'slave1', image: 'gcr.io/sentrifugo/jenkins-slave:v1', ttyEnabled: true, command: 'cat')
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]) {
   node(label) {
def tagging = JOB_NAME
def tagclass = new GlobalVars()
def tag = tagclass.tupleFunc(tagging)
def folder = JOB_NAME.split("/")[0]


           stage("LOADING DEPLOY_CONFIG") {
                container('slave1') {  
sh 'rm deployconfig -rf; mkdir deployconfig; chmod -R 777 deployconfig'
dir ('deployconfig') {
git branch: 'master',
credentialsId: 'bitbucket_cred',
url: 'https://tech-devops@bitbucket.org/sil-dev/deploy-configs.git'
         }
       }
      } 
def p = pipelineCfg()
def namespace = p.namespace + params.ENVIRONMENT
    stage("ROLL BACK") {
            container('slave1') {
                sh """
                cd /home/jenkins/agent/workspace/${JOB_NAME}/deployconfig/${folder}
                sed -i 's/imagename/${p.appname}-${params.ENVIRONMENT}:${params.Image_tag}/g' ${p.appname}.yaml
                sed -i 's/{namespace}/${namespace}/g' ${p.appname}.yaml
                kubectl apply -f ${p.appname}.yaml
                """
            }
        
        }
        }
        }
}
            
        
