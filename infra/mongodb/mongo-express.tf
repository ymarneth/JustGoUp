resource "kubernetes_deployment_v1" "mongo_express" {
  metadata {
    name      = "mongo-express"
    namespace = var.namespace
    labels    = { app = "mongo-express" }
  }

  spec {
    replicas = 1

    selector {
      match_labels = { app = "mongo-express" }
    }

    template {
      metadata {
        labels = { app = "mongo-express" }
      }

      spec {
        container {
          name  = "mongo-express"
          image = "mongo-express:1.0.2"

          port {
            container_port = 8081
          }

          # Connect to your MongoDB service in-cluster (you confirmed svc "mongodb")
          env {
            name  = "ME_CONFIG_MONGODB_URL"
            value = "mongodb://${var.mongodb_app_user}:${var.mongodb_app_password}@mongodb:ö/${var.mongodb_app_db}?authSource=${var.mongodb_app_db}"
            }

            env {
                name = "ME_CONFIG_BASICAUTH"
                value = "false"
            }
        }
      }
    }
  }

  depends_on = [helm_release.mongodb]
}

resource "kubernetes_service_v1" "mongo_express" {
  metadata {
    name      = "mongo-express"
    namespace = var.namespace
  }

  spec {
    selector = { app = "mongo-express" }

    port {
      name        = "http"
      port        = 8081
      target_port = 8081
      protocol    = "TCP"
    }

    type = "NodePort"
  }
}
