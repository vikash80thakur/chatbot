locals {
  network_name = "${var.project_name}-net"

  org_image  = "${var.dockerhub_repo}:organization-${var.image_tag}"
  chat_image = "${var.dockerhub_repo}:chatbot-${var.image_tag}"
  gw_image   = "${var.dockerhub_repo}:gateway-${var.image_tag}"
}

# -----------------------
# Docker network
# -----------------------
resource "docker_network" "app_net" {
  name = local.network_name
}

# -----------------------
# PostgreSQL volume
# -----------------------
resource "docker_volume" "postgres_data" {
  name = "${var.project_name}-postgres-data"
}

# -----------------------
# PostgreSQL container
# -----------------------
resource "docker_container" "postgres" {
  name  = "postgres"
  image = var.postgres_image

  restart = "unless-stopped"

  networks_advanced {
    name = docker_network.app_net.name
  }

  ports {
    internal = 5432
    external = var.postgres_host_port
  }

  env = [
    "POSTGRES_DB=${var.postgres_db}",
    "POSTGRES_USER=${var.postgres_user}",
    "POSTGRES_PASSWORD=${var.postgres_password}"
  ]

  mounts {
    target = "/var/lib/postgresql/data"
    source = docker_volume.postgres_data.name
    type   = "volume"
  }
}

# -----------------------
# Organization service
# -----------------------
resource "docker_container" "organization_service" {
  name  = "organization-service"
  image = local.org_image

  restart = "unless-stopped"

  depends_on = [docker_container.postgres]

  networks_advanced {
    name = docker_network.app_net.name
  }

  ports {
    internal = 8082
    external = 8082
  }

  env = [
    "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${var.postgres_db}",
    "SPRING_DATASOURCE_USERNAME=${var.postgres_user}",
    "SPRING_DATASOURCE_PASSWORD=${var.postgres_password}",
    # actuator health exposure (safe for local)
    "MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED=true",
    "MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info"
  ]
}

# -----------------------
# Chatbot service (stateless)
# -----------------------
resource "docker_container" "chatbot_service" {
  name  = "chatbot-service"
  image = local.chat_image

  restart = "unless-stopped"

  networks_advanced {
    name = docker_network.app_net.name
  }

  ports {
    internal = 8084
    external = 8084
  }

  # no DB env vars (keep it stateless for now)
}

# -----------------------
# API Gateway (Spring Cloud Gateway)
# Route config is passed via env vars (equivalent to application.properties)
# -----------------------
resource "docker_container" "api_gateway" {
  name  = "api-gateway"
  image = local.gw_image

  restart = "unless-stopped"

  depends_on = [docker_container.organization_service, docker_container.chatbot_service]

  networks_advanced {
    name = docker_network.app_net.name
  }

  ports {
    internal = 8080
    external = var.gateway_host_port
  }

  env = [
    "SERVER_PORT=8080",

    # Route 0 -> organization-service
    "SPRING_CLOUD_GATEWAY_ROUTES_0_ID=organization-service",
    "SPRING_CLOUD_GATEWAY_ROUTES_0_URI=http://organization-service:8082",
    "SPRING_CLOUD_GATEWAY_ROUTES_0_PREDICATES_0=Path=/org/**",
    "SPRING_CLOUD_GATEWAY_ROUTES_0_FILTERS_0=RewritePath=/org/(?<segment>.*), /$${segment}",

    # Route 1 -> chatbot-service
    "SPRING_CLOUD_GATEWAY_ROUTES_1_ID=chatbot-service",
    "SPRING_CLOUD_GATEWAY_ROUTES_1_URI=http://chatbot-service:8084",
    "SPRING_CLOUD_GATEWAY_ROUTES_1_PREDICATES_0=Path=/chat/**"
  ]
}