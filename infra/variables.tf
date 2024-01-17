variable "region" {
  type    = string
  default = "us-east-2"
}

variable "infra_name" {
  type    = string
  default = "mikes"
}

variable "name" {
  type    = string
  default = "mikes-producao"
}

variable "db_name" {
  type    = string
  default = "producao"
}

variable "sns_name_status_producao_alterado" {
  type    = string
  default = "status_producao_alterado"
}

variable "sqs_name_sqs_iniciar_producao" {
  type    = string
  default = "iniciar_producao"
}

variable "sqs_name_sqs_producao_pedido" {
  type    = string
  default = "producao-pedido"
}

variable "subnets" {
  type    = list(string)
  default = [
    "subnet-0c9e1d22c842d362b",
    "subnet-08e43d2d7fa2c463e"
  ]
}
