pipeline {
    agent any
    
    tools {
        maven 'Maven3.8.7' // Nombre configurado en Jenkins Global Tool Configuration
        jdk 'Jdk17'        // Nombre configurado en Jenkins Global Tool Configuration
    }
    
    environment {
        // Variables de SonarQube
        SONAR_HOST_URL = 'http://sonarqube:9000' // Cambia por tu URL de SonarQube
        
        // Configuración Maven
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        stage('Setup Variables') {
            steps {
                script {
                    // Limpiar el nombre del job de todos los caracteres problemáticos
                    def cleanName = "${env.JOB_NAME}"
                        .replaceAll('/', '-')
                        .replaceAll('%2F', '-')
                        .replaceAll('%20', '-')
                        .replaceAll(' ', '-')
                        .replaceAll('[^a-zA-Z0-9\\-_\\.:]+', '-')  // Solo permite caracteres válidos
                        .replaceAll('^-+|-+$', '')  // Quita guiones al inicio y final
                        .toLowerCase()
                    
                    // Asegurar que no empiece con número
                    if (cleanName.matches('^[0-9].*')) {
                        cleanName = 'proj-' + cleanName
                    }
                    
                    // Establecer variables de entorno
                    env.SONAR_PROJECT_KEY = cleanName
                    env.SONAR_PROJECT_NAME = cleanName
                    
                    echo "🔧 Nombre original: ${env.JOB_NAME}"
                    echo "🔧 SonarQube Project Key: ${env.SONAR_PROJECT_KEY}"
                    echo "🔧 SonarQube Project Name: ${env.SONAR_PROJECT_NAME}"
                }
            }
        }

        stage('Compile') {
            steps {
                echo '🔨 Compilando el proyecto...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Ejecutando tests...'
                sh 'mvn org.jacoco:jacoco-maven-plugin:0.8.8:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.8:report'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    
                    // Archivar reportes de cobertura si existen
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Análisis de calidad con SonarQube...'
                withSonarQubeEnv('Sonarqube') { // Nombre configurado en Jenkins
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.java.coveragePlugin=jacoco \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                        -Dsonar.junit.reportPaths=target/surefire-reports \
                        -Dsonar.java.binaries=target/classes
                    '''
                }
            }
        }
        // stage('Quality Gate') {
        //     steps {
        //         echo '🚪 Verificando Quality Gate...'
        //         timeout(time: 2, unit: 'MINUTES') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }
    }
    
    post {
        always {
            echo '🧹 Limpiando workspace...'
        }
        success {
            echo '✅ Pipeline ejecutado exitosamente!'
            // Aquí puedes añadir notificaciones de éxito
        }
        failure {
            echo '❌ Pipeline falló!'
            // Aquí puedes añadir notificaciones de fallo
        }
        unstable {
            echo '⚠️ Pipeline inestable!'
        }
    }
}