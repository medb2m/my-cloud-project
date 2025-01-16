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
        SONARQUBE_IMAGE = 'matrixuv/sonarqube_hub'
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

        stage('Run Backend Tests') {
            steps {
                script {
                    echo 'Running Backend Tests...'
                    docker.withRegistry(DOCKER_REGISTRY, 'docker-hub') {
                        try {
                            dir('backend') {
                                sh 'mvn -Dtest=com.bezkoder.spring.jpa.h2.controller.TutorialControllerTests test'
                            }
                        } catch (Exception e) {
                            echo "Backend tests failed: ${e.message}"
                            currentBuild.result = 'FAILURE'
                            throw e
                        }
                    }
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build and Push Images') {
            steps {
                echo 'Building and Pushing Docker Images...'
                script {
                    docker.withRegistry(DOCKER_REGISTRY, 'docker-hub') {
                        docker.build("${BACKEND_IMAGE}:${IMAGE_TAG}", 'backend/').push()
                        docker.build("${FRONTEND_IMAGE}:${IMAGE_TAG}", 'frontend/').push()
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
                        // Step 1: Stop and remove existing containers
                        echo 'Stopping and removing existing containers...'
                        sh '''
                        docker-compose -f docker-compose.yml down || true
                        docker rm -f mycloud_backend || true
                        docker rm -f mycloud_frontend || true
                        docker rm -f mycloud_mysql || true
                        docker rm -f mycloud_prometheus || true
                        docker rm -f mycloud_grafana || true
                        docker rm -f mycloud_nexus || true
                        docker rm -f mycloud_sonarqube || true
                        '''

                        // Step 2: Remove the network (if it exists)
                        echo 'Removing existing network...'
                        sh '''
                        docker network rm mycloud_app_network || true
                        '''

                        // Step 3: Bring up the containers
                        echo 'Starting containers with Docker Compose...'
                        sh '''
                        docker-compose -f docker-compose.yml up -d
                        '''

                        // Step 4: Wait for MySQL to be ready
                        echo 'Waiting for MySQL to be ready...'
                        sh '''
                        while ! docker exec mycloud_mysql mysqladmin ping -h localhost -u root -proot --silent; do
                            sleep 5
                        done
                        echo "MySQL is ready!"
                        '''
                    } catch (Exception e) {
                        echo "Deployment failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Running SonarQube analysis...'
                script {
                    withSonarQubeEnv(SONAR_SERVER) {
                        dir('backend') {
                            sh """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=my-app \
                                -Dsonar.host.url=http://mycloud_sonarqube:9000
                            """
                        }
                    }
                }
            }
        }

        stage('Publish Artifacts to Nexus') {
            steps {
                echo 'Deploying to Nexus...'
                dir('backend') {
                    sh """
                    mvn deploy \
                        -DaltDeploymentRepository=deploymentRepo::default::${NEXUS_URL} \
                        -DskipTests=true
                    """
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
                to: 'medobouh@gmail.com', 
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
                to: 'medobouh@gmail.com', 
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
