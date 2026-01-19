resource "kubernetes_deployment_v1" "kafka" {
  metadata {
    name      = "kafka-demo"
    namespace = var.namespace
    labels    = { app = "kafka-demo" }
  }

  spec {
    replicas = 1

    selector {
      match_labels = { app = "kafka-demo" }
    }

    template {
      metadata {
        labels = { app = "kafka-demo" }
      }

      spec {
        container {
          name  = "kafka"
          image = "apache/kafka:4.1.1"

          # === REQUIRED FOR KRaft ===
          env {
            name  = "CLUSTER_ID"
            value = "d4f0e6b5-0f4e-4d7e-9e47-4d3c91d9a111"
          }

          env {
            name  = "KAFKA_PROCESS_ROLES"
            value = "broker,controller"
          }

          env {
            name  = "KAFKA_NODE_ID"
            value = "1"
          }

          env {
            name  = "KAFKA_CONTROLLER_QUORUM_VOTERS"
            value = "1@kafka-demo:9093"
          }

          env {
            name  = "KAFKA_LISTENERS"
            value = "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093"
          }

          env {
            name  = "KAFKA_ADVERTISED_LISTENERS"
            value = "PLAINTEXT://kafka-demo:9092"
          }

          env {
            name  = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP"
            value = "PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT"
          }

          env {
            name  = "KAFKA_CONTROLLER_LISTENER_NAMES"
            value = "CONTROLLER"
          }

          env {
            name  = "KAFKA_LOG_DIRS"
            value = "/data/kafka"
          }

          port {
            container_port = 9092
          }

          port {
            container_port = 9093
          }

          volume_mount {
            name       = "shared-data"
            mount_path = "/data"
          }
        }

        volume {
          name = "shared-data"
          persistent_volume_claim {
            claim_name = var.pvc_name
          }
        }
      }
    }
  }
}

resource "kubernetes_service_v1" "kafka" {
  metadata {
    name      = "kafka-demo"
    namespace = var.namespace
    labels    = { app = "kafka-demo" }
  }

  spec {
    selector = { app = "kafka-demo" }

    port {
      name        = "broker"
      port        = 9092
      target_port = 9092
      protocol    = "TCP"
    }

    port {
      name        = "controller"
      port        = 9093
      target_port = 9093
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }
}
