resource "kubernetes_manifest" "ingestion_service" {
  manifest = {
    apiVersion = "serving.knative.dev/v1"
    kind       = "Service"
    metadata = {
      name      = "ingestion-service"
      namespace = var.namespace
    }
    spec = {
      template = {
        spec = {
          containers = [{
            image = "ghcr.io/justgoup/ingestion-go-kafka:latest"
            env = [
              {
                name  = "KAFKA_BOOTSTRAP_SERVERS"
                value = "kafka-demo:9092"
              },
              {
                name  = "KAFKA_TOPIC"
                value = "ingestion-topic"
              }
            ]
          }]
        }
      }
    }
  }
}
