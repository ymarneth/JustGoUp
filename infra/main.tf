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

module "mongodb" {
  source = "./mongodb"

  namespace          = var.namespace
  storage_class_name = var.mongodb_storage_class_name
  persistence_size   = var.mongodb_persistence_size

  mongodb_root_password = var.mongodb_root_password
  mongodb_app_password  = var.mongodb_app_password
}
