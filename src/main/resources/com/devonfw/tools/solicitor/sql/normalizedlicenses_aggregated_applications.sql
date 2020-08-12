-- SPDX-License-Identifier: Apache-2.0
--
-- generate list of NormalizedLicenses with aggregation of application and version.
-- The trace info will also be aggregated as it normally differs for different Applications
-- due to reading from different input files.
-- This is a replacement of "allden_normalizedlicenses.sql" for generating aggregated reports.
select 
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("version",'-'),NVL("normalizedLicense",'-')) as CORR_KEY_0,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("normalizedLicense",'-')) as CORR_KEY_1,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-'),NVL("version",'-')) as CORR_KEY_2,
    CONCAT(NVL("applicationName",'-'),NVL("groupId",'-'),NVL("artifactId",'-')) as CORR_KEY_3,
    "applicationName", 
	"version" , 
	"trace" , 
	"groupId",
	"artifactId",
	"repoType",
	"ossHomepage",
	"usagePattern",
	"ossModified",	
	"declaredLicense",
	"licenseUrl",
	"normalizedLicenseType",
	"normalizedLicense",
	"normalizedLicenseUrl",
	"effectiveNormalizedLicenseType",
	"effectiveNormalizedLicense",
	"effectiveNormalizedLicenseUrl",
	"legalPreApproved",
	"copyLeft",
	"licenseCompliance",
	"licenseRefUrl",
	"includeLicense",
	"includeSource",
	"reviewedForRelease",
	"comments",
	"legalApproved",
	"legalComments"

from (
	select
		GROUP_CONCAT(DISTINCT a."applicationName" ORDER BY "applicationName" ASC SEPARATOR ', ') as "applicationName", 
		GROUP_CONCAT(DISTINCT ac."version" ORDER BY "version" ASC SEPARATOR ', ') as "version" , 
		GROUP_CONCAT(l."trace" ORDER BY "trace" ASC SEPARATOR U&'\000D\000A---------\000D\000A') as "trace" , 
		ac."groupId",
		ac."artifactId",
		ac."repoType",
		ac."ossHomepage",
		ac."usagePattern",
		ac."ossModified",	
		l."declaredLicense",
		l."licenseUrl",
		l."normalizedLicenseType",
		l."normalizedLicense",
		l."normalizedLicenseUrl",
		l."effectiveNormalizedLicenseType",
		l."effectiveNormalizedLicense",
		l."effectiveNormalizedLicenseUrl",
		l."legalPreApproved",
		l."copyLeft",
		l."licenseCompliance",
		l."licenseRefUrl",
		l."includeLicense",
		l."includeSource",
		l."reviewedForRelease",
		l."comments",
		l."legalApproved",
		l."legalComments"
	from 
		APPLICATION a, 
		APPLICATIONCOMPONENT ac, 
		NORMALIZEDLICENSE l 
	where
		a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
		ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE  
	group by 
		"groupId",
		"artifactId",
		"repoType",
		"ossHomepage",
		"usagePattern",
		"ossModified",	
		"declaredLicense",
		"licenseUrl",
		"normalizedLicenseType",
	    "normalizedLicense",
		"normalizedLicenseUrl",
		"effectiveNormalizedLicenseType",
		"effectiveNormalizedLicense",
	    "effectiveNormalizedLicenseUrl",
		"legalPreApproved",
		"copyLeft",
	    "licenseCompliance",
		"licenseRefUrl",
		"includeLicense",
		"includeSource",
	    "reviewedForRelease",
		"comments",
		"legalApproved",
		"legalComments"
)
order by
	UPPER("groupId"), 
	UPPER("artifactId"), 
	UPPER("version"), 
	UPPER("effectiveNormalizedLicense"),
	UPPER("normalizedLicense"),
	UPPER("applicationName")
	