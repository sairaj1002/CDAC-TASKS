package in.gov.wamis.integration.registry;

import in.gov.wamis.common.enums.PortalCode;
import in.gov.wamis.common.exception.PortalDisabledException;
import in.gov.wamis.integration.common.ProcurementPortal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PortalAdapterRegistryTest {

    @Test
    @DisplayName("Returns registered and enabled adapter for known portal code")
    void testGetKnownEnabledAdapter() {
        ProcurementPortal mockPortal = Mockito.mock(ProcurementPortal.class);
        when(mockPortal.getPortalCode()).thenReturn(PortalCode.GEPNIC);
        when(mockPortal.isEnabled()).thenReturn(true);

        PortalAdapterRegistry registry = new PortalAdapterRegistry(List.of(mockPortal));
        ProcurementPortal adapter = registry.getAdapter(PortalCode.GEPNIC);

        assertNotNull(adapter);
        assertEquals(PortalCode.GEPNIC, adapter.getPortalCode());
    }

    @Test
    @DisplayName("Throws PortalDisabledException when adapter is registered but disabled")
    void testGetDisabledAdapter() {
        ProcurementPortal mockPortal = Mockito.mock(ProcurementPortal.class);
        when(mockPortal.getPortalCode()).thenReturn(PortalCode.GEPNIC);
        when(mockPortal.isEnabled()).thenReturn(false);

        PortalAdapterRegistry registry = new PortalAdapterRegistry(List.of(mockPortal));
        PortalDisabledException ex = assertThrows(PortalDisabledException.class, () ->
                registry.getAdapter(PortalCode.GEPNIC)
        );
        assertEquals("GEPNIC", ex.getPortalCode());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException for unregistered portal")
    void testGetUnknownAdapter() {
        PortalAdapterRegistry registry = new PortalAdapterRegistry(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> registry.getAdapter(PortalCode.GEPNIC));
        assertThrows(IllegalArgumentException.class, () -> registry.getAdapter("NON_EXISTENT"));
        assertThrows(IllegalArgumentException.class, () -> registry.getAdapter((PortalCode) null));
    }
}

