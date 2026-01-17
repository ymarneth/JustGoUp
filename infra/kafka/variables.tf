variable "namespace" {
  description = "Kubernetes namespace for Kafka resources"
  type        = string
}

variable "enabled" {
  description = "Whether to deploy Kafka and Zookeeper"
  type        = bool
  default     = true
}

variable "pvc_name" {
  description = "Name of the shared PersistentVolumeClaim for Kafka and Zookeeper"
  type        = string
}
