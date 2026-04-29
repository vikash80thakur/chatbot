{{- define "chat.name" -}}chatbot-service{{- end -}}
{{- define "chat.fullname" -}}{{ printf "%s-%s" .Release.Name (include "chat.name" .) }}{{- end -}}