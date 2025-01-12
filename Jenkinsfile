
pipeline{

    agent any

    environment{
        BRANCH_NAME = 'main'
    }

    tools {
            maven 'Maven 3.x'  // Referencing the Maven tool configured in Jenkins
    }

    stages{

        // stage 1 : checkout code from git
        stage('checkout'){
            steps{
                git branch:"${env.BRANCH_NAME}", credentialsId:"GithubCred", url: "https://github.com/Baljeet1699/MCP.git"
            }
        }

        

        // Stage 2: Run Docker Compose to start MySQL and Spring Boot containers
        stage('Docker Compose Up') {
            steps {
                script {
                    // Start the Docker containers using docker-compose.yml
                    sh 'docker-compose -f docker-compose.yml up -d'
                }
            }
        }
       
       
    }

}