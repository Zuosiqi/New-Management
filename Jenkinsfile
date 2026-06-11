pipeline {
    agent any

    tools {
        // 使用我们在 Jenkins 全局工具中配置的别名
        jdk 'jdk-21'
        maven 'maven-3.6.3'
    }

    environment {
        // Docker 镜像仓库前缀 (当前是本地仓库，如果是 Docker Hub 需修改)
        IMAGE_PREFIX = 'ea-management'
        // 构建标签，默认使用 Jenkins 的 BUILD_NUMBER
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        // K8s 部署文件的相对路径
        K8S_DIR = 'k8s'
        // GitHub 凭据 ID (我们在 Jenkins 中配置的 ID)
        GITHUB_CREDENTIAL_ID = 'github-auth'
        // Kubeconfig 凭据 ID (我们在 Jenkins 中配置的 ID)
        KUBECONFIG_CREDENTIAL_ID = 'k8s-kubeconfig'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '==== 1. 开始拉取代码 ===='
                // 使用 Jenkins 的 Git 插件拉取代码，请替换为你的实际 GitHub 仓库地址
                // git credentialsId: "${GITHUB_CREDENTIAL_ID}", url: 'https://github.com/your-username/EA-Management-System.git', branch: 'main'
                
                // 由于目前我们在同一个节点上实验，为了避免配置 Git URL，我们先模拟打印，或者你在这里填入你的 Git 地址
                echo '代码拉取完成（假设）'
            }
        }

        stage('Build') {
            steps {
                echo '==== 2. 开始 Maven 编译打包 ===='
                // 编译整个项目，跳过测试
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Push') {
            steps {
                echo '==== 3. 开始构建 Docker 镜像 ===='
                // 使用 nerdctl 替代 docker 进行构建，并指定命名空间为 k8s.io
                // 这样构建的镜像会直接存在于 containerd 中，K8s 可以直接拉取到
                sh """
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/eureka-server:${IMAGE_TAG} -f eureka-server/Dockerfile ./eureka-server
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/gateway-service:${IMAGE_TAG} -f gateway-service/Dockerfile ./gateway-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/employee-service:${IMAGE_TAG} -f employee-service/Dockerfile ./employee-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/attendance-service:${IMAGE_TAG} -f attendance-service/Dockerfile ./attendance-service
                    sudo nerdctl -n k8s.io build -t ${IMAGE_PREFIX}/leave-service:${IMAGE_TAG} -f leave-service/Dockerfile ./leave-service
                """
                
                echo '==== 4. 镜像导出与分发 (实验环境特殊处理) ===='
                // 使用 nerdctl 打 latest 标签
                sh """
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/eureka-server:${IMAGE_TAG} ${IMAGE_PREFIX}/eureka-server:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/gateway-service:${IMAGE_TAG} ${IMAGE_PREFIX}/gateway-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/employee-service:${IMAGE_TAG} ${IMAGE_PREFIX}/employee-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/attendance-service:${IMAGE_TAG} ${IMAGE_PREFIX}/attendance-service:latest
                    sudo nerdctl -n k8s.io tag ${IMAGE_PREFIX}/leave-service:${IMAGE_TAG} ${IMAGE_PREFIX}/leave-service:latest
                """
                // 注意：在真实的 K8s 生产环境中，最好是将镜像推送到私有仓库（如 Harbor）。
                // 这里我们暂且省略使用 nerdctl 导出导入各个 worker 节点的复杂操作，
                // 如果你的 k8s 部署 yaml 中的 image 标签是 latest 且策略是 IfNotPresent，
                // 并且你已经在所有节点上都有了 latest 的底包镜像，部署时会自动拉取。
                // 最佳实践：建议后续搭建 Harbor 私有镜像仓库。
            }
        }

        stage('Deploy to K8s') {
            steps {
                echo '==== 5. 开始部署到 Kubernetes 集群 ===='
                // 使用 Kubernetes CLI 插件加载 kubeconfig 凭据
                withKubeConfig([credentialsId: "${KUBECONFIG_CREDENTIAL_ID}"]) {
                    // 更新 k8s yaml 文件中的镜像版本 (使用 sed 替换)
                    // 注意：这里的 yaml 文件需要你在仓库里准备好，并且里面的镜像标签最好用占位符或者统一用 latest。
                    // 这里演示直接重新 apply。
                    sh """
                        kubectl apply -f ${K8S_DIR}/01-eureka.yaml
                        kubectl apply -f ${K8S_DIR}/02-employee.yaml
                        kubectl apply -f ${K8S_DIR}/03-attendance.yaml
                        kubectl apply -f ${K8S_DIR}/04-leave.yaml
                        kubectl apply -f ${K8S_DIR}/05-gateway.yaml
                    """
                    
                    // 触发滚动更新，确保拉取新的镜像 (如果使用了 latest 标签，需要重启 pod)
                    // sh "kubectl rollout restart deployment -n ea-ms"
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
