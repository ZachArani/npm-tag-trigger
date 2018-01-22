pipeline {
  agent any
  parameters {
    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
  }
  stages {
    stage('General Setup') {
      steps {
        git(url: 'https://github.com/ZachArani/npm-tag-trigger.git', branch: 'master')
      }
    }
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }
}
