# JustGoUp Infra - Developer Setup Guide

This document explains how to set up the development environment to work on the JustGoUp infrastructure code and deploy services locally using Minikube and OpenTofu (Tofu).


## 1. Necessary Tooling

| Tool                     | Purpose                                          | Installation                                                             |
| ------------------------ | ------------------------------------------------ | ------------------------------------------------------------------------ |
| **kubectl**              | Kubernetes CLI to interact with clusters         | [Install kubectl](https://kubernetes.io/docs/tasks/tools/)               |
| **minikube**             | Local Kubernetes cluster for development         | [Install minikube](https://minikube.sigs.k8s.io/docs/start/)             |
| **docker**               | Build and run container images                   | [Install Docker](https://docs.docker.com/get-docker/)                    |
| **OpenTofu (Tofu)**      | Infrastructure-as-code tool used for the project | [Install OpenTofu](https://opentofu.io/docs/getting-started)             |


## 2. Creating a Minikube Profile

We use Minikube to run a local cluster without affecting your global `~/.kube/config`. This isolates your environment and allows multiple cluster profiles.

### 1. Create a Minikube profile:

```sh
minikube start -p justgoup \
  --cpus=4 \
  --memory=8192 \
  --driver=docker
```

### 2. Verify the cluster is running:

```sh
kubectl --context=justgoup get nodes
```


### 3. Ensure a dedicated kubeconfig entry exists:

```sh
kubectl config get-contexts
# you should see something like:
# CURRENT   NAME        CLUSTER     AUTHINFO    NAMESPACE
# *         justgoup    minikube    minikube
```

## 3. Configuring Your .kube Directory

By default, Minikube modifies ~/.kube/config. To avoid conflicts with other clusters:

Export a separate KUBECONFIG file:

```sh
export KUBECONFIG="$HOME/.kube/justgoup-config"
```

Or on windows:
```sh
[System.Environment]::SetEnvironmentVariable(
    "KUBECONFIG",
    "$HOME\.kube\justgoup-config",
    "User"
)
```

Switch contexts to your Minikube profile:

```sh
kubectl config use-context justgoup
```

Verify you are using the correct context:

```sh
kubectl config current-context
# Output should be: justgoup
```

## 4. Deploying the Tofu Templates

OpenTofu (Tofu) is used to manage Kubernetes manifests and resources for this project.

### 1. Initialize the Tofu workspace:

```sh
tofu init
```

### 2. Plan the deployment:

```sh
tofu plan
```

This will show which resources would be created, updated, or destroyed.

### 3. Apply the templates:

```sh
tofu apply
```

⚠️ Notes:

Make sure your KUBECONFIG points to the correct cluster (justgoup Minikube profile).

For local development, Kafka and Knative services will be deployed into the justgoup namespace by default.

If you need to clean up resources, you can use tofu destroy to safely remove them.

## 5. Checking Resources

Once deployed, you can verify:

- Pods:
    ```sh
    kubectl get pods -n justgoup
    ```

- Services:

    ```sh
    kubectl get svc -n justgoup
    ```

- Knative services (if deployed):

    ```sh
    kubectl get ksvc -n justgoup
    ```

## 6. Notes for Development

Kafka is running locally in KRaft mode on port 9092 in Minikube.

Ingestion service can be tested locally via port forwarding:

```sh
kubectl port-forward svc/ingestion-service 8080:80 -n justgoup
curl http://localhost:8080
```

- All Tofu templates are in the infra/ directory.
- Use branches and commits responsibly when testing new resources.
