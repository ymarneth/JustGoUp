# Project Documentation
## 1. Automated Infrastructure Provisioning
Automated Infrastructure Provisioning is a core aspect of the JustGoUp project.
All infrastructure components are defined declaratively using OpenTofu (Infrastructure as Code).

This includes:
- Kubernetes namespaces
- Deployments (Kafka, MongoDB, Kafka Connect, application services)
- Services and Jobs
- Persistent storage (PVCs)
- Supporting components such as Mongo Express

Using OpenTofu ensures that the infrastructure can be recreated reliably on any machine, manual setup steps are minimized, configuration changes are version-controlled, and the local development environment closely resembles the later OKD deployment. The infrastructure can be provisioned or removed using a single command:
```sh
tofu apply
tofu destroy
```

This approach significantly reduces setup errors and improves reproducibility.

## 2. Scalability
Scalability is considered at both the application and infrastructure level.

From an infrastructure perspective:
- All services are deployed as Kubernetes workloads and can be scaled horizontally
- Kafka enables decoupled, asynchronous communication between producers and consumers
- New consumers can be added without modifying existing services

From an architectural perspective:
- Event-based communication via Kafka avoids tight coupling
- MongoDB supports horizontal scaling and flexible data models
- Stateless services (e.g. ingestion services) can be replicated easily

While the local Minikube setup uses single replicas for simplicity, the architecture is designed so that scaling up in a real cluster (e.g. OKD) only requires configuration changes, not architectural changes.

## 3. Fault Tolerance
Fault tolerance is addressed through both platform mechanisms and architectural decisions.

Kubernetes provides:
- automatic pod restarts on failure
- health monitoring
- self-healing behavior for crashed containers

Kafka contributes to fault tolerance by:
- persisting events independently of consumers
- allowing consumers to recover and reprocess messages
- decoupling producers from downstream systems

MongoDB persistence is handled via PersistentVolumeClaims, ensuring that:
- data survives pod restarts
- temporary failures do not result in data loss

This combination ensures that transient failures do not break the overall system.

## 4. NoSql
NoSQL plays a central role in the JustGoUp project. The application produces event-oriented, hierarchical data, such as climbing sessions, boulder attempts, or user-specific metadata.

MongoDB was chosen because:
- it supports document-based storage, which maps naturally to sessions and events
- the schema can evolve without costly migrations
- nested data structures can be stored efficiently
- write-heavy workloads are handled well

Compared to a relational database, MongoDB reduces complexity for storing event data and enables flexible experimentation during development.

## 5. Replication 
Replication is used implicitly through the chosen technologies and can be extended further in production environments.

In the current setup:
- Kafka supports topic replication (configured minimally for local development)
- MongoDB is deployed as a standalone instance locally but supports replica sets in production
- Kubernetes allows multiple replicas of stateless services

Although the local Minikube setup uses single-node configurations for simplicity, the architecture is replication-ready and can be extended without redesign.

## 6. Costs
The solution is designed to minimize costs during development while remaining cloud-ready.

For local development:
- Minikube runs entirely on the developer’s machine
- No cloud resources are consumed
- Costs are effectively zero

In a cloud or OKD environment:
- Costs scale with actual resource usage
- Services can be scaled independently
- Managed Kubernetes platforms reduce operational overhead

Compared to a traditional non-cloud solution:
- no dedicated hardware is required
- scaling does not require upfront investment
- infrastructure can be adjusted dynamically

Overall, the cloud-native approach provides a strong cost advantage, especially for early-stage development and experimentation.

# JustGoUp Infra - Developer Setup Guide
This part explains how to set up the development environment to work on the JustGoUp infrastructure code and deploy services locally using Minikube and OpenTofu (Tofu).

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