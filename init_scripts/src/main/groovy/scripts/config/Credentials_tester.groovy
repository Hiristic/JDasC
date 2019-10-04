pipeline {
  agent any
  stages {

    stage('usernamePassword') {
      steps {
        script {
          withCredentials([
            usernamePassword(credentialsId: 'server_login',
              usernameVariable: 'username',
              passwordVariable: 'password')
          ]) {
            print 'username = ' + username + ' password = ' + password
            print 'username.collect { it }=' + username.collect { it }
            print 'password.collect { it }=' + password.collect { it }
          }
        }
      }
    }
  }
}
