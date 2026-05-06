terraform {
  required_version = ">= 1.5.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"

  # Use your existing docker login credentials (recommended for private Docker Hub repo)
  registry_auth {
    address     = "https://index.docker.io/v1/"
    config_file = pathexpand("~/.docker/config.json")
  }
}