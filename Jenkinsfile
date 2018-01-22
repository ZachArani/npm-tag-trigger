pipeline {
  agent any
  options { 
    buildDiscarder(logRotator(numToKeepStr: '10')) 
  }
  stages {
    stage('General Setup') {
      steps {
        git(url: 'https://github.com/ZachArani/npm-tag-trigger.git', branch: 'master')
     
      }
    }
  }
}
