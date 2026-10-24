pipeline {
    agent none

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(
            numToKeepStr: '10',
            artifactNumToKeepStr: '5'
        ))
    }

    stages {
        stage('Verify Build Tools') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21'
                    args '-u root:root'
                }
            }
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build and Test') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21'
                    args '-u root:root'
                }
            }
            steps {
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('Archive Artifact') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21'
                    args '-u root:root'
                }
            }
            steps {
                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }

        stage('Build Docker Image') {
            agent any
            steps {
                sh 'docker --version'
                sh 'docker build -t connected-vehicle-platform:latest .'
                sh 'docker images connected-vehicle-platform'
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Build, tests, artifact archiving, and Docker image creation completed.'
        }
        failure {
            echo 'FAILURE: Check the failed stage in Console Output.'
        }
        always {
            echo 'Pipeline execution completed.'
        }
    }
}

