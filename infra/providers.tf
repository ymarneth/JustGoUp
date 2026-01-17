provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "okd-context"
}

provider "helm" {
  kubernetes {
    config_path    = "~/.kube/config"
    config_context = "okd-context"
  }
}
