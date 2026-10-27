pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        ACR_NAME        = 'manuvehiclef0d03a32'
        ACR_LOGIN_SERVER = 'manuvehiclef0d03a32.azurecr.io'
        IMAGE_NAME      = 'connected-vehicle-platform'

        VM_HOST         = '85.211.245.154'
        VM_USER         = 'azureuser'

        // The application must listen on port 8070.
        APP_PORT        = '8070'

        // Maven runs inside a Docker container.
        MAVEN_IMAGE     = 'maven:3.9-eclipse-temurin-21'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh '''
                    set -eu

                    echo "Checking required tools..."

                    git --version
                    docker --version

                    test -f pom.xml
                    test -f Dockerfile

                    echo "Repository and Dockerfile found."
                '''
            }
        }

        stage('Compile, Test and Package') {
            steps {
                sh '''
                    set -eu

                    docker run --rm \
                      -v "$WORKSPACE:/workspace" \
                      -v "$HOME/.m2:/root/.m2" \
                      -w /workspace \
                      "$MAVEN_IMAGE" \
                      mvn -B clean verify
                '''
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    set -eu

                    docker build \
                      -t "$IMAGE_NAME:$BUILD_NUMBER" \
                      -t "$IMAGE_NAME:latest" \
                      .
                '''
            }
        }

        stage('Push Image to Azure ACR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'azure-acr-service-principal',
                        usernameVariable: 'ACR_CLIENT_ID',
                        passwordVariable: 'ACR_CLIENT_SECRET'
                    )
                ]) {
                    sh '''
                        set -eu

                        VERSIONED_IMAGE="$ACR_LOGIN_SERVER/$IMAGE_NAME:$BUILD_NUMBER"
                        LATEST_IMAGE="$ACR_LOGIN_SERVER/$IMAGE_NAME:latest"

                        # Authenticate without placing the secret in the command line.
                        printf '%s' "$ACR_CLIENT_SECRET" |
                          docker login "$ACR_LOGIN_SERVER" \
                            --username "$ACR_CLIENT_ID" \
                            --password-stdin

                        docker tag "$IMAGE_NAME:$BUILD_NUMBER" "$VERSIONED_IMAGE"
                        docker tag "$IMAGE_NAME:latest" "$LATEST_IMAGE"

                        docker push "$VERSIONED_IMAGE"
                        docker push "$LATEST_IMAGE"

                        # Remove registry credentials from this build agent.
                        docker logout "$ACR_LOGIN_SERVER"
                    '''
                }
            }
        }

        stage('Deploy to Azure VM') {
            steps {
                sshagent(credentials: ['azure-vm-ssh']) {
                    sh '''
                        set -eu

                        VERSIONED_IMAGE="$ACR_LOGIN_SERVER/$IMAGE_NAME:$BUILD_NUMBER"

                        # The VM must have Azure CLI installed, a working
                        # system-assigned managed identity, and Docker installed.
                        # Its identity must have AcrPull permission.

                        ssh \
                          -o StrictHostKeyChecking=accept-new \
                          "$VM_USER@$VM_HOST" \
                          "sudo bash -s -- '$VERSIONED_IMAGE' '$ACR_NAME' '$APP_PORT'" \
                          <<'REMOTE_SCRIPT'

                        set -eu

                        IMAGE="$1"
                        REGISTRY="$2"
                        APP_PORT="$3"

                        # Confirm that application configuration exists
                        # before changing the running container.
                        test -f /opt/connected-vehicle-platform/.env

                        # Authenticate as the VM's managed identity.
                        sudo az login --identity --output none

                        # Authenticate Docker to ACR using that identity.
                        sudo az acr login --name "$REGISTRY"

                        # Download the exact image built by this Jenkins job.
                        sudo docker pull "$IMAGE"

                        # Replace the existing application container.
                        sudo docker rm -f connected-vehicle-platform || true

                        sudo docker run -d \
                          --name connected-vehicle-platform \
                          --restart unless-stopped \
                          --env-file /opt/connected-vehicle-platform/.env \
                          -p "$APP_PORT:$APP_PORT" \
                          "$IMAGE"

                        # Confirm that Docker started the container.
                        sleep 5
                        sudo docker ps \
                          --filter name=connected-vehicle-platform

                        REMOTE_SCRIPT
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
            echo 'The image was pushed to ACR and deployment was attempted.'
        }

        failure {
            echo 'Pipeline failed. Review the first failed stage and its console log.'
        }

        always {
            echo "Build number: ${env.BUILD_NUMBER}"
        }
    }
}