provider "aws" {
  region = var.region
}

# -- iam

resource "aws_iam_role" "ecs_execution_role" {
  name               = "${var.name}_ecs_execution_role"
  assume_role_policy = file("iam/role/ecs_execution_role.json")
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_ecr_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/EC2InstanceProfileForImageBuilderECRContainerBuilds"
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_cloudwatch_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess"
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_sns_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSNSFullAccess"
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_sqs_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSQSFullAccess"
}

# -- topics

resource "aws_sns_topic" "sns_topic_status_producao_alterado" {
  name = var.sns_name_status_producao_alterado
}

# -- queues

resource "aws_sqs_queue" "sqs_iniciar_producao" {
  name                       = var.sqs_name_sqs_iniciar_producao
  delay_seconds              = 0
  max_message_size           = 262144
  message_retention_seconds  = 259200
  visibility_timeout_seconds = 30

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.sqs_iniciar_producao_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue" "sqs_iniciar_producao_dlq" {
  name                       = "${var.sqs_name_sqs_iniciar_producao}-dlq"
  delay_seconds              = 0
  max_message_size           = 262144
  message_retention_seconds  = 1209600
  visibility_timeout_seconds = 30
}

resource "aws_sqs_queue_policy" "sqs_iniciar_producao_policy" {
  queue_url = aws_sqs_queue.sqs_iniciar_producao.id

  policy = templatefile("iam/policy/sqs_queue_policy.json", {
    QUEUE_ARN = aws_sqs_queue.sqs_iniciar_producao.arn
  })
}

# -- subscribes

data "aws_sqs_queue" "sqs_queue_producao_pedido" {
  name = var.sqs_name_sqs_producao_pedido
}

data "aws_sns_topic" "sns_topic_pedido_confirmado" {
  name = var.sns_name_pedido_confirmado
}

resource "aws_sns_topic_subscription" "sqs_producao_pedido_subscription_sns_status_producao" {
  topic_arn            = aws_sns_topic.sns_topic_status_producao_alterado.arn
  protocol             = "sqs"
  endpoint             = data.aws_sqs_queue.sqs_queue_producao_pedido.arn
  raw_message_delivery = true
}

resource "aws_sns_topic_subscription" "sqs_iniciar_producao_subscription_sns_pedido_confirmado" {
  topic_arn            = data.aws_sns_topic.sns_topic_pedido_confirmado.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.sqs_iniciar_producao.arn
  raw_message_delivery = true
}

# -- task definition

data "aws_db_instance" "db_instance" {
  db_instance_identifier = "mikes-db"
}

data "aws_secretsmanager_secret" "db_credentials" {
  name = "mikes/db/db_credentials"
}

data "aws_secretsmanager_secret_version" "db_credentials_current" {
  secret_id = data.aws_secretsmanager_secret.db_credentials.id
}

locals {
  db_credentials = jsondecode(data.aws_secretsmanager_secret_version.db_credentials_current.secret_string)
}

resource "aws_cloudwatch_log_group" "ecs_log_group" {
  name = "/ecs/${var.name}"
}

resource "aws_ecs_task_definition" "ecs_task_definition" {
  family             = var.name
  network_mode       = "awsvpc"
  execution_role_arn = aws_iam_role.ecs_execution_role.arn

  container_definitions = templatefile("container/definitions/mikes_app_container_definitions.json", {
    NAME                              = "${var.name}-container"
    DB_HOST                           = data.aws_db_instance.db_instance.address
    DB_PORT                           = data.aws_db_instance.db_instance.port
    DB_NAME                           = var.db_name
    DB_USER                           = local.db_credentials["username"]
    DB_PASSWORD                       = local.db_credentials["password"]
    SQS_INIT_PRODUCTION_URL           = aws_sqs_queue.sqs_iniciar_producao.arn
    SNS_PRODUCTION_STATUS_CHANGED_ARN = aws_sns_topic.sns_topic_status_producao_alterado.arn
    REGION                            = var.region
    LOG_GROUP_NAME                    = aws_cloudwatch_log_group.ecs_log_group.name
  })
}

# -- service

data "aws_ecs_cluster" "ecs_cluster" {
  cluster_name = "${var.infra_name}_cluster"
}

data "aws_security_group" "security_group" {
  name = "${var.infra_name}_security_group"
}

data "aws_vpc" "vpc" {
  id = "vpc-0ffc09ae69916058b"
}

data "aws_lb" "ecs_alb" {
  name = "mikes-ecs-alb"
}

resource "aws_lb_target_group" "lb_target_group_producao" {
  name     = "${var.name}-lb-tg-producao"
  port     = 8085
  protocol = "HTTP"
  target_type = "ip"
  vpc_id   = data.aws_vpc.vpc.id
}

resource "aws_lb_listener" "lb_listener" {
  load_balancer_arn = data.aws_lb.ecs_alb.arn
  port              = 8085
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.lb_target_group_producao.arn
  }
}

resource "aws_ecs_service" "ecs_service" {
  name            = "${var.name}_service"
  cluster         = data.aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.ecs_task_definition.arn
  desired_count   = 1 // minimo possivel p/ economizar resources

  network_configuration {
    subnets         = var.subnets
    security_groups = [data.aws_security_group.security_group.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.lb_target_group_producao.arn
    container_name   = "${var.name}-container"
    container_port   = 8085
  }

  force_new_deployment = true

  placement_constraints {
    type = "distinctInstance"
  }

  capacity_provider_strategy {
    capacity_provider = "${var.infra_name}_capacity_provider"
    weight            = 100
  }
}
