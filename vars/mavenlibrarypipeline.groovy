def call() {
def p = pipelineCfg()
 stage("ADDING CONFIG_FILES") {
                container('slave1') {      

    sh """
    cd /home/jenkins/workspace/${JOB_NAME}/
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
                cd /home/jenkins/workspace/${JOB_NAME}/
                mvn clean install
                }
                }
                }