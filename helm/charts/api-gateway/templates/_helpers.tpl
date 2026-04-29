{{- define "gw.name" -}}api-gateway{{- end -}}
{{- define "gw.fullname" -}}{{ printf "%s-%s" .Release.Name (include "gw.name" .) }}{{- end -}}