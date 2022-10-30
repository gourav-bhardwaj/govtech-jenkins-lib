def helmDeployStep() {
    String KUBE_CONTEXT = "kubernetes-admin@kubernetes"
    String KUBE_CREDENTIAL_ID = "GOV_KUBE_CONFIG"
    String HELM_FILENAME = "deploy-dev"
    String jobName = application
    String NAMESPACE = BRANCH_NAME
    String BUILD_TIMESTAMP = "10-29-2022"
    String version = "11"
    String BRANCH_NAME = "dev"
    String DOCKER_REGISTRY = "govkumardocker"
    String application = "govtech-api-gateway"
     withCredentials([file(credentialsId: "${KUBE_CREDENTIAL_ID}", variable: 'KUBECONFIG_CONTENT')]) {
        sh "pwd"
        sh "ls -ltr"
        sh "helm version --kubeconfig ${KUBECONFIG_CONTENT} --kube-context ${KUBE_CONTEXT}"
        //sh "helm upgrade --install --namespace ${NAMESPACE} ${jobName} helm-chart/spring-boot -f values/${HELM_FILENAME}.yaml --set image.repository=${DOCKER_REGISTRY}/${application},image.tag=${BUILD_TIMESTAMP}.${version}.${BRANCH_NAME} --kubeconfig ${KUBECONFIG_CONTENT} --kube-context ${KUBE_CONTEXT} --debug --atomic"
    }
}

def dockerBuildAndPush() {
    String DOCKER_CREDENTIALS_ID = "GOV_DOCKER_CRED"
    String BUILD_TIMESTAMP = "10-29-2022"
    String version = "11"
    String BRANCH_NAME = "dev"
    String DOCKER_REGISTRY = "govkumardocker"
    String application = "govtech-api-gateway"
    withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
        sh "docker build -t ${DOCKER_REGISTRY}/${application}:${BUILD_TIMESTAMP}.${version}.${BRANCH_NAME} ."
        sh "docker push ${DOCKER_REGISTRY}/${application}:${BUILD_TIMESTAMP}.${version}.${BRANCH_NAME}"
    }
}

def call() {
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
                   git url: "https://github.com/gourav-bhardwaj/govtech-api-gateway.git", branch: 'dev', credentialsId: 'govtech-git-cred-id'
                   sh 'mkdir -p helm-chart'
                   dir('helm-chart') {
                     git url: "https://github.com/gourav-bhardwaj/govtech-helm-chart-app.git", branch: 'dev', credentialsId: 'govtech-git-cred-id'
                   }
               }
            }
            stage("Docker build & push") {
              steps {
                script {
                  dockerBuildAndPush()
                }
              }
            }
            stage("Helm Deploy") {
                steps {
                    script {
                       helmDeployStep()
                    }
                }
            }
        }
    }
}
