-- SPDX-License-Identifier: Apache-2.0
--
-- returns data needed to create script for downloading all sources which need to be included in the distribution
select distinct
	NVL(ac."groupId", 'NA') as "groupId",
	NVL(ac."artifactId", 'NA') as "artifactId",
	NVL(ac."version", 'NA' ) as "version",
	NVL(ac."packageUrl", 'NA' ) as "packageUrl",
	REGEXP_REPLACE(NVL(ac."groupId", 'NA'),'\.','/') as "groupIdT",
	GROUP_CONCAT(DISTINCT l."includeSource" ORDER BY l."includeSource" DESC SEPARATOR ', ') as "includeSource",
	GROUP_CONCAT(DISTINCT l."effectiveNormalizedLicense" ORDER BY l."effectiveNormalizedLicense" DESC SEPARATOR ', ') as "effectiveNormalizedLicense"
from
	APPLICATION a,
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND
	l."normalizedLicenseType" not in ('COMMERCIAL')
group by
	"groupId",
	"artifactId",
	"packageUrl",
	"version"
order by
	"groupId",
	"artifactId",
	"version"
