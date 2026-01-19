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
