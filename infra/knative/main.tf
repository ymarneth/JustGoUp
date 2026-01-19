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
            image = "gcr.io/knative-samples/helloworld-go"
            env = [{
              name  = "TARGET"
              value = "JustGoUp"
            }]
          }]
        }
      }
    }
  }
}
