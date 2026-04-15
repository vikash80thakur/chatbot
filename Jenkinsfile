pipeline {
  agent any

  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    // ✅ Docker Hub single repo
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

    // ✅ Keep tests (safe approach right now)
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
              ./mvnw -B -ntp clean test
            '''
          }
        }

        // org-service tests previously failed due to DB requirement in CI.
        // Keep it skipped until we add H2 profile or Testcontainers.
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

    // ✅ Build local images (as you already do successfully) [1](https://myoffice.accenture.com/personal/girish_hardia_accenture_com/_layouts/15/Doc.aspx?sourcedoc=%7BA56D0D9E-FF9C-440C-9A03-BE45DEEA2290%7D&file=temp.docx&action=default&mobileredirect=true)
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

    // ✅ MAIN ARTIFACT: Push images to Docker Hub under girishhardia/chatbot with different tags
    stage('Push Images to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                          usernameVariable: 'DH_USER',
                                          passwordVariable: 'DH_TOKEN')]) {
          sh '''
            set -eux
            echo "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin

            # Tag each service into ONE repo using distinct tags
            docker tag ${ORG_SVC}:${IMAGE_TAG}  ${DOCKERHUB_REPO}:organization-${IMAGE_TAG}
            docker tag ${GW_SVC}:${IMAGE_TAG}   ${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}
            docker tag ${CHAT_SVC}:${IMAGE_TAG} ${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}

            # Optional rolling tags (handy for latest)
            docker tag ${ORG_SVC}:${IMAGE_TAG}  ${DOCKERHUB_REPO}:organization-latest
            docker tag ${GW_SVC}:${IMAGE_TAG}   ${DOCKERHUB_REPO}:gateway-latest
            docker tag ${CHAT_SVC}:${IMAGE_TAG} ${DOCKERHUB_REPO}:chatbot-latest

            # Push versioned tags
            docker push ${DOCKERHUB_REPO}:organization-${IMAGE_TAG}
            docker push ${DOCKERHUB_REPO}:gateway-${IMAGE_TAG}
            docker push ${DOCKERHUB_REPO}:chatbot-${IMAGE_TAG}

            # Push rolling tags
            docker push ${DOCKERHUB_REPO}:organization-latest
            docker push ${DOCKERHUB_REPO}:gateway-latest
            docker push ${DOCKERHUB_REPO}:chatbot-latest

            docker logout
          '''
        }
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
      // Tests are important; we publish whatever exists
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

      // ✅ Main artifact reference is the pushed image tags (not JARs)
      archiveArtifacts artifacts: 'artifacts/image-tags.txt', fingerprint: true, allowEmptyArchive: false

      // Optional: keep jars archived for debugging (NOT the main artifact)
      // archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
    }
  }
}