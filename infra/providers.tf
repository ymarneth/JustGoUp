provider "kubernetes" {
  config_path = pathexpand("~/.kube/justgoup-config")
  #config_context = "okd-context"
  config_context = "justgoup"
}

provider "helm" {
  kubernetes = {
    config_path = pathexpand("~/.kube/justgoup-config")
    #config_context = "okd-context"
    config_context = "justgoup"
  }
}
