pipeline {
  agent {
    node {
      label 'nodejs'
    }
    
  }
  stages {
    stage('General Setup') {
      steps {
        git(url: 'https://github.com/ZachArani/npm-tag-trigger.git', branch: 'master')
      }
    }
    stage('If on Master') {
      when {
        branch 'master'
      }
      steps {
        sh 'git describe HEAD'
      }
    }
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }
  parameters {
    string(name: 'releaseTag', defaultValue: 'snapshot')
    string(name: 'GITHUB_PR_HEAD_SHA', defaultValue: '')
  }
}