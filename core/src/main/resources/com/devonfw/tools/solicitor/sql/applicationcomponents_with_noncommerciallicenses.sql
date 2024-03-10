-- SPDX-License-Identifier: Apache-2.0
--
select 
	GROUP_CONCAT(DISTINCT a."applicationName" ORDER BY "applicationName" DESC SEPARATOR ', ') as APPS, 
	GROUP_CONCAT(DISTINCT ac."version" ORDER BY "version" DESC SEPARATOR ', ') as "version" , 
	ac."groupId", 
	ac."artifactId",
	ac."packageUrl",
	ac."ossHomepage",
	ac."sourceRepoUrl",
	ac."sourceDownloadUrl",
	ac."packageDownloadUrl",
	ac."copyrights",
	GROUP_CONCAT(DISTINCT CASE WHEN l."effectiveNormalizedLicenseType" = 'IGNORE' THEN CONCAT(l."normalizedLicense", ' (NA)') WHEN l."effectiveNormalizedLicense" != l."normalizedLicense" THEN CONCAT(l."normalizedLicense", ' (redistributed under ', l."effectiveNormalizedLicense", ')') ELSE  l."normalizedLicense" END ORDER BY "normalizedLicense" DESC SEPARATOR ', ') as "licenses" 

from 
	APPLICATION a, 
	APPLICATIONCOMPONENT ac, 
	NORMALIZEDLICENSE l 
where
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND
	l."normalizedLicenseType" NOT in ('COMMERCIAL', 'IGNORE') 
group by 
	"groupId", 
	"artifactId",
	"packageUrl",
	"ossHomepage",
	"sourceRepoUrl",
	"sourceDownloadUrl",
	"packageDownloadUrl",
	"copyrights"
order by 
	"packageUrl"
