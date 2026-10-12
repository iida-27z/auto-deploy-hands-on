FROM eclipse-temurin:25.0.5.1_2-jre@sha256:4ed132f6832e9804b123e89a849a7b4502756ae64a4ee08ebab939d9d5d0a9fa

WORKDIR /app

RUN apt update && apt install -y openssh-server && \
    apt clean && \
    rm -rf /var/lib/apt/lists/*

ARG GROUP_NAME=deployer
ARG USER_NAME=deployer
RUN groupadd -r ${GROUP_NAME} && useradd -r -g ${GROUP_NAME} -m -s /bin/bash ${USER_NAME} \
    && mkdir -p /home/${USER_NAME}/.ssh /var/run/sshd \
    && chown -R ${USER_NAME}:${GROUP_NAME} /home/${USER_NAME}

RUN chown -R ${USER_NAME}:${GROUP_NAME} /app
