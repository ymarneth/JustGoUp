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

resource "kubernetes_job_v1" "kafka_topics" {
  metadata {
    name      = "kafka-create-topics"
    namespace = var.namespace
  }

  spec {
    template {
      metadata { name = "kafka-create-topics" }
      spec {
        restart_policy = "Never"

        init_container {
          name  = "wait-for-kafka"
          image = "busybox"
          command = [
            "sh",
            "-c",
            "until nc -z kafka-demo.justgoup.svc.cluster.local 9092; do echo waiting for kafka; sleep 2; done"
          ]
        }

        container {
          name  = "kafka-cli"
          image = "apache/kafka:4.1.1"

          command = [
            "/opt/kafka/bin/kafka-topics.sh",
            "--create",
            "--bootstrap-server",
            "kafka-demo.justgoup.svc.cluster.local:9092",
            "--replication-factor",
            "1",
            "--partitions",
            "1",
            "--topic",
            "ingestion-topic",
            "--if-not-exists"
          ]
        }
      }
    }

    backoff_limit = 0
  }
}
