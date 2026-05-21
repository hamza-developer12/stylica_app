package com.example.stylica_app;

import com.example.stylica_app.models.UserModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTests {

    private UserModel adminUser;
    private UserModel moderatorUser;
    private UserModel vendorUser;
    private UserModel customerUser;

    @Before
    public void setUp() {
        adminUser = new UserModel(
                "uid_admin", "Ali", "Khan", "Male",
                "admin@stylica.com", "admin",
                "03001234567", "Islamabad", "", "verified"
        );

        moderatorUser = new UserModel(
                "uid_mod", "Sara", "Ahmed", "Female",
                "mod@stylica.com", "moderator",
                "03001234568", "Lahore", "fashion", "verified"
        );

        vendorUser = new UserModel(
                "uid_vendor", "Usman", "Tariq", "Male",
                "vendor@stylica.com", "vendor",
                "03001234569", "Karachi", "electronics", "verified"
        );

        customerUser = new UserModel(
                "uid_customer", "Ayesha", "Malik", "Female",
                "customer@stylica.com", "customer",
                "03001234570", "Peshawar", "", "verified"
        );
    }

    // ─── Role Assignment Tests ───────────────────────────────────────────────

    @Test
    public void testAdminRoleIsCorrect() {
        assertEquals("admin", adminUser.getRole());
    }

    @Test
    public void testModeratorRoleIsCorrect() {
        assertEquals("moderator", moderatorUser.getRole());
    }

    @Test
    public void testVendorRoleIsCorrect() {
        assertEquals("vendor", vendorUser.getRole());
    }

    @Test
    public void testCustomerRoleIsCorrect() {
        assertEquals("customer", customerUser.getRole());
    }

    // ─── Role Redirect Logic Tests ───────────────────────────────────────────

    @Test
    public void testAdminRedirectsToAdminDashboard() {
        String destination = getDestinationForRole(adminUser.getRole());
        assertEquals("AdminDashboardActivity", destination);
    }

    @Test
    public void testModeratorRedirectsToModeratorDashboard() {
        String destination = getDestinationForRole(moderatorUser.getRole());
        assertEquals("ModeratorDashboardActivity", destination);
    }

    @Test
    public void testVendorRedirectsToVendorDashboard() {
        String destination = getDestinationForRole(vendorUser.getRole());
        assertEquals("VendorDashboardActivity", destination);
    }

    @Test
    public void testCustomerRedirectsToCustomerDashboard() {
        String destination = getDestinationForRole(customerUser.getRole());
        assertEquals("CustomerDashboardActivity", destination);
    }

    @Test
    public void testUnknownRoleRedirectsToLogin() {
        String destination = getDestinationForRole("unknown_role");
        assertEquals("LoginActivity", destination);
    }

    // ─── Verification Status Tests ───────────────────────────────────────────

    @Test
    public void testVerifiedUserCanLogin() {
        assertTrue(isAllowedToLogin(adminUser));
        assertTrue(isAllowedToLogin(moderatorUser));
        assertTrue(isAllowedToLogin(vendorUser));
        assertTrue(isAllowedToLogin(customerUser));
    }

    @Test
    public void testUnverifiedUserCannotLogin() {
        UserModel unverifiedVendor = new UserModel(
                "uid_unverified", "Zara", "Shah", "Female",
                "zara@stylica.com", "vendor",
                "03009999999", "Multan", "fashion", "pending"
        );
        assertFalse(isAllowedToLogin(unverifiedVendor));
    }

    @Test
    public void testRejectedUserCannotLogin() {
        UserModel rejectedUser = new UserModel(
                "uid_rejected", "Bilal", "Raza", "Male",
                "bilal@stylica.com", "vendor",
                "03008888888", "Faisalabad", "electronics", "rejected"
        );
        assertFalse(isAllowedToLogin(rejectedUser));
    }

    // ─── Field Integrity Tests ────────────────────────────────────────────────

    @Test
    public void testNoUserHasNullEmail() {
        assertNotNull(adminUser.getEmail());
        assertNotNull(moderatorUser.getEmail());
        assertNotNull(vendorUser.getEmail());
        assertNotNull(customerUser.getEmail());
    }

    @Test
    public void testNoUserHasNullUserId() {
        assertNotNull(adminUser.getUserId());
        assertNotNull(moderatorUser.getUserId());
        assertNotNull(vendorUser.getUserId());
        assertNotNull(customerUser.getUserId());
    }

    @Test
    public void testVendorAndModeratorHaveDomain() {
        assertNotNull(vendorUser.getDomain());
        assertFalse(vendorUser.getDomain().isEmpty());

        assertNotNull(moderatorUser.getDomain());
        assertFalse(moderatorUser.getDomain().isEmpty());
    }

    // ─── Helper Methods (mirrors your actual login logic) ────────────────────

    private String getDestinationForRole(String role) {
        if (role == null) return "LoginActivity";
        switch (role) {
            case "admin":     return "AdminDashboardActivity";
            case "moderator": return "ModeratorDashboardActivity";
            case "vendor":    return "VendorDashboardActivity";
            case "customer":  return "CustomerDashboardActivity";
            default:          return "LoginActivity";
        }
    }

    private boolean isAllowedToLogin(UserModel user) {
        return "verified".equals(user.getVerificationStatus());
    }
}