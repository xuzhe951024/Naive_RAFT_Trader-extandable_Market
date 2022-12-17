package com.zhexu.cs677_lab3.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class DockerClientService {
    /**
     * connect to docker server
     *
     * @return
     */

    public DockerClient connectDocker(String dockerInstance) {
        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerInstance).build();
        dockerClient.infoCmd().exec();
        return dockerClient;

    }

    /**
     * create container
     *
     * @param client
     * @return
     */

    public CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName) {
        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
        return container;

    }

    /**
     * start container
     *
     * @param client
     * @param containerId
     */

    public void startContainer(DockerClient client, String containerId) {
        client.startContainerCmd(containerId).exec();
    }

    /**
     * close container
     *
     * @param client
     * @param containerId
     */

    public void stopContainer(DockerClient client, String containerId) {
        client.stopContainerCmd(containerId).exec();
    }

    /**
     * delete container
     *
     * @param client
     * @param containerId
     */

    public void removeContainer(DockerClient client, String containerId) {
        client.removeContainerCmd(containerId).exec();
    }
}
