-- Copyright 2019 Capgemini SE.
-- SPDX-License-Identifier: Apache-2.0
--
-- generate all Applications in denormalized form including all hierachical data  (allden -> "all denormalized")
select 
	e.*,
	a.* 
from 
	ENGAGEMENT e,
	APPLICATION a
where 
	e.ID_ENGAGEMENT = a.PARENT_APPLICATION
order by 
	UPPER("ID_APPLICATION") -- sort by ID so assuring we have the same order as defined in config
