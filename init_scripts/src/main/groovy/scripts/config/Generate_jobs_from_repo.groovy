import jenkins.model.Jenkins;
import static groovy.io.FileType.FILES
import java.io.File
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob

@NonCPS
def getAllJenkinsfiles() {
  def jenkinsfiles = []
    new File(WORKSPACE, "jenkinsfiles").traverse(type: FILES, nameFilter: ~/.*\.groovy/) {
        jenkinsfiles.add(it)
    }
    return jenkinsfiles
}

pipeline {
  agent any
  parameters {
    string(
      description: 'Select repository to generate jobs from',
      name: 'repo',
      defaultValue: 'https://github.com/Hiristic/Jenkins-global-lib.git',
      trim: false
    )
  }
  stages {
    stage('Generate jobs') {
      steps {
        script {
          echo "Job URL: "+currentBuild.absoluteUrl
          git params.repo
          
          def scripts = []
          scripts = getAllJenkinsfiles()
          if (scripts!=null) {
            // sort to run them in an alphabetic order
            scripts.sort().each {
              createJob(it)
            }
          }
        }
      }
    }
  }
}

def createJob(File jenkinsfile){
  jobName = jenkinsfile.name.minus(".groovy")
  WorkflowJob project = Jenkins.instance.getItem(jobName)
  if (project == null) {
    project = Jenkins.instance.createProject(WorkflowJob.class, jobName)
    echo "Generated new job: "+jobName
  }
  project.setDefinition(new CpsFlowDefinition(jenkinsfile.text, false))
  echo "Applied config to job: "+jobName
}