variable "project_name" {
  type    = string
  default = "chatbot-local"
}

variable "dockerhub_repo" {
  type    = string
  default = "girishhardia/chatbot"
}

# This is the Jenkins-generated tag PART, e.g. 45-2e3d3400
variable "image_tag" {
  type        = string
  description = "Jenkins tag suffix (BUILD-GITSHA). Example: 45-2e3d3400"
}

variable "postgres_image" {
  type    = string
  default = "postgres:15"
}

variable "postgres_db" {
  type    = string
  default = "organizationdb"
}

variable "postgres_user" {
  type    = string
  default = "orguser"
}

variable "postgres_password" {
  type        = string
  sensitive   = true
  description = "Postgres password (do not commit real password)."
}

# Host ports (change if already used)
variable "gateway_host_port" {
  type    = number
  default = 8080
}

variable "postgres_host_port" {
  type    = number
  default = 5432
}