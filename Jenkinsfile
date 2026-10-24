pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(
            logRotator(
                numToKeepStr: '15',
                artifactNumToKeepStr: '10'
            )
        )
    }

    environment {
        APP_NAME = 'connected-vehicle-platform'
        DOCKER_IMAGE = 'connected-vehicle-platform'
        MAVEN_IMAGE = 'maven:3.9-eclipse-temurin-21'
    }

    stages {

        // 1. Get source code from Git
        stage('Checkout') {
            steps {
                checkout scm

                sh '''
                    echo "Application: $APP_NAME"
                    echo "Branch: ${BRANCH_NAME:-main}"
                    echo "Build number: $BUILD_NUMBER"
                    git log -1 --oneline
                '''
            }
        }

        // 2. Verify the Jenkins host and Docker
        stage('Verify Environment') {
            steps {
                sh '''
                    set -eu

                    echo "=== Operating System ==="
                    uname -a

                    echo "=== Docker Version ==="
                    docker --version

                    echo "=== Docker Daemon ==="
                    docker info --format 'Docker engine: {{.ServerVersion}}'

                    echo "=== Maven Container ==="
                    docker run --rm "$MAVEN_IMAGE" mvn -version

                    echo "=== Java in Maven Container ==="
                    docker run --rm "$MAVEN_IMAGE" java -version
                '''
            }
        }

        // 3. Compile source code
        stage('Compile') {
            steps {
                sh '''
                    set -eu

                    docker run --rm \
                        -v "$WORKSPACE:/workspace" \
                        -v "$WORKSPACE/.m2:/root/.m2" \
                        -w /workspace \
                        "$MAVEN_IMAGE" \
                        mvn -B -DskipTests compile
                '''
            }
        }

        // 4. Run unit tests and package verification
        stage('Test and Verify') {
            steps {
                sh '''
                    set -eu

                    docker run --rm \
                        -v "$WORKSPACE:/workspace" \
                        -v "$WORKSPACE/.m2:/root/.m2" \
                        -w /workspace \
                        "$MAVEN_IMAGE" \
                        mvn -B test
                '''
            }

            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/TEST-*.xml',
                        allowEmptyResults: true
                    )

                    sh '''
                        if find . -path '*/target/surefire-reports/TEST-*.xml' \
                            -type f -print -quit | grep -q .; then
                            echo "Test report files found."
                        else
                            echo "WARNING: No unit-test reports found."
                            echo "Add unit tests to enable meaningful test reporting."
                        fi
                    '''
                }
            }
        }

        // 5. Build the deployable JAR
        stage('Package Application') {
            steps {
                sh '''
                    set -eu

                    docker run --rm \
                        -v "$WORKSPACE:/workspace" \
                        -v "$WORKSPACE/.m2:/root/.m2" \
                        -w /workspace \
                        "$MAVEN_IMAGE" \
                        mvn -B -DskipTests package

                    echo "=== Generated JAR ==="
                    ls -lh target/*.jar
                '''
            }
        }

        // 6. Archive the application artifact in Jenkins
        stage('Archive JAR') {
            steps {
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        // 7. Build the Docker image on the Jenkins host
        stage('Build Docker Image') {
            steps {
                sh '''
                    set -eu

                    docker build \
                        --pull \
                        -t "$DOCKER_IMAGE:$BUILD_NUMBER" \
                        -t "$DOCKER_IMAGE:latest" \
                        .
                '''
            }
        }

        // 8. Validate the resulting image
        stage('Validate Docker Image') {
            steps {
                sh '''
                    set -eu

                    echo "=== Image Details ==="
                    docker image inspect \
                        "$DOCKER_IMAGE:$BUILD_NUMBER" \
                        --format 'Image ID: {{.Id}}'

                    echo "=== Java Runtime ==="
                    docker run --rm \
                        --entrypoint java \
                        "$DOCKER_IMAGE:$BUILD_NUMBER" \
                        -version

                    echo "=== Built Images ==="
                    docker images "$DOCKER_IMAGE"
                '''
            }
        }
    }

    post {
        success {
            echo '''
            SUCCESS: CI pipeline completed.
            Source checkout, compilation, test execution,
            packaging, artifact archiving, Docker build,
            and image validation completed.
            '''
        }

        failure {
            echo '''
            FAILURE: The pipeline stopped at a failed stage.
            Check the Jenkins Console Output for the exact error.
            '''
        }

        aborted {
            echo 'Pipeline was aborted or timed out.'
        }

        always {
            echo "Build #${BUILD_NUMBER} finished."
        }
    }
}
