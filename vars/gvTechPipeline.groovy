#!/usr/bin/env groovy

// vars/gvTechPipeline.groovy
def call(String name) {
    pipeline {
        agent any
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
            stage("Step - 1") {
                steps {
                    echo "Welcome dear ${name}"
                }
            }
        }
    }
}
