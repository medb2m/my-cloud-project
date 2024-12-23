pipeline {
    agent any

    environment {
        SONAR_SERVER = 'sonarqube-server'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/'
        NEXUS_URL = 'http://77.37.125.190:8081/repository/maven-releases'
    }

    stages {
        stage('Checkout c') {
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
        stage('Run Tests') {
            when {
                expression {
                    return false // Disable test for now
                }
            }
            steps {
                echo 'Running Tests...'
                dir('backend') {
                    sh 'mvn test'
                }
                dir('frontend') {
                    sh 'npm test'
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
                                -Dsonar.host.url=http://localhost:9000
                            """
                        }
                    }
                }
            }
        }
        stage('Deploy to Nexus') {
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
        stage('Docker Build and Push') {
            steps {
                echo 'Building and Pushing Docker Images to Docker Hub...'
                script {
                    try {
        docker.withRegistry(DOCKER_REGISTRY, 'docker-hub') {
            docker.build("matrixuv/backend:latest", 'backend/').push()
            docker.build("matrixuv/frontend:latest", 'frontend/').push()
        }
    } catch (Exception e) {
        echo "Error occurred during Docker Build and Push: ${e.message}"
        currentBuild.result = 'FAILURE'
        throw e
    }
                }
            }
        }
        /*stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying with Docker Compose...'
                sh 'docker-compose -f docker/docker-compose.yml up -d'
            }
        }*/
        stage('Deploy with Docker Compose') {
    steps {
        echo 'Deploying with Docker Compose...'
        script {
            try {
                sh 'docker-compose -f docker/docker-compose.yml up -d'
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
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
        always {
            echo 'Archiving artifacts and test results...'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
