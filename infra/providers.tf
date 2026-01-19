provider "kubernetes" {
  config_path = "~/.kube/config"
  #config_context = "okd-context"
  config_context = "minikube"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
    #config_context = "okd-context"
    config_context = "minikube"
  }
}
