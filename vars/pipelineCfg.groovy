def call() {
    def test = JOB_NAME
    def (name, value) = test.split("/")
  Map pipelineCfg = readJSON(file: "/home/jenkins/agent/workspace/${JOB_NAME}/deployconfig/${name}/pipeline.json")
  return pipelineCfg
}

