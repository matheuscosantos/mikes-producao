terraform {
  backend "s3" {
    bucket = "mikes-terraform-state"
    key    = "mikes_producao.tfstate"
    region = "us-east-2"
    encrypt = true
  }
}
