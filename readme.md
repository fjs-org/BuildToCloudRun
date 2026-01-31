# Basic Documentation

### Endpoints
* http://localhost:8080/health
* http://localhost:8080/swagger-ui/index.html#/

### Setup GAR on GCP
* gcloud services enable artifactregistry.googleapis.com
* Create Artifact Repo via: https://console.cloud.google.com/artifacts/create-repo?project=projectfj - europe-west3-docker.pkg.dev/projectfj/fj-image-repo

# Setup "Workload Identity Federation":
* Create Identity Pool & Provider:
    * https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-deployment-pipelines?hl=en#gcloud
    * https://console.cloud.google.com/iam-admin/workload-identity-pools?orgonly=true&project=projectfj&supportedpurview=organizationId
* Create SA:
  * gcloud iam service-accounts create github-ci \
    --display-name="GitHub Actions CI"
  * Get SA permissions:
    gcloud projects add-iam-policy-binding projectfj \
    --member="serviceAccount:github-ci@projectfj.iam.gserviceaccount.com" \
    --role="roles/artifactregistry.writer"
  * Link the Service Account to the WIP
    * gcloud projects describe projectfj --format="value(projectNumber)"
    * gcloud iam service-accounts add-iam-policy-binding github-ci@projectfj.iam.gserviceaccount.com \
      --role="roles/iam.workloadIdentityUser" \
      --member="principalSet://iam.googleapis.com/projects/129285069564/locations/global/workloadIdentityPools/fj-identity-pool/attribute.repository/fjlenz/BuildToCloudRun"
* Set Repo Secrets in: https://github.com/fjlenz/BuildToCloudRun/settings/secrets/actions
  GCP_PROJECT_ID: projectfj
  GCP_SA_EMAIL: `github-ci@projectfj.iam.gserviceaccount.com`
  GCP_WORKLOAD_PROVIDER: projects/129285069564/locations/global/workloadIdentityPools/fj-identity-pool/providers/fj-provider


* see: https://medium.com/@hkayw95/how-to-securely-push-docker-images-from-github-actions-to-google-artifact-registry-0972ed554b64

