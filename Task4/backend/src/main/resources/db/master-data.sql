-- ====================================================================
-- WAMIS master data from demo_masters_updated.xls
--
-- Run by Spring Boot on every startup, right after db/schema.sql (spring.sql.init.data-locations).
-- Safe to re-run. Can also be run by hand:  psql -U postgres -d wamis -f master-data.sql
--   1. loads the sheet rows into the wamis_master tables
--   2. refreshes the matching categories in wamis_core.wamis_master_dropdowns, which the
--      existing /api/masters/dropdowns endpoint serves to the frontend
--   3. maps legacy values on existing pre-tenders to the master values
-- Cleanups made to the sheet data: leading spaces trimmed (tenderer_class), one exact
-- duplicate row dropped (product_category: 'Aids and appliances for the disabled'/Services).
-- ====================================================================

INSERT INTO wamis_master.master_tender_category (category, description, display_order) VALUES
    ('Goods', 'Goods', 1),
    ('Services', 'Services', 2),
    ('Works', 'Works', 3)
ON CONFLICT (category) DO NOTHING;

INSERT INTO wamis_master.master_product_category (name, tender_category_id, display_order)
SELECT v.name, c.id, v.display_order
FROM (VALUES
    ('new', 'Goods', 1),
    ('ABCDE', 'Goods', 2),
    ('Mechanical structural/Pipelines works', 'Services', 3),
    ('Instrumentation services', 'Services', 4),
    ('Electrical related work', 'Services', 5),
    ('Civil works', 'Services', 6),
    ('Boiler related works', 'Services', 7),
    ('Refractory/castable works', 'Services', 8),
    ('Fumigation works', 'Services', 9),
    ('Painting works', 'Services', 10),
    ('Plantation related works', 'Services', 11),
    ('Printing/Binding related works', 'Services', 12),
    ('Operations & Maintenance Contract', 'Services', 13),
    ('Insulation work', 'Services', 14),
    ('Sports Goods/Equipments', 'Goods', 15),
    ('Uniforms/Curtains/Clothes', 'Goods', 16),
    ('Computer- Data Processing', 'Goods', 17),
    ('Computer- H/W', 'Goods', 18),
    ('Computer- Manpower', 'Goods', 19),
    ('Computer- S/W', 'Goods', 20),
    ('Electronics Equipment', 'Goods', 21),
    ('Food Products', 'Goods', 22),
    ('Furniture/ Fixture', 'Goods', 23),
    ('Pesticide', 'Goods', 24),
    ('Publishing/Printing', 'Goods', 25),
    ('Stationery', 'Goods', 26),
    ('Medicines', 'Goods', 27),
    ('Medical Equipments/Waste', 'Goods', 28),
    ('Miscellaneous', 'Goods', 29),
    ('Pipe Laying Works', 'Works', 30),
    ('Agricultural or Forestry', 'Works', 31),
    ('UPS', 'Works', 32),
    ('Packaging', 'Works', 33),
    ('Industrial/Medical GAS', 'Works', 34),
    ('Documentary film,Video film', 'Works', 35),
    ('Pumps/Motors', 'Works', 36),
    ('Weighing System', 'Works', 37),
    ('Bearings', 'Works', 38),
    ('Air Compressor', 'Works', 39),
    ('Network /Communication Equipments', 'Works', 40),
    ('Textile', 'Works', 41),
    ('Chemicals/Minerals', 'Works', 42),
    ('Security System', 'Works', 43),
    ('Audio-Visual Equipment', 'Works', 44),
    ('Access Control System', 'Works', 45),
    ('Architecture/Interior Design', 'Works', 46),
    ('Explosive', 'Works', 47),
    ('Non-Explosive', 'Works', 48),
    ('Water Equipments/ Meter/ Drilling/ Boring', 'Works', 49),
    ('Metal Fabrication', 'Works', 50),
    ('Adhesives', 'Works', 51),
    ('Warehouse', 'Works', 52),
    ('Cargo / Containers', 'Works', 53),
    ('Government Stock/Security', 'Works', 54),
    ('Land/Building', 'Works', 55),
    ('Power/Energy Projects/Products', 'Works', 56),
    ('Iron/Steel Materials', 'Works', 57),
    ('Survey', 'Works', 58),
    ('Scrap/Disposables', 'Works', 59),
    ('Metals', 'Works', 60),
    ('Ceramics', 'Works', 61),
    ('Oil/Gas', 'Works', 62),
    ('Mining', 'Works', 63),
    ('Coal', 'Works', 64),
    ('Crane Services', 'Services', 65),
    ('Entertainment/Musical Instruments', 'Works', 66),
    ('Plant Protection Equipment', 'Works', 67),
    ('Solar water heater', 'Works', 68),
    ('Bus Body Construction', 'Works', 69),
    ('JUTE PRODUCT', 'Works', 70),
    ('Fleet Management', 'Works', 71),
    ('Micro Nutrient Chemicals/Printed Polythene Covers', 'Works', 72),
    ('Handicrafts', 'Works', 73),
    ('Bio-Fertilizer Production Materials', 'Works', 74),
    ('Air-Conditioner', 'Services', 75),
    ('Hotel/ Catering', 'Services', 76),
    ('Housekeeping/ Cleaning', 'Services', 77),
    ('Machineries', 'Services', 78),
    ('Support/Maintenance Service', 'Services', 79),
    ('Shipping/ Transportation/ Vehicle', 'Services', 80),
    ('Information Technology', 'Services', 81),
    ('Fire & Safety', 'Services', 82),
    ('Aviation', 'Services', 83),
    ('Consultancy', 'Services', 84),
    ('Supply and Erection', 'Services', 85),
    ('Aids and appliances for the disabled', 'Services', 86),
    ('Fertilizer', 'Services', 87),
    ('Automatic Bus Washing Machine', 'Services', 88),
    ('Supply, Erection and Commissioning', 'Services', 89),
    ('Crop Products', 'Goods', 90),
    ('metal makes', 'Goods', 91),
    ('Mobile Goods', 'Goods', 92),
    ('Mobile Services', 'Services', 93),
    ('Mobile Works', 'Works', 94),
    ('Information Technology (IT)', 'Services', 95),
    ('Railway Goods', 'Goods', 96),
    ('Civil Works - Construction Works', 'Works', 97),
    ('Cables and Wires', 'Goods', 98),
    ('Abrasives', 'Goods', 99),
    ('Castings / Foundaries', 'Goods', 100),
    ('Instrumentation', 'Goods', 101),
    ('Mechanical Equipment', 'Goods', 102),
    ('Paint/Enamel Works', 'Works', 103),
    ('Electrical Goods', 'Goods', 104),
    ('Handicrafts', 'Services', 105),
    ('Jute Products (Services)', 'Services', 106),
    ('Micro Nutrient Chemicals/Printed Polythene Covers', 'Services', 107),
    ('Solar Water Heater', 'Services', 108),
    ('Solid Waste Management Works', 'Works', 109),
    ('Stones/Granite', 'Works', 110),
    ('Surgical Sutures', 'Works', 111),
    ('Ash Handling Equipments', 'Goods', 112),
    ('Electrical Generators', 'Goods', 113),
    ('Hydrogen Generation', 'Goods', 114),
    ('Locomotive System', 'Goods', 115),
    ('Mechanical Fasteners', 'Goods', 116),
    ('Non Dairy Items', 'Goods', 117),
    ('Metals - Ferrous', 'Goods', 118),
    ('Metals - Non Ferrous', 'Goods', 119),
    ('Metal Plates', 'Goods', 120),
    ('Vehicles/Vehicle Spares', 'Goods', 121),
    ('Consumables - Cotton', 'Goods', 122),
    ('Consumables - Paper/Printing/Photocopy Paper', 'Goods', 123),
    ('Consumables - Raw Materials', 'Goods', 124),
    ('Minor Forest Produce', 'Goods', 125),
    ('Animal Feed Ingredients', 'Goods', 126),
    ('Corporate Bonds/Investments', 'Goods', 127),
    ('Synthetic Web Equipment', 'Goods', 128),
    ('Supply, errection and Commissioning', 'Services', 129),
    ('Hiring of Vehicles', 'Services', 130),
    ('Repair and Maintenance', 'Services', 131),
    ('AMC/Maintenance Contracts', 'Services', 132),
    ('Shipping', 'Services', 133),
    ('Power Packs(Hydralic, Solar, etc.,)', 'Services', 134),
    ('Marine Services', 'Services', 135),
    ('Survey and Investigation services', 'Services', 136),
    ('Sugar and allied products', 'Services', 137),
    ('Allotment of Space', 'Services', 138),
    ('Manpower Supply', 'Services', 139),
    ('Photostat services', 'Services', 140),
    ('Info. Tech. Services', 'Services', 141),
    ('Facility Management', 'Services', 142),
    ('Advertisement Services', 'Services', 143),
    ('Scanning, Digitisation Services', 'Services', 144),
    ('Marine Works', 'Works', 145),
    ('Pipes and Pipe related activities', 'Works', 146),
    ('Gold and Silver Coins/Bars', 'Works', 147),
    ('Medals, Corporate Mementos/Plates', 'Works', 148),
    ('Street Lighting', 'Works', 149),
    ('Surveillance Equipments', 'Works', 150),
    ('Civil Works - Buildings', 'Works', 151),
    ('Civil Works - Bridges', 'Works', 152),
    ('Civil Works - Roads', 'Works', 153),
    ('Civil Works - Canal', 'Works', 154),
    ('Civil Works - Water Works', 'Works', 155),
    ('Civil Works - Lift Irrigation Schemes', 'Works', 156),
    ('Civil Works - Highways', 'Works', 157),
    ('Civil Works - Others', 'Works', 158),
    ('Job Works', 'Works', 159),
    ('Machinery and Machining Tools', 'Goods', 160),
    ('Electronic Componets and Devices', 'Goods', 161),
    ('Construction Works', 'Works', 162),
    ('Electrical Works', 'Works', 163),
    ('Plant Protection Input/Equpmt Works', 'Works', 164),
    ('Miscellaneous Goods', 'Goods', 165),
    ('Miscellaneous Services', 'Services', 166),
    ('Miscellaneous Works', 'Works', 167),
    ('Financial and Insurance Services', 'Services', 168),
    ('Condemned scrap material', 'Goods', 169),
    ('Condemned buses and other vehicles', 'Goods', 170),
    ('Silk By-product', 'Goods', 171),
    ('Recruitment Services', 'Services', 172),
    ('Silk bye product', 'Goods', 173),
    ('save', 'Services', 174),
    ('Hiring of Goods', 'Goods', 175),
    ('Mgmt. Of Stockyard in JV', 'Services', 176)
) AS v(name, tendercategory, display_order)
JOIN wamis_master.master_tender_category c ON c.category = v.tendercategory
ON CONFLICT (name, tender_category_id) DO NOTHING;

INSERT INTO wamis_master.master_tender_type (tendertype, description, display_order) VALUES
    ('Single', 'Single Tender', 1),
    ('Open Tender', 'Open Tender', 2),
    ('Open Limited', 'Open Limited', 3),
    ('Limited', 'Limited Tender', 4)
ON CONFLICT (tendertype) DO NOTHING;

INSERT INTO wamis_master.master_form_of_contract (formofcontract, display_order) VALUES
    ('QCBS', 1),
    ('Turn-key', 2),
    ('Lump-sum', 3),
    ('Multi-stage', 4),
    ('Percentage', 5),
    ('Item Rate', 6),
    ('Fixed-rate', 7),
    ('Tender cum Auction', 8),
    ('Item Wise', 9),
    ('Empanelment', 10),
    ('EPC Contract', 11),
    ('Fixed Rate Contract', 12),
    ('EOI', 13),
    ('PPP-BoT-ToT', 14),
    ('Supply', 15),
    ('Works', 16),
    ('Service', 17),
    ('Piece-work', 18)
ON CONFLICT (formofcontract) DO NOTHING;

INSERT INTO wamis_master.master_bid_openers_type (botype, bovalue, description, display_order) VALUES
    ('2 Off 4', 4, '2 Of 4', 1),
    ('2 Off 3', 3, '2 Of 3', 2),
    ('2 Off 2', 2, '2 Of 2', 3),
    ('3 Off 3', 3, '3 Of 3', 4)
ON CONFLICT (botype) DO NOTHING;

INSERT INTO wamis_master.master_tenderer_class (tenderclass, description, display_order) VALUES
    ('A', 'Class A', 1),
    ('B', 'Class B', 2),
    ('C', 'Class C', 3),
    ('D', 'Class D', 4),
    ('E', 'Class E', 5),
    ('OTHERS', 'All other categories', 6),
    ('I class', 'First class', 7),
    ('II class', 'Second class', 8),
    ('III class', 'Third class', 9),
    ('IV class', 'Fourth class', 10),
    ('V class', 'Fifth class', 11),
    ('Class III A & above', 'Civil', 12),
    ('Class IV & above', 'Class IV & above', 13),
    ('Test', 'Test', 14),
    ('Class A', 'Class A', 15),
    ('F', 'Class F', 16),
    ('NA', 'puthire recred', 17),
    ('Not Applicable', 'Not Applicable', 18)
ON CONFLICT (tenderclass) DO NOTHING;

-- --------------------------------------------------------------------
-- Feed the existing dropdown API (GET /api/masters/dropdowns) from the master tables.
-- display_name is the master value the frontend sends; the backend resolves it to the
-- master row id that wamis_pretenders stores as a foreign key.
-- --------------------------------------------------------------------
DELETE FROM wamis_core.wamis_master_dropdowns
 WHERE category IN ('FORM_OF_CONTRACT','BID_OPENERS','TENDER_TYPE','TENDER_CATEGORY','PRODUCT_CATEGORY','TENDERER_CLASS');

INSERT INTO wamis_core.wamis_master_dropdowns (category, code, display_name, description, display_order, active, created_by, created_at, updated_by, updated_at)
SELECT 'FORM_OF_CONTRACT', formofcontract, formofcontract, NULL, display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now() FROM wamis_master.master_form_of_contract
UNION ALL
SELECT 'BID_OPENERS', botype, botype, description, display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now() FROM wamis_master.master_bid_openers_type
UNION ALL
SELECT 'TENDER_TYPE', tendertype, tendertype, description, display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now() FROM wamis_master.master_tender_type
UNION ALL
SELECT 'TENDER_CATEGORY', category, category, description, display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now() FROM wamis_master.master_tender_category
UNION ALL
-- description carries the parent tender category so the form can filter by it
SELECT 'PRODUCT_CATEGORY', left(p.name, 40) || '|' || c.category, p.name, c.category, p.display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now()
  FROM wamis_master.master_product_category p JOIN wamis_master.master_tender_category c ON c.id = p.tender_category_id
UNION ALL
SELECT 'TENDERER_CLASS', tenderclass, tenderclass, description, display_order, TRUE, 'SYSTEM', now(), 'SYSTEM', now() FROM wamis_master.master_tenderer_class;

-- --------------------------------------------------------------------
-- One-time conversion of pre-tenders from earlier versions, which stored the master
-- values as text (tender_type, form_contract, category, product_category, bid_openers,
-- tenderer_class): fill the *_id foreign keys from the text, then drop the text columns.
-- Values written by the old hard-coded dropdown seed are mapped to the xls values.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
                WHERE table_schema = 'wamis_core' AND table_name = 'wamis_pretenders'
                  AND column_name = 'tender_type') THEN

        UPDATE wamis_core.wamis_pretenders p SET tender_type_id = m.id
          FROM wamis_master.master_tender_type m
         WHERE p.tender_type_id IS NULL
           AND m.tendertype = CASE p.tender_type
                                  WHEN 'National Competitive Bid' THEN 'Open Tender'
                                  WHEN 'NCB'                      THEN 'Open Tender'
                                  WHEN 'Single Tender'            THEN 'Single'
                                  WHEN 'Limited Tender'           THEN 'Limited'
                                  ELSE p.tender_type END;

        UPDATE wamis_core.wamis_pretenders p SET form_contract_id = m.id
          FROM wamis_master.master_form_of_contract m
         WHERE p.form_contract_id IS NULL
           AND m.formofcontract = CASE p.form_contract WHEN 'Lump Sum' THEN 'Lump-sum' ELSE p.form_contract END;

        UPDATE wamis_core.wamis_pretenders p SET tender_category_id = m.id
          FROM wamis_master.master_tender_category m
         WHERE p.tender_category_id IS NULL AND m.category = p.category;

        UPDATE wamis_core.wamis_pretenders p SET product_category_id = m.id
          FROM wamis_master.master_product_category m
         WHERE p.product_category_id IS NULL
           AND m.name = p.product_category AND m.tender_category_id = p.tender_category_id;

        UPDATE wamis_core.wamis_pretenders p SET bid_openers_id = m.id
          FROM wamis_master.master_bid_openers_type m
         WHERE p.bid_openers_id IS NULL AND m.botype = p.bid_openers;

        UPDATE wamis_core.wamis_pretenders p SET tenderer_class_id = m.id
          FROM wamis_master.master_tenderer_class m
         WHERE p.tenderer_class_id IS NULL
           AND m.tenderclass = CASE p.tenderer_class WHEN 'A or equivalent' THEN 'Class A' ELSE p.tenderer_class END;

        ALTER TABLE wamis_core.wamis_pretenders
            DROP COLUMN tender_type,
            DROP COLUMN form_contract,
            DROP COLUMN category,
            DROP COLUMN IF EXISTS product_category,
            DROP COLUMN IF EXISTS bid_openers,
            DROP COLUMN IF EXISTS tenderer_class;
    END IF;

    -- required references; skipped (with a notice) while an old row could not be mapped
    BEGIN
        ALTER TABLE wamis_core.wamis_pretenders
            ALTER COLUMN tender_type_id     SET NOT NULL,
            ALTER COLUMN form_contract_id   SET NOT NULL,
            ALTER COLUMN tender_category_id SET NOT NULL;
    EXCEPTION WHEN not_null_violation THEN
        RAISE NOTICE 'Some pre-tenders have no tender type / form of contract / tender category; fix them, NOT NULL not applied yet';
    END;
END $$;
