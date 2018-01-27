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
        sh(returnStdout: true, script: 'git tag -l --points-at HEAD')
      }
    }
    stage('If on Master') {
      when {
        branch 'master'
      }
      steps {
        sh 'echo "this is master"'
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