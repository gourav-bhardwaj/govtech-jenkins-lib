#!/usr/bin/env groovy

// vars/gvTechPipeline.groovy
def call(String name) {
    pipeline {
        agent any
        tools {
            gradle 'mygradle'
            git 'mygit'
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
                   sh "git rev-parse --short HEAD > .git/commit"
                   sh "basename `git rev-parse --show-toplevel` > .git/image"
                   COMMIT = readFile('.git/commit').trim()
                   echo "COMMIT ID is $COMMIT"
                   sh 'git name-rev --name-only HEAD > GIT_BRANCH'
                   sh 'cat GIT_BRANCH | cut -f3 -d "/" > test'
                   BRANCH_NAME = readFile('test').trim()
                   NAMESPACE = ""
                   CHANNEL = ""
                   KUBE_CONTEXT = ""
                   KUBE_CREDENTIAL_ID = ""
                   if (BRANCH_NAME == 'dev') {
                        NAMESPACE = "gv-tech"
                        CHANNEL = "dev"
                        KUBE_CONTEXT = "kubernetes-admin@kubernetes"
                        KUBE_CREDENTIAL_ID = "GOVTECH_KUBE_CRED"
                        NEW_BRANCH_NAME = readFile('test').trim()
                        echo "********This is $NEW_BRANCH_NAME**************"
               } else if (BRANCH_NAME == 'pre-dev') {
                        NAMESPACE = "gov-tech-pre-dev"
                        CHANNEL = "pre-dev"
                        KUBE_CONTEXT = "kubernetes-admin@kubernetes"
                        KUBE_CREDENTIAL_ID = "GOVTECH_KUBE_CRED"
                        NEW_BRANCH_NAME = readFile('test').trim()
                        echo "********This is $NEW_BRANCH_NAME**************"
               }
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
