# Create the 'justgoup' namespace
resource "kubernetes_namespace_v1" "justgoup" {
  metadata {
    name = "justgoup"
  }
}

resource "kubernetes_persistent_volume_claim_v1" "justgoup" {
  metadata {
    name      = "justgoup-pvc"
    namespace = kubernetes_namespace_v1.justgoup.metadata[0].name
  }

  spec {
    access_modes = ["ReadWriteOnce"]

    resources {
      requests = {
        storage = "5Gi"
      }
    }
  }
}

module "kafka" {
  source    = "./kafka"
  namespace = var.namespace
  enabled   = var.kafka_enabled

  pvc_name = var.kafka_pvc_name
}
