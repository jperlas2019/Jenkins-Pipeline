def call(dockerRepoName, imageName, portNum) {
    pipeline {
    agent any

    parameters {
        booleanParam(defaultValue: false, description: 'Deploy the App', name: 'DEPLOY')
    }

    stages {
        stage('Build') {
            steps {
                sh 'pip install -r requirements.txt'
            }
        }
        stage('Lint') {
            steps {
                sh 'pylint-fail-under --fail_under 5 *.py'
            }
        }
        stage('Test and Coverage') {
            steps {
                script {
                        def test_reports_exists = fileExists 'test-reports'
                        if (test_reports_exists) {
                            sh "rm -f test-reports/*.xml"
                        }

                        def api_test_reports_exits = fileExists 'api-test-reports'
                        if (api_test_reports_exits) {
                            sh "rm -rf api-test-reports/*.xml"
                        }
                    def files = findFiles(glob: "test*.py")
                    for (int i=0;i<files.size();i++) {
                        sh """python3 -m coverage run --omit */site-packages/*,*/dist-packages/* ${files[i].name}"""
                    }
                }
            }
            post {
                always {
                    script {
                        sh """python3 -m coverage report"""
                        def test_reports_exists = fileExists 'test-reports'
                        if (test_reports_exists) {
                            junit "test-reports/*.xml"
                        }

                        def api_test_reports_exits = fileExists 'api-test-reports'
                        if (api_test_reports_exits) {
                            junit "api-test-reports/*.xml"
                        }
                    }
                }
            }
        }

        stage('Package') {
            when {
                expression { env.GIT_BRANCH == 'origin/main' }
            }
            steps {
                withCredentials([string(credentialsId: 'jperlas', variable: 'TOKEN')]) {
                    sh "docker login -u 'jperlas' -p '$TOKEN' docker.io"
                    sh "docker build -t ${dockerRepoName}:latest --tag jperlas/${dockerRepoName}:${imageName} ."
                    sh "docker push jperlas/${dockerRepoName}:${imageName}"
                }
            }
        }

        stage('Zip Artifacts') {
            steps {
                script {
                    sh 'zip app.zip *.py'
                    archiveArtifacts artifacts: 'app.zip'
                }
            }
        }

        stage('Deliver') {
            when {
                expression { params.DEPLOY }
            }
            steps {
                sh "docker stop ${dockerRepoName} || true && docker rm ${dockerRepoName} || true"
                sh "docker run -d -p ${portNum}:${portNum} --name ${dockerRepoName} ${dockerRepoName}:latest"
            }
        }
    }
}

}
