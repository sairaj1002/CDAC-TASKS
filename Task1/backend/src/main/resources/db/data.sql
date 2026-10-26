INSERT INTO road_component (
    scope_id, cd_work_type, created_at, deleted, road_length, nature_of_work, 
    office_id, proposed_road_type, repair_maintenance_type, revision_timestamp, 
    revision_type, tenant_id, updated_at, road_width, work_id, work_plan_type
) VALUES 
(101, 'MINOR_BRIDGE', NOW(), false, 550, 'NEW_CONSTRUCTION', 10, 'MURUM_EARTHEN', NULL, NOW(), 'CREATED', 1, NOW(), 7, 2001, 'PLAN'),
(102, 'DD_DHAPA_DRAIN', NOW(), false, 1200, 'MAINTENANCE', 10, 'WBM_METAL', 'SPECIAL_REPAIR', NOW(), 'UPDATED', 1, NOW(), 6, 2002, 'NON_PLAN'),
(103, 'BC_BOX_CULVERT', NOW(), false, 850, 'IMPROVEMENT', 12, 'BT_DAMBRI', NULL, NOW(), 'CREATED', 1, NOW(), 12, 2003, 'PLAN'),
(104, 'SD_SLAB_DRAIN', NOW(), false, 300, 'RECONSTRUCTION', 14, 'CONCRETE', 'CURRENT_REPAIR_AMC', NOW(), 'UPDATED', 2, NOW(), 4, 2004, 'NON_PLAN'),
(105, 'ARCH_CULVERT', NOW(), false, 2100, 'NEW_CONSTRUCTION', 11, 'CONCRETE', NULL, NOW(), 'CREATED', 1, NOW(), 10, 2005, 'PLAN'),
(106, 'HP_HARD_PASSAGE', NOW(), true, 450, 'MAINTENANCE', 15, 'BT_DAMBRI', 'FDR', NOW(), 'DELETED', 3, NOW(), 7, 2006, 'NON_PLAN'),
(107, 'HPC_HUME_PIPE_CULVERT', NOW(), false, 1500, 'IMPROVEMENT', 10, 'WBM_METAL', NULL, NOW(), 'CREATED', 1, NOW(), 14, 2007, 'PLAN'),
(108, 'VC_VENTED_CAUSEWAY', NOW(), false, 980, 'RECONSTRUCTION', 12, 'MURUM_EARTHEN', 'SPECIAL_REPAIR', NOW(), 'UPDATED', 1, NOW(), 8, 2008, 'NON_PLAN'),
(109, 'MINOR_BRIDGE', NOW(), false, 3400, 'NEW_CONSTRUCTION', 19, 'BT_DAMBRI', NULL, NOW(), 'CREATED', 2, NOW(), 18, 2009, 'PLAN'),
(110, 'BC_BOX_CULVERT', NOW(), false, 620, 'MAINTENANCE', 14, 'CONCRETE', 'CURRENT_REPAIR_AMC', NOW(), 'UPDATED', 1, NOW(), 5, 2010, 'NON_PLAN')
ON CONFLICT (scope_id) DO NOTHING;

INSERT INTO bridge_component (
    scope_id, approval_road, bridge_activity, created_at, deleted, length, 
    number_of_spans, office_id, other_activity_description, revision_timestamp, 
    revision_type, river_nallah_barrier, span_length, tenant_id, updated_at, width, work_id
) VALUES 
(501, 'NH-48 Link', 'NEW_CONSTRUCTION', NOW(), false, 45, 3, 10, NULL, NOW(), 'CREATED', 'Ganga River', 15, 1, NOW(), 12, 3001),
(502, 'State Highway 4', 'REPAIR_TO_BRIDGE', NOW(), false, 120, 6, 12, NULL, NOW(), 'UPDATED', 'Railway Crossing', 20, 1, NOW(), 14, 3002),
(503, 'District Road 22', 'OTHER_ACTIVITY', NOW(), false, 15, 1, 14, 'Routine foundation check', NOW(), 'UPDATED', 'Local Nallah', 15, 2, NOW(), 8, 3003),
(504, 'Bypass Road A', 'NEW_CONSTRUCTION', NOW(), false, 210, 7, 10, NULL, NOW(), 'CREATED', 'Yamuna River', 30, 1, NOW(), 16, 3004),
(505, 'Rural Road 105', 'REPAIR_TO_BRIDGE', NOW(), false, 30, 2, 15, NULL, NOW(), 'CREATED', 'Canal Cross', 15, 3, NOW(), 6, 3005),
(506, 'NH-48 Link', 'OTHER_ACTIVITY', NOW(), true, 45, 3, 10, 'Visual inspection audit', NOW(), 'DELETED', 'Ganga River', 15, 1, NOW(), 12, 3006),
(507, 'City Center Flyover', 'NEW_CONSTRUCTION', NOW(), false, 350, 12, 11, NULL, NOW(), 'CREATED', 'Metro Line Intersect', 30, 1, NOW(), 22, 3007),
(508, 'Coastal Highway', 'REPAIR_TO_BRIDGE', NOW(), false, 80, 4, 19, NULL, NOW(), 'UPDATED', 'Tidal Creek', 20, 2, NOW(), 15, 3008),
(509, 'Forest Route 3', 'OTHER_ACTIVITY', NOW(), false, 25, 1, 14, 'Clearance and widening', NOW(), 'CREATED', 'Mountain Stream', 25, 1, NOW(), 3, 3009),
(510, 'Expressway Link 2', 'NEW_CONSTRUCTION', NOW(), false, 500, 15, 10, NULL, NOW(), 'CREATED', 'Narmada River', 35, 1, NOW(), 24, 3010)
ON CONFLICT (scope_id) DO NOTHING;

-- Synchronize sequences so subsequent autoincrement inserts do not collide
SELECT setval(pg_get_serial_sequence('road_component', 'scope_id'), COALESCE((SELECT MAX(scope_id) FROM road_component), 1));
SELECT setval(pg_get_serial_sequence('bridge_component', 'scope_id'), COALESCE((SELECT MAX(scope_id) FROM bridge_component), 1));
