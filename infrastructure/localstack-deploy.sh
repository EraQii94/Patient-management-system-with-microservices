#!/bin/bash
# This script deploys AWS resources to LocalStack for local development and testing.

set -e # Exit immediately if a command exits with a non-zero status.

# Deploy the CloudFormation stack to LocalStack
aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name patient-management \
    --template-file "./cdk.out/LocalStack.template.json" \

aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query 'LoadBalancers[0].DNSName' --output text