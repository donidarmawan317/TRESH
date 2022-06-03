curl \
-X POST \
-H "Authorization: Bearer $(gcloud auth print-access-token)" \
-H "Content-Type: application/json" \
https://asia-southeast1-aiplatform.googleapis.com/v1/projects/${PROJECT_ID}/locations/asia-southeast1/endpoints/${ENDPOINT_ID}:predict \
-d "@${INPUT_DATA_FILE}"