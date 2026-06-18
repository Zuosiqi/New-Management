pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    tools {
        jdk 'jdk-21'
        maven 'maven-3.6.3'
    }

    environment {
        IMAGE_PREFIX = 'ea-management'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        K8S_DIR = 'k8s'
        GITHUB_CREDENTIAL_ID = 'github-auth'
        KUBECONFIG_CREDENTIAL_ID = 'k8s-kubeconfig'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '==== 1. 开始拉取代码 ===='
                checkout scm
                sh 'git log -1 --oneline'
            }
        }

        stage('Build') {
            steps {
                echo '==== 2. 开始 Maven 编译打包 ===='
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                echo '==== 3. 开始构建 Docker 镜像 ===='
                sh """
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/eureka-server:${IMAGE_TAG} -f eureka-server/Dockerfile ./eureka-server
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/gateway-service:${IMAGE_TAG} -f gateway-service/Dockerfile ./gateway-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/employee-service:${IMAGE_TAG} -f employee-service/Dockerfile ./employee-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/attendance-service:${IMAGE_TAG} -f attendance-service/Dockerfile ./attendance-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/leave-service:${IMAGE_TAG} -f leave-service/Dockerfile ./leave-service
                """
                
                echo '==== 4. 镜像导出与分发 (实验环境特殊处理) ===='
                sh """
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/eureka-server:${IMAGE_TAG} ${IMAGE_PREFIX}/eureka-server:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/gateway-service:${IMAGE_TAG} ${IMAGE_PREFIX}/gateway-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/employee-service:${IMAGE_TAG} ${IMAGE_PREFIX}/employee-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/attendance-service:${IMAGE_TAG} ${IMAGE_PREFIX}/attendance-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/leave-service:${IMAGE_TAG} ${IMAGE_PREFIX}/leave-service:latest
                """
            }
        }

        stage('Deploy to K8s') {
            steps {
                echo '==== 5. 开始部署到 Kubernetes 集群 ===='
                withKubeConfig([credentialsId: "${KUBECONFIG_CREDENTIAL_ID}"]) {
                    sh """
                        kubectl apply -f ${K8S_DIR}/00-namespace-and-secret.yaml
                        kubectl apply -f ${K8S_DIR}/01-eureka.yaml
                        kubectl apply -f ${K8S_DIR}/02-employee.yaml
                        kubectl apply -f ${K8S_DIR}/03-attendance.yaml
                        kubectl apply -f ${K8S_DIR}/04-leave.yaml
                        kubectl apply -f ${K8S_DIR}/05-gateway.yaml
                    """
                }
            }
        }
    }

    post {
        always {
            echo '==== 6. 流水线执行结束 ===='
        }
        success {
            echo '🎉 恭喜！构建和部署成功！'
        }
        failure {
            echo '❌ 糟糕！构建或部署失败，请检查日志！'
        }
    }
}
