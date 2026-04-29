pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    DOCKERHUB_REPO = "girishhardia/chatbot"
    ORG_SVC  = "organization-service"
    GW_SVC   = "gateway-service"
    CHAT_SVC = "chatbot-service"
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        script {
          env.GIT_SHA   = sh(script: "git rev-parse --short=8 HEAD", returnStdout: true).trim()
          env.IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_SHA}"
        }
      }
    }

    stage('Prep: Make mvnw executable') {
      steps {
        sh '''
          set -eux
          chmod +x organization-service/mvnw || true
          chmod +x gateway-service/mvnw || true
          chmod +x chatbot-service/mvnw || true
        '''
      }
    }

    stage('Tests (Maven Wrapper)') {
      parallel {
        stage('gateway-service tests') {
          steps {
            sh '''
              set -eux
              cd gateway-service
              ./mvnw -B -ntp clean test
            '''
          }
        }

        stage('chatbot-service tests') {
          steps {
            sh '''
              set -eux
              cd chatbot-service
              ./mvnw -B -ntp clean test -DskipTests
            '''
          }
        }

        stage('organization-service tests (skipped for now)') {
          steps {
            sh '''
              set -eux
              cd organization-service
              ./mvnw -B -ntp clean test -DskipTests
            '''
          }
        }
      }
    }

    stage('Docker Build Images') {
      parallel {
        stage('Build organization-service image') {
          steps {
            sh '''
              set -eux
              export DOCKER_BUILDKIT=0
              docker build -t ${ORG_SVC}:${IMAGE_TAG} organization-service
            '''
          }
        }

        stage('Build gateway-service image') {
          steps {
            sh '''
              set -eux
              export DOCKER_BUILDKIT=0
              docker build -t ${GW_SVC}:${IMAGE_TAG} gateway-service
            '''
          }
        }

        stage('Build chatbot-service image') {
          steps {
            sh '''
              set -eux
              export DOCKER_BUILDKIT=0
              docker build -t ${CHAT_SVC}:${IMAGE_TAG} chatbot-service
            '''
          }
        }
      }
    }

    stage('Security Scan (Trivy)') {
      steps {
        sh '''
          set -eux

          echo "🔐 Trivy scan: organization-service"
          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v $HOME/.cache/trivy:/root/.cache/trivy \
            aquasec/trivy:0.69.3 image \
            --severity CRITICAL \
            --exit-code 1 \
            ${ORG_SVC}:${IMAGE_TAG}

          echo "🔐 Trivy scan: gateway-service"
          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v $HOME/.cache/trivy:/root/.cache/trivy \
            aquasec/trivy:0.69.3 image \
            --severity CRITICAL \
            --exit-code 1 \
            ${GW_SVC}:${IMAGE_TAG}

          echo "🔐 Trivy scan: chatbot-service"
          docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            -v $HOME/.cache/trivy:/root/.cache/trivy \
            aquasec/trivy:0.69.3 image \
            --severity CRITICAL \
            --exit-code 1 \
            ${CHAT_SVC}:${IMAGE_TAG}
        '''
      }
    }

    stage('Push Images to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                          usernameVariable: 'DH_USER',
                                          passwordVariable: 'DH_TOKEN')]) {

          // Hard stop if DockerHub upload stalls
          timeout(time: 20, unit: 'MINUTES') {
            sh '''
              set -eux
              echo "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin

              # Tag each service into ONE repo using immutable tags
              docker tag ${ORG_SVC}:${IMAGE_TAG}  ${DOCKERHUB_REPO}:organization-${IMAGE_TAG}
              docker tag ${GW_SVC}:${IMAGE_TAG}   ${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}
              docker tag ${CHAT_SVC}:${IMAGE_TAG} ${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}
            '''
          }

          // Retry pushes (helps with intermittent 502)
          retry(2) {
            timeout(time: 20, unit: 'MINUTES') {
              sh '''
                set -eux
                docker push ${DOCKERHUB_REPO}:organization-${IMAGE_TAG}
                docker push ${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}
                docker push ${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}
              '''
            }
          }

          sh 'docker logout'
        }
      }
    }

    // stage('Deploy to Minikube (Rolling Update)') {
    //   steps {
    //     sh '''
    //       set -eux

    //       # Ensure we are talking to minikube
    //       kubectl config use-context minikube

    //       # Update images in running deployments (CD step)
    //       kubectl set image deployment/organization-service \
    //         organization-service=${DOCKERHUB_REPO}:organization-${IMAGE_TAG}

    //       kubectl set image deployment/chatbot-service \
    //         chatbot-service=${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}

    //       kubectl set image deployment/api-gateway \
    //         api-gateway=${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}

    //       # Wait for rollouts
    //       kubectl rollout status deployment/organization-service
    //       kubectl rollout status deployment/chatbot-service
    //       kubectl rollout status deployment/api-gateway
    //     '''
    //   }
    // }

    stage('Deploy to Minikube (Helm Umbrella Chart)') {
      steps {
        sh '''
          set -eux

          # Make sure we are using minikube
          kubectl config use-context minikube

          # Helm deploy / upgrade (NO latest tags)
          helm upgrade --install chatbot-platform ./helm \
            --set organization-service.image.tag="${IMAGE_TAG}" \
            --set chatbot-service.image.tag="${IMAGE_TAG}" \
            --set api-gateway.image.tag="${IMAGE_TAG}"

          # show release status
          helm status chatbot-platform
        '''
      }
    }

    stage('Write Image Artifact (metadata)') {
      steps {
        sh '''
          set -eux
          mkdir -p artifacts
          cat > artifacts/image-tags.txt <<EOF
${DOCKERHUB_REPO}:organization-${IMAGE_TAG}
${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}
${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}
EOF
          cat artifacts/image-tags.txt
        '''
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'artifacts/image-tags.txt', fingerprint: true, allowEmptyArchive: false
    }
  }
}