module "knative" {
  source    = "./knative"
  namespace = var.namespace
}

module "kafka" {
  source    = "./kafka"
  namespace = var.namespace
  enabled   = var.kafka_enabled

  pvc_name = var.kafka_pvc_name
}
