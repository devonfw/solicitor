-- SPDX-License-Identifier: Apache-2.0
--
select 
	GROUP_CONCAT(DISTINCT a."applicationName" ORDER BY "applicationName" DESC SEPARATOR ', ') as APPS, 
	GROUP_CONCAT(DISTINCT ac."version" ORDER BY "version" DESC SEPARATOR ', ') as "version" , 
	ac."groupId", 
	ac."artifactId",
	ac."packageUrl",
	l."effectiveNormalizedLicense", 
	l."guessedLicenseUrl", 
	l."guessedLicenseContent",
	UCASE(REGEXP_REPLACE(l."guessedLicenseContent",'\s','')) as "unifiedGuessedLicenseContent"
from 
	APPLICATION a, 
	APPLICATIONCOMPONENT ac, 
	NORMALIZEDLICENSE l 
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND 
	l."effectiveNormalizedLicenseType" like 'OSS-%' 
group by 
	"groupId", 
	"artifactId",
	"packageUrl",
	"effectiveNormalizedLicense", 
	"guessedLicenseUrl", 
	"guessedLicenseContent",
	"unifiedGuessedLicenseContent" 
order by 
	"groupId", 
	"artifactId", 
	"version", 
	"effectiveNormalizedLicense"