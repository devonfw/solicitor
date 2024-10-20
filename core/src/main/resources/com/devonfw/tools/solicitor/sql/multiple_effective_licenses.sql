-- SPDX-License-Identifier: Apache-2.0
--
-- generate list of all applicationComponents which have more than one not ignored effecticeNormalizedLicense. I.e. it
-- finds all components where multiple licenses apply simultaneously.
select 
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("version",'-'),NVL("effectiveNormalizedLicenses",'-')) as CORR_KEY_0,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("effectiveNormalizedLicenses",'-')) as CORR_KEY_1,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("version",'-')) as CORR_KEY_2,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-')) as CORR_KEY_3,
    "applicationName", 
	"groupId",
	"artifactId",
	"version" ,
	"packageUrl",
	"effectiveNormalizedLicenses",
	"licenseCount"

from (
	select
		ac."groupId",
		ac."artifactId",
		ac."version",
		ac."packageUrl",
		a."applicationName", 
		GROUP_CONCAT(l."effectiveNormalizedLicense" ORDER BY "effectiveNormalizedLicense" ASC SEPARATOR ', ') as "effectiveNormalizedLicenses",
		count(*) as "licenseCount"
	from 
		APPLICATION a, 
		APPLICATIONCOMPONENT ac, 
		NORMALIZEDLICENSE l 
	where
		a."reportingGroups" LIKE '%#reportingGroup#%' AND 
		a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
		ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND
		l."effectiveNormalizedLicenseType" != 'IGNORE'
	group by 
		"groupId",
		"artifactId",
		"version",
		"packageUrl",
		"applicationName" 
)
where
    "licenseCount" > 1
order by
	UPPER("groupId"), 
	UPPER("artifactId"), 
	UPPER("version"),
	UPPER("applicationName")
	