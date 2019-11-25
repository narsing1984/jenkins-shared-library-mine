import com.customized.GlobalVars

def call() {
def tagging = JOB_NAME
def tagclass = new GlobalVars()
def tag = tagclass.tupleFunc(tagging)
def p = pipelineCfg()
def namespace = p.namespace + tag


 stage("ADDING CONFIG_FILES") {
                container('slave1') {      

    sh """
    cd /home/jenkins/agent/workspace/${JOB_NAME}/
    sed -i 's/buildnumber/${BUILD_NUMBER}/g' pom.xml
    """
    configFileProvider([configFile(fileId: '6f516d95-a2b4-41a5-b5e2-ab66ffbe76ae', variable: 'settingsfile')]) {
               sh """
               mkdir -p /root/.m2/
               cp $settingsfile /root/.m2/settings.xml
               """
               }
    configFileProvider([configFile(fileId: '1146441e-cc8d-4a45-a286-c1f697d2a088', variable: 'securityfile')]) {
               sh """
               cp $securityfile /root/.m2/settings-security.xml
               """
               }
               }
               }
 
    stage("BUILD IMAGE") {
                container('slave1') { 
                sh """
                cd /home/jenkins/agent/workspace/${JOB_NAME}/
                mvn clean install
                docker build -t gcr.io/sentrifugo/${p.appname}-${tag}:$BUILD_NUMBER .
                """
                }
                }
    stage('PUSH IMAGE') {
                 container('slave1') {
                 docker.withRegistry('https://gcr.io', 'gcr:sentrifugo') {
                 sh "docker push gcr.io/sentrifugo/${p.appname}-${tag}:$BUILD_NUMBER"
    
    
            }
             }
            }
    stage("DEPLOY ON KUBERNETES") {
            container('slave1') {
                sh """
                cd /home/jenkins/agent/workspace/${JOB_NAME}/deployconfig/${p.appname}
                sed -i 's/imagename/${p.appname}-${tag}:$BUILD_NUMBER/g' ${p.appname}.yaml
                sed -i 's/{namespace}/${namespace}/g' ${p.appname}.yaml
                kubectl apply -f ${p.appname}.yaml
                """
            }
        
        }
        }

            
        
