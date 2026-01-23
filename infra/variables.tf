variable "namespace" {
  description = "Kubernetes namespace to deploy into"
  type        = string
  default     = "justgoup"
}

variable "kafka_pvc_name" {
  description = "PVC name provided by the cluster admin"
  type        = string
  default     = "justgoup-pvc"
}

variable "kafka_enabled" {
  description = "Whether to deploy Kafka and Zookeeper"
  type        = bool
  default     = true
}

variable "mongodb_storage_class_name" {
  type    = string
  default = null
}

variable "mongodb_persistence_size" {
  type    = string
  default = "5Gi"
}

variable "mongodb_root_password" {
  type      = string
  sensitive = true
}

variable "mongodb_app_password" {
  type      = string
  sensitive = true
}
