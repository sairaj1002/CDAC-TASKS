package in.gov.wamis.integration.registry;

import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.common.exception.PortalDisabledException;
import in.gov.wamis.integration.common.ProcurementPortal;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for e-procurement portal adapters.
 * Selects an adapter by PortalCode without long if/else chains.
 */
@Component
public class PortalAdapterRegistry {

    private final Map<PortalCode, ProcurementPortal> adapterMap = new EnumMap<>(PortalCode.class);

    public PortalAdapterRegistry(List<ProcurementPortal> adapters) {
        if (adapters != null) {
            for (ProcurementPortal adapter : adapters) {
                adapterMap.put(adapter.getPortalCode(), adapter);
            }
        }
    }

    public ProcurementPortal getAdapter(PortalCode portalCode) {
        if (portalCode == null) {
            throw new IllegalArgumentException("Portal code must not be null");
        }
        ProcurementPortal adapter = adapterMap.get(portalCode);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter registered for portal: " + portalCode);
        }
        if (!adapter.isEnabled()) {
            throw new PortalDisabledException(portalCode.name());
        }
        return adapter;
    }

    public ProcurementPortal getAdapter(String portalCodeStr) {
        return getAdapter(PortalCode.fromString(portalCodeStr));
    }
}

