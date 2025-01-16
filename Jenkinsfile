pipeline {
    agent any

    environment {
        SONAR_SERVER = 'sonarqube-server'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/'
        NEXUS_URL = 'http://77.37.125.190:8081/repository/maven-releases'
        FRONTEND_IMAGE = 'matrixuv/angular_hub'
        BACKEND_IMAGE = 'matrixuv/springboot_hub'
        MYSQL_IMAGE = 'matrixuv/mysql_hub'
        GRAFANA_IMAGE = 'matrixuv/grafana_hub'
        PROMETHEUS_IMAGE = 'matrixuv/prometheus_hub'
        NEXUS_IMAGE = 'matrixuv/nexus_hub'
        SONARQUBE_IMAGE = 'matrixuv/sonarqube'
        IMAGE_TAG = "1.0.0"
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Pulling code from GitHub...'
                git branch: 'main',
                    url: 'https://github.com/medb2m/my-cloud-project.git'
            }
        }

        stage('Build Backend') {
            steps {
                echo 'Building Backend...'
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                echo 'Building Frontend...'
                dir('frontend') {
                    sh 'npm install && npm run build'
                }
            }
        }

        stage('Build and Push Images') {
            steps {
                echo 'Building and Pushing Docker Images...'
                script {
                    docker.withRegistry(DOCKER_REGISTRY, 'docker-hub') {
                        // Backend image
                        docker.build("${BACKEND_IMAGE}:${IMAGE_TAG}", 'backend/').push()
                        // Frontend image
                        docker.build("${FRONTEND_IMAGE}:${IMAGE_TAG}", 'frontend/').push()
                        // Push dependent images
                        sh "docker pull ${MYSQL_IMAGE}:${IMAGE_TAG} && docker tag ${MYSQL_IMAGE}:${IMAGE_TAG} ${MYSQL_IMAGE}:${IMAGE_TAG} && docker push ${MYSQL_IMAGE}:${IMAGE_TAG}"
                        sh "docker pull ${GRAFANA_IMAGE}:${IMAGE_TAG} && docker tag ${GRAFANA_IMAGE}:${IMAGE_TAG} ${GRAFANA_IMAGE}:${IMAGE_TAG} && docker push ${GRAFANA_IMAGE}:${IMAGE_TAG}"
                        sh "docker pull ${PROMETHEUS_IMAGE}:${IMAGE_TAG} && docker tag ${PROMETHEUS_IMAGE}:${IMAGE_TAG} ${PROMETHEUS_IMAGE}:${IMAGE_TAG} && docker push ${PROMETHEUS_IMAGE}:${IMAGE_TAG}"
                        sh "docker pull ${NEXUS_IMAGE}:${IMAGE_TAG} && docker tag ${NEXUS_IMAGE}:${IMAGE_TAG} ${NEXUS_IMAGE}:${IMAGE_TAG} && docker push ${NEXUS_IMAGE}:${IMAGE_TAG}"
                        sh "docker pull ${SONARQUBE_IMAGE}:${IMAGE_TAG} && docker tag ${SONARQUBE_IMAGE}:${IMAGE_TAG} ${SONARQUBE_IMAGE}:${IMAGE_TAG} && docker push ${SONARQUBE_IMAGE}:${IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying with Docker Compose...'
                script {
                    try {
                        sh '''
                        docker-compose -f docker-compose.yml down || true
                        docker-compose -f docker-compose.yml up -d
                        '''
                    } catch (Exception e) {
                        echo "Deployment failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            emailext (
                subject: "SUCCESS: Pipeline '${env.JOB_NAME}' (${env.BUILD_NUMBER})",
                body: """
                    Pipeline '${env.JOB_NAME}' (${env.BUILD_NUMBER}) completed successfully!
                    Check the build details at: ${env.BUILD_URL}
                """,
                to: 'benmohamed.mohamed@esprit.tn', 
                attachLog: true
            )
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
            emailext (
                subject: "FAILURE: Pipeline '${env.JOB_NAME}' (${env.BUILD_NUMBER})",
                body: """
                    Pipeline '${env.JOB_NAME}' (${env.BUILD_NUMBER}) failed!
                    Check the build details at: ${env.BUILD_URL}
                """,
                to: 'benmohamed.mohamed@esprit.tn', 
                attachLog: true
            )
        }
        always {
            echo 'Archiving artifacts and test results...'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
