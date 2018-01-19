pipeline {
  agent any
  stages {
    stage('General Setup') {
      steps {
        git(url: 'https://github.com/ZachArani/npm-tag-trigger.git', branch: 'master')
        script {
          options {
            
            
            
            buildDiscarder(logRotator(numToKeepStr: '10'))
          }
        }
        
      }
    }
  }
}