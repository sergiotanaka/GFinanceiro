# Dockerfile para criar imagem do serviço do gfinanceiro

FROM openjdk
MAINTAINER Pinguin Corp

# Copiar o jar executavel
ADD gfinanceiro-service.jar /usr/share/gfinanceiro/gfinanceiro-service.jar

# Configurar o RUN default
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "/usr/share/gfinanceiro/gfinanceiro-service.jar"]