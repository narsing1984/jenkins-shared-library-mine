import com.customized.GlobalVars

def call() {

def tagging = JOB_NAME
def tagclass = new GlobalVars()
def tag = tagclass.tupleFunc(tagging)
def folder = JOB_NAME.split("/")[0]
def p = pipelineCfg()
def namespace = p.namespace + tag

 stage("ADDING CONFIGFILES") {
                container('slave1') {      
    configFileProvider([configFile(fileId: 'a4bd32f6-8483-489a-a97f-b40654a9fc35', variable: 'npmrc')]) {
               sh """
               cd /home/jenkins/agent/workspace/${JOB_NAME}/
               cp $npmrc .npmrc
               npm install
               sed -i 's/npm-group/npm-private/g' .npmrc
               npm publish
               """
               }
               }
               }
 
    stage("BUILD IMAGE") {
                container('slave1') { 
                sh """
                docker build --build-arg ENV=${tag} -t gcr.io/sentrifugo/${p.appname}-${tag}:$BUILD_NUMBER .
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
                cd /home/jenkins/agent/workspace/${JOB_NAME}/deployconfig/${folder}
                sed -i 's/imagename/${p.appname}-${tag}:$BUILD_NUMBER/g' ${p.appname}.yaml
                sed -i 's/{namespace}/${namespace}/g' ${p.appname}.yaml
                kubectl apply -f ${p.appname}.yaml
                """
                  
            }
        }
        }

            
        
