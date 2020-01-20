# Dockerfile para criar imagem do serviço do gfinanceiro

FROM openjdk:10
MAINTAINER Pinguin Corp

# Copiar o jar executavel
ADD gfinanceiro-service.jar /usr/share/gfinanceiro/gfinanceiro-service.jar
# Copiando a base de dados
ADD gfinanceiro.mv.db /gfinanceiro.mv.db

# Configurar o RUN default
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "/usr/share/gfinanceiro/gfinanceiro-service.jar"]

