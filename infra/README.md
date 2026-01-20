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
minikube start -p justgoup --driver=docker
```

### 2. Create the `justgoup` namespace:

```sh
kubectl --context=justgoup create namespace justgoup
kubectl config set-context justgoup --namespace=justgoup
```

### 3. Verify the cluster is running:

```sh
kubectl --context=justgoup get nodes
```

### 4. Ensure a dedicated kubeconfig entry exists:

```sh
kubectl config get-contexts
# you should see something like:
# CURRENT   NAME        CLUSTER     AUTHINFO    NAMESPACE
# *         justgoup    justgoup    justgoup    justgoup
```

## 3. Configuring Your `.kube` Directory

By default, Minikube modifies `~/.kube/config`. To avoid conflicts with other clustersTo avoid conflicts with other clusters, we export the Minikube context into a dedicated kubeconfig file.


### 1. Create / verify the Minikube profile

```sh
minikube profile list
kubectl config get-contexts
```

For both you should see the `justgoup` profile.

### 1. Export the Minikube context

```sh
kubectl config view \
  --context=justgoup \
  --minify \
  --flatten \
  > "$HOME/.kube/justgoup-config"
```

```sh
export KUBECONFIG="$HOME/.kube/justgoup-config"
```

Or on windows:

```ps
kubectl config view `
  --context=justgoup `
  --minify `
  --flatten `
  > $HOME\.kube\justgoup-config
```

```ps
[System.Environment]::SetEnvironmentVariable(
    "KUBECONFIG",
    "$HOME\.kube\justgoup-config",
    "User"
)
```

Switch contexts to your Minikube profile:

```sh
kubectl config use-context justgoup
kubectl config current-context
# Output should be: justgoup
```

## 4. Preparing cluster Resources (Minikube)

### 1. Set up Knative

Knative requires an ingress controller. For Minikube we use ingress-nginx.

```sh
minikube addons enable ingress -p justgoup
minikube addons enable ingress-dns -p justgoup
```

Install the Knative operator:

```sh
kubectl apply -f https://github.com/knative/operator/releases/download/knative-v1.20.1/operator.yaml
```

Install Knative Serving:

```sh
kubectl create namespace knative-serving
kubectl apply -f ./minikube/knative-serving.yaml

# Verify Knative Serving components:
kubectl get pods -n knative-serving
```

Expected running pods include:
- controller
- autoscaler
- activator
- webhook

### 2. Create a Persistent Volume Claim

Kafka and NoqSQL need a PVC. For demo purposes they will share one:

```sh
kubectl apply -f ./minikube/pvc.yaml
```

## 5. Deploying the Tofu Templates

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

### 4. Verify the deployment

```sh
kubectl get pods -n justgoup
```

⚠️ Notes:

Make sure your KUBECONFIG points to the correct cluster (justgoup Minikube profile).

For local development, Kafka and Knative services will be deployed into the justgoup namespace by default.

If you need to clean up resources, you can use tofu destroy to safely remove them.

## 6. Checking Resources

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

## 7. Notes for Development

Kafka is running locally in KRaft mode on port 9092 in Minikube.

Ingestion service can be tested locally via port forwarding:

```sh
kubectl port-forward svc/ingestion-service 8080:80 -n justgoup
curl http://localhost:8080
```

- All Tofu templates are in the infra/ directory.
- Use branches and commits responsibly when testing new resources.
