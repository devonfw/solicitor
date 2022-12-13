-- SPDX-License-Identifier: Apache-2.0
--
select
	GROUP_CONCAT(DISTINCT a."applicationName" ORDER BY "applicationName" DESC SEPARATOR ', ') as APPS,
	GROUP_CONCAT(DISTINCT ac."version" ORDER BY "version" DESC SEPARATOR ', ') as "version" ,
	ac."groupId",
	ac."artifactId",
	ac."ossHomepage",
  ac."sourceRepoUrl",
	ac."copyrights",
	l."normalizedLicense",
	l."effectiveNormalizedLicense"
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
	"ossHomepage",
	"sourceRepoUrl",
	"copyrights",
	"normalizedLicense",
	"effectiveNormalizedLicense"
order by
	"groupId",
	"artifactId",
	"version",
	"normalizedLicense"
