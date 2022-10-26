#!/usr/bin/env groovy

// vars/gvTechPipeline.groovy
def call(String name) {
    pipeline {
        agent any
        tools {
            gradle 'mygradle'
        }
        stages {
            stage("GIT") {
               steps {
                   step([$class: 'WsCleanup'])
                   //checkout scm
                   sh 'mkdir -p helm-chart'
                   dir('helm-chart') {
                     git url: "https://github.com/gourav-bhardwaj/govtech-helm-chart-app.git", branch: 'dev', credentialsId: 'govtech-git-cred-id'
                   }
               }
            }
            stage("Env Variable") {
               steps {
                 script {
                   sh "git config --global init.defaultBranch master"
                   sh "git branch -m master"
                   sh "git init"  
                   sh "git rev-parse --short HEAD > .git/commit"
                   sh "basename `git rev-parse --show-toplevel` > .git/image"
                   COMMIT = readFile('.git/commit').trim()
                   echo "COMMIT ID is $COMMIT"
                 }
               }
            }
            stage("Step - 1") {
                steps {
                    echo "Welcome dear ${name}"
                }
            }
        }
    }
}
