variable "namespace" {
  type    = string
  default = "justgoup"
}

variable "release_name" {
  type    = string
  default = "mongodb"
}

variable "storage_class_name" {
  type    = string
  default = null
  # For OKD/your cluster, set this to whatever StorageClass you want (e.g. "ocs-storagecluster-ceph-rbd", "standard", etc.)
}

variable "persistence_size" {
  type    = string
  default = "5Gi"
}

variable "mongodb_root_user" {
  type    = string
  default = "root"
}

variable "mongodb_root_password" {
  type      = string
  sensitive = true
}

variable "mongodb_app_db" {
  type    = string
  default = "justgoup"
}

variable "mongodb_app_user" {
  type    = string
  default = "app"
}

variable "mongodb_app_password" {
  type      = string
  sensitive = true
}
