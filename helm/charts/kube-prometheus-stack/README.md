# kube-prometheus-stack ServiceMonitor Templates

This subchart provides ServiceMonitor resources for monitoring your microservices with Prometheus, configured through the `serviceMonitor` values.

## Configuration

ServiceMonitors can be enabled/disabled through the parent chart's `values.yaml`:

```yaml
kube-prometheus-stack:
  serviceMonitor:
    enabled: true                    # Master toggle for all ServiceMonitors
    
    apiGateway:
      enabled: true                  # Enable/disable API Gateway monitoring
      interval: 30s                  # Scrape interval
      scrapeTimeout: 10s             # Scrape timeout
      
    chatbotService:
      enabled: true                  # Enable/disable Chatbot Service monitoring
      interval: 30s
      scrapeTimeout: 10s
      
    organizationService:
      enabled: true                  # Enable/disable Organization Service monitoring
      interval: 30s
      scrapeTimeout: 10s
      
    postgresql:
      enabled: true                  # Enable/disable PostgreSQL monitoring
      interval: 30s
      scrapeTimeout: 10s
      
    rabbitmq:
      enabled: true                  # Enable/disable RabbitMQ monitoring
      interval: 30s
      scrapeTimeout: 10s
```

## ServiceMonitors

The following ServiceMonitors are created when their respective services are enabled:

1. **API Gateway** - Monitors the API Gateway service on `/actuator/prometheus`
2. **Chatbot Service** - Monitors the Chatbot Service on `/actuator/prometheus`
3. **Organization Service** - Monitors the Organization Service on `/actuator/prometheus`
4. **PostgreSQL** - Monitors the PostgreSQL database metrics
5. **RabbitMQ** - Monitors the RabbitMQ message broker metrics

## Requirements

- kube-prometheus-stack must be installed (provides the Prometheus CRD for ServiceMonitor)
- Services must have `app.kubernetes.io/name` label matching the service name

## Example Service Labels

Services should have labels like:

```yaml
labels:
  app.kubernetes.io/name: api-gateway
  app.kubernetes.io/instance: chatbot-platform
```

## Monitoring Endpoints

Each ServiceMonitor targets a specific port and path:

- **Spring Boot Services** (API Gateway, Chatbot, Organization): Port `metrics`, Path `/actuator/prometheus`
- **PostgreSQL**: Port `metrics` (exposed by metrics sidecar)
- **RabbitMQ**: Port `metrics` (exposed by metrics plugin)

## Disabling Monitoring

To disable monitoring for specific services:

```yaml
kube-prometheus-stack:
  serviceMonitor:
    enabled: false                   # Disable all ServiceMonitors
```

Or individually:

```yaml
kube-prometheus-stack:
  serviceMonitor:
    enabled: true
    apiGateway:
      enabled: false                 # Disable only API Gateway monitoring
```

## Prometheus Scrape Configuration

The ServiceMonitor CRD is automatically discovered by Prometheus configured with:

```yaml
serviceMonitorSelectorLabels: {}
```

This allows Prometheus to discover all ServiceMonitors in the cluster.
