-- Minimal master rows for the H2 test profile (Hibernate creates the tables);
-- covers the defaults used by PreTenderForm and PreTenderService.seedSampleWorks
INSERT INTO wamis_master.master_tender_category (category, description, display_order) VALUES ('Works', 'Works', 1);
INSERT INTO wamis_master.master_tender_type (tendertype, description, display_order) VALUES ('Open Tender', 'Open Tender', 1);
INSERT INTO wamis_master.master_form_of_contract (formofcontract, display_order) VALUES ('Supply', 1);
INSERT INTO wamis_master.master_form_of_contract (formofcontract, display_order) VALUES ('Percentage', 2);
INSERT INTO wamis_master.master_bid_openers_type (botype, bovalue, description, display_order) VALUES ('2 Off 2', 2, '2 Of 2', 1);
INSERT INTO wamis_master.master_tenderer_class (tenderclass, description, display_order) VALUES ('Class A', 'Class A', 1);
