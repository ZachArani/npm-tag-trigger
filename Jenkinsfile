pipeline {
  agent any
  stages {
    stage('General Setup') {
      environment {
        Test = 'Foo'
      }
      steps {
        git(url: 'https://github.com/ZachArani/npm-tag-trigger.git', branch: 'master')
      }
    }
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }
}