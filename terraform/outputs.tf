output "gateway_url" {
  value = "http://localhost:${var.gateway_host_port}"
}

output "postgres_port" {
  value = var.postgres_host_port
}

output "images" {
  value = {
    organization = local.org_image
    chatbot      = local.chat_image
    gateway      = local.gw_image
  }
}