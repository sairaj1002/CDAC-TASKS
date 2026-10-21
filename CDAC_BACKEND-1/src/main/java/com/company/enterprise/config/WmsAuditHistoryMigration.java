package com.company.enterprise.config;

import java.sql.SQLException;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WmsAuditHistoryMigration implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public WmsAuditHistoryMigration(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws SQLException {
        boolean workAuditExists = tableExists("work_details_audit");
        boolean revisionExists = tableExists("audit_revision");

        if (!workAuditExists && !revisionExists) {
            return;
        }
        if (workAuditExists != revisionExists) {
            throw new IllegalStateException(
                    "Cannot safely migrate WMS audit history: both work_details_audit and audit_revision must exist");
        }

        jdbcTemplate.update("""
                INSERT INTO wms_audit_details (
                    legacy_revision, revision_at, revision_type, work_reg_no, work_details,
                    actual_start_date, contractor_name, stipulated_start_date, stipulated_end_date,
                    created_at, updated_at, created_by, modified_by, user_id, user_name, ip_address, mac_address
                )
                SELECT
                    revision.id,
                    to_timestamp(revision.timestamp / 1000.0)::timestamp,
                    COALESCE(
                        revision.revision_type,
                        CASE audit_row.revtype
                            WHEN 0 THEN 'CREATED'
                            WHEN 1 THEN 'UPDATED'
                            WHEN 2 THEN 'DELETED'
                        END
                    ),
                    audit_row.work_reg_no,
                    audit_row.work_details,
                    audit_row.actual_start_date,
                    audit_row.contractor_name,
                    audit_row.stipulated_start_date,
                    audit_row.stipulated_end_date,
                    audit_row.created_at,
                    audit_row.updated_at,
                    first_revision.user_name,
                    revision.user_name,
                    revision.user_id,
                    revision.user_name,
                    revision.ip_address,
                    revision.mac_address
                FROM work_details_audit audit_row
                JOIN audit_revision revision ON revision.id = audit_row.rev
                LEFT JOIN (
                    SELECT DISTINCT ON (work_reg_no) work_reg_no, rev
                    FROM work_details_audit
                    ORDER BY work_reg_no, rev
                ) first_audit ON first_audit.work_reg_no = audit_row.work_reg_no
                LEFT JOIN audit_revision first_revision ON first_revision.id = first_audit.rev
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM wms_audit_details migrated
                    WHERE migrated.legacy_revision = audit_row.rev
                      AND migrated.work_reg_no = audit_row.work_reg_no
                )
                """);

        int missingCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM work_details_audit source
                LEFT JOIN wms_audit_details target
                  ON target.legacy_revision = source.rev
                 AND target.work_reg_no = source.work_reg_no
                WHERE target.audit_id IS NULL
                """, Integer.class);
        if (missingCount != 0) {
            throw new IllegalStateException(
                    "WMS audit history migration did not copy every work_details_audit row");
        }

        jdbcTemplate.execute("DROP TABLE work_details_audit");
        jdbcTemplate.execute("DROP TABLE audit_revision");
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            var metadata = connection.getMetaData();
            try (var tables = metadata.getTables(connection.getCatalog(), null, tableName, new String[] {"TABLE"})) {
                if (tables.next()) {
                    return true;
                }
            }
            try (var tables = metadata.getTables(
                    connection.getCatalog(), null, tableName.toUpperCase(Locale.ROOT), new String[] {"TABLE"})) {
                return tables.next();
            }
        }
    }
}
