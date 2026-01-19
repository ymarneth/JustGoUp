output "service_name" {
  value = kubernetes_manifest.ingestion_service.manifest.metadata.name
}
