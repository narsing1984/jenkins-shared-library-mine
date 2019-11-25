def call() {
def p = pipelineCfg()
 stage("ADDING CONFIGFILES") {
                container('slave1') {      
    configFileProvider([configFile(fileId: 'a4bd32f6-8483-489a-a97f-b40654a9fc35', variable: 'npmrc')]) {
               sh """
               cd /home/jenkins/workspace/${JOB_NAME}/
               cp $npmrc .npmrc
               npm install
               sed -i 's/npm-group/npm-private/g' .npmrc
               npm publish
               """
               }
               }
               }
               }