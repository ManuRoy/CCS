# Terraform — Azure foundation

This folder is intentionally a safe starter rather than a one-command AKS deployment.
Azure resources can incur charges. Check your Azure for Students balance, region quotas,
and pricing before creating resources. Never commit credentials or Terraform state.

Suggested next step: add a `main.tf` using the official AzureRM provider for a resource
group, Azure Container Registry, and AKS, then configure remote state in a secured backend.
Keep the database password in Key Vault or Kubernetes Secrets, not in Terraform source.
