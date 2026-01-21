output "mongodb_service_host" {
  value = "${helm_release.mongodb.name}.${var.namespace}.svc.cluster.local"
}

output "mongodb_database" {
  value = var.mongodb_app_db
}

output "mongodb_app_user" {
  value = var.mongodb_app_user
}
