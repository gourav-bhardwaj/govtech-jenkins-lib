def call() {
    String KUBE_CONTEXT = "kubernetes-admin@kubernetes"
    String KUBE_CREDENTIAL_ID = "GOV_KUBE_CONFIG"
    String BUILD_TIMESTAMP = "10-29-2022"
    String version = "11"
    String BRANCH_NAME = "dev"

    String DOCKER_REGISTRY = "govkumardocker"
    String application = "govtech-api-gateway"
    String HELM_FILENAME = "deploy-dev"
    String jobName = application
    String NAMESPACE = BRANCH_NAME

    //sh "helm version"
    pipeline {
        agent any
        tools {
            gradle 'mygradle'
        }
        stages {
            stage("Helm Deploy") {
                steps {
                    script {
                        withCredentials([file(credentialsId: "${KUBE_CREDENTIAL_ID}", variable: 'KUBECONFIG_CONTENT')]) {
                            sh "pwd"
                            sh "ls -ltr"
                            sh "helm version --kubeconfig ${KUBECONFIG_CONTENT} --kube-context ${KUBE_CONTEXT}"
                            //sh "helm upgrade --install --namespace ${NAMESPACE} ${jobName} helm-chart/spring-boot -f values/${HELM_FILENAME}.yaml --set image.repository=${DOCKER_REGISTRY}/${application},image.tag=${BUILD_TIMESTAMP}.${version}.${BRANCH_NAME} --kubeconfig ${KUBECONFIG_CONTENT} --kube-context ${KUBE_CONTEXT} --debug --atomic"
                        }
                    }
                }
            }
        }
    }
}
