import jenkins.model.Jenkins
import jenkins.plugins.git.GitSCMSource
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import org.jenkinsci.plugins.workflow.job.WorkflowJob

def generator_job = "Generate_jobs_from_code_dev"

println("=== Initialize all the jobs")
println(Jenkins.instance.getItem(generator_job))
if (Jenkins.instance.getItem(generator_job) != null) {
    println("Generate_jobs_from_code job has already been initialized, skipping the step")
    return
}

// Create initial jobs
WorkflowJob project2 = Jenkins.instance.createProject(WorkflowJob.class, "Credentials_tester")
project2.setDescription("This job will test if the encripted credentials are encoded corectly")
File credentials_test = new File(Jenkins.instance.rootDir, "init.groovy.d/scripts/config/Credentials_tester.groovy")
project2.setDefinition(new CpsFlowDefinition(credentials_test.text, true))

WorkflowJob project = Jenkins.instance.createProject(WorkflowJob.class, generator_job)
project.setDescription("This job will generate jobs for each jenkinsfile in repository")
File job_generator = new File(Jenkins.instance.rootDir, "init.groovy.d/scripts/config/Generate_jobs_from_repo.groovy")
project.setDefinition(new CpsFlowDefinition(job_generator.text, false))
