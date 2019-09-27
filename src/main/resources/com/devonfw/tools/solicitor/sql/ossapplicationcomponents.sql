-- SPDX-License-Identifier: Apache-2.0
--
select 
	GROUP_CONCAT(DISTINCT a."applicationName" ORDER BY "applicationName" DESC SEPARATOR ', ') as APPS, 
	GROUP_CONCAT(DISTINCT ac."version" ORDER BY "version" DESC SEPARATOR ', ') as "version" , 
	ac."groupId", 
	ac."artifactId", 
	l."effectiveNormalizedLicense", 
	l."effectiveNormalizedLicenseUrl", 
	l."effectiveNormalizedLicenseContent",
	UCASE(REGEXP_REPLACE(l."effectiveNormalizedLicenseContent",'\s','')) as "unifiedEffectiveNormalizedLicenseContent"
from 
	APPLICATION a, 
	APPLICATIONCOMPONENT ac, 
	NORMALIZEDLICENSE l 
where
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND 
	l."effectiveNormalizedLicenseType" like 'OSS-%' 
group by 
	"groupId", 
	"artifactId", 
	"effectiveNormalizedLicense", 
	"effectiveNormalizedLicenseUrl", 
	"effectiveNormalizedLicenseContent",
	"unifiedEffectiveNormalizedLicenseContent" 
order by 
	"groupId", 
	"artifactId", 
	"version", 
	"effectiveNormalizedLicense"