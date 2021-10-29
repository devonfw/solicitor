-- SPDX-License-Identifier: Apache-2.0
--
-- returns data needed to create script for downloading all sources which need to be included in the distribution
select distinct
	REGEXP_REPLACE(a."applicationName",'\s','') as "applicationName",
	NVL(ac."groupId", 'NA') as "groupId",
	NVL(ac."artifactId", 'NA') as "artifactId",
	NVL(ac."version", 'NA' ) as "version",
	NVL(ac."repoType", 'NA' ) as "repoType",
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
	l."includeSource" != 'no'
group by
	"applicationName",
	"groupId",
	"artifactId",
	"version",
	"repoType"
order by
	"applicationName",
	"groupId",
	"artifactId",
	"version"
