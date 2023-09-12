package com.jdoodle.docker.rest;

import com.jdoodle.docker.shell.ShellUtil;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DockerController {
    @Autowired
    ShellUtil shellUtil;


    @GetMapping("/docker")
    String docker() {
        return shellUtil.executeCommand("docker images");
    }

    @GetMapping("/dockerps")
    String dockerps() {
        return shellUtil.executeCommand("docker ps");
    }

    @GetMapping("/create-start-docker-nginx")
    String startDocker() {
        String startDocker = "";
        String[] command = {"docker", "start", "nginx-container-final"};
        startDocker = shellUtil.processBuilder(command);
        if (startDocker.isEmpty()) {
            String[] commandRun = {"docker", "run", "-d", "--name", "nginx-container-final", "-p", "8083:80", "my-nginx:latest"};
            startDocker = shellUtil.processBuilder(commandRun);
        }
        return startDocker;
    }

    @GetMapping("/stop-docker-nginx")
    String stopDocker() {
        String[] command = {"docker", "stop", "nginx-container-final"};
        return shellUtil.processBuilder(command);
    }

    @GetMapping("/get-page-docker")
    String getPageDocker() {
        String[] command = {"curl", "http://localhost:8083"};
        return shellUtil.processBuilder(command);
    }

    @PostMapping("/create-start-docker-k8s")
    public String startKubernetesPod() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            Pod pod = new PodBuilder()
                    .withNewMetadata()
                    .withName("kubernetes-pod")
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("nginx-container-final")
                    .withImage("my-nginx:latest")
                    .endContainer()
                    .endSpec()
                    .build();

            client.pods().create(pod);
            return "Pod started successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error starting pod: " + e.getMessage();
        }
    }

    @DeleteMapping("/stop-docker-k8s")
    public String stopKubernetesPod(@PathVariable String podName) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            client.pods().inNamespace("default").withName("kubernetes-pod").delete();
            return "Pod " + podName + " stopped successfully.";
        } catch (ResourceNotFoundException e) {
            return "Pod " + podName + " not found.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error stopping pod: " + e.getMessage();
        }
    }

    @GetMapping("/get-page-k8s")
    String getPageKubernetes() {
        String[] command = {"curl", "http://localhost:8083"};
        return shellUtil.processBuilder(command);
    }

}
