#!/bin/bash
awslocal sqs create-queue  --queue-name local-queue --attributes VisibilityTimeout=30,MessageRetentionPeriod=345600
awslocal --region sa-east-1 sqs create-queue  --queue-name local-queue2 --attributes VisibilityTimeout=30,MessageRetentionPeriod=345600
awslocal --region sa-east-1 sns create-topic --name local-topic
awslocal sns subscribe --topic-arn arn:aws:sns:sa-east-1:000000000000:local-topic --protocol sqs --notification-endpoint arn:aws:sqs:sa-east-1:000000000000:local-queue2 --attributes RawMessageDelivery=true