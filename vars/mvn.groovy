def call(String name = 'human') {
    properties([parameters([string(defaultValue: 'test', description: 'test or verify', name: 'testType', trim: false)])])

    node {
        stage("git") {
            git url: 'https://github.com/hyunil-shin/java-maven-junit-helloworld.git', branch: 'failed'

        }
        
        stage('build') {
            withEnv(["PATH+MAVEN=${tool 'mvn-3.6.0'}/bin"]) {
                sh 'mvn --version'
                try {
                    sh "mvn clean ${params.testType}"
                } catch(e) {
                    currentBuild.result = 'FAILURE'
                }

            }
        }
        
        stage('report') {
            junit 'target/surefire-reports/*.xml'
            jacoco execPattern: 'target/**.exec'
            archiveArtifacts artifacts: 'target/surefire-reports/*.xml'
            if (currentBuild.result == 'FAILURE') {
                emailext body: 'Test Message', subject: 'Test Subject', to: 'woonghyun.ka@nhnpayco.com'
            }


        }
    }

}



