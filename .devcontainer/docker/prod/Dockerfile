FROM eclipse-temurin:26.0.2.1_1-jre@sha256:0dbf52b3861a84e2a19c503245504e5a71a5f071ccb9c48997efd4a3454a52b7

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
