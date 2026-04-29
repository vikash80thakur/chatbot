{{- define "org.name" -}}organization-service{{- end -}}
{{- define "org.fullname" -}}{{ printf "%s-%s" .Release.Name (include "org.name" .) }}{{- end -}}