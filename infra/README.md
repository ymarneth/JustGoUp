# JustGoUp Infra - Developer Setup Guide

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

### 2. Verify the cluster is running:

```sh
kubectl --context=justgoup get nodes
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

Install the Knative operator:

```sh
kubectl apply -f https://github.com/knative/operator/releases/download/knative-v1.20.1/operator.yaml
```

Install Knative Serving:

```sh
kubectl apply -f ./minikube/knative-serving.yaml

# Verify Knative Serving components:
kubectl get pods -n knative-serving
```

Expected running pods include:
- controller
- autoscaler
- activator
- webhook

Install Knative Eventing:

```sh
kubectl apply -f ./minikube/knative-eventing.yaml

# Verify Knative Eventing components:
kubectl get pods -n knative-eventing
```

## 5. Get Mongo Express running
MongoDB is deployed inside the Minikube cluster via OpenTofu.
Mongo Express is used as a development UI to inspect MongoDB data.

Due to networking limitations with Minikube (Docker driver) on Windows, Mongo Express is accessed using a combination of Kubernetes port-forwarding and a local Docker container.

### 5.1 Prerequisites

Make sure the following are true:
- Minikube profile justgoup is running
- MongoDB and mongo-express deployment have been created via `tofu apply`
- kubectl is using the justgoup context

Verify:
```sh 
minikube status -p justgoup
kubectl config current-context
kubectl get pods -n justgoup | findstr mongo
```

You should see:
- a running MongoDB pod
- a running mongo-express pod

### 5.2 Port-forward Mongo Express
Open one terminal and keep it open:
```sh
kubectl port-forward -n justgoup deploy/mongo-express 8081:8081 --address 127.0.0.1
```

This forwards the Mongo Express service from the cluster to your local machine.

### 5.3 Start Mongo Express via Docker
Open a second terminal and run:
```sh
docker run --rm -p 8081:8081 `
  -e ME_CONFIG_MONGODB_URL="mongodb://<ROOT_USER>:<ROOT_PASSWORD>@host.docker.internal:27017/admin?authSource=admin" `
  -e ME_CONFIG_BASICAUTH="false" `
  mongo-express:1.0.2
```

Notes:
- <ROOT_USER> and <ROOT_PASSWORD> are the MongoDB root credentials configured in OpenTofu
- host.docker.internal allows the Docker container to reach MongoDB via the host network on Windows
- Root credentials are required because Mongo Express executes admin commands (e.g. serverStatus)

### 5.4 Open Mongo Express in the browser
Open your browser and navigate to http://127.0.0.1:8081.

You should now see:
- the Mongo Express UI
- the justgoup database
- collections created by seed jobs or Kafka consumers

## 6. Deploying the Tofu Templates

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

## 7. Checking Resources

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

## 8. Notes for Development

Kafka is running locally in KRaft mode on port 9092 in Minikube.

Ingestion service can be tested locally via port forwarding:

```sh
kubectl port-forward svc/ingestion-service 8080:80 -n justgoup
curl http://localhost:8080
```

- All Tofu templates are in the infra/ directory.
- Use branches and commits responsibly when testing new resources.
