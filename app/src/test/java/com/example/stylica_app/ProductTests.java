package com.example.stylica_app;

import com.example.stylica_app.models.ProductModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductTests {

    private ProductModel product;

    @Before
    public void setUp() {
        product = new ProductModel(
                "prod_001",
                "Men's Leather Jacket",
                "Premium quality leather jacket",
                "Clothing",
                "Jackets",
                50,
                4999.99,
                "https://cloudinary.com/stylica/jacket.jpg",
                true,
                true,
                "pending",
                "uid_vendor_01",
                "Usman Tariq",
                new double[]{4.5, 3.0, 5.0},
                3
        );
    }

    // ─── GET (Read) Tests ─────────────────────────────────────────────────────

    @Test
    public void testGetProductId() {
        assertEquals("prod_001", product.getProductId());
    }

    @Test
    public void testGetProductName() {
        assertEquals("Men's Leather Jacket", product.getProductName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Premium quality leather jacket", product.getDescription());
    }

    @Test
    public void testGetCategory() {
        assertEquals("Clothing", product.getCategory());
    }

    @Test
    public void testGetSubcategory() {
        assertEquals("Jackets", product.getSubcategory());
    }

    @Test
    public void testGetPrice() {
        assertEquals(4999.99, product.getPrice(), 0.001);
    }

    @Test
    public void testGetStockQuantity() {
        assertEquals(50, product.getStockQuantity());
    }

    @Test
    public void testGetStatus() {
        assertEquals("pending", product.getStatus());
    }

    @Test
    public void testGetVendorId() {
        assertEquals("uid_vendor_01", product.getUserId());
    }

    @Test
    public void testGetVendorName() {
        assertEquals("Usman Tariq", product.getUserName());
    }

    @Test
    public void testGetIsFeatured() {
        assertTrue(product.getFeatured());
    }

    @Test
    public void testGetIsNew() {
        assertTrue(product.getNew());
    }

    @Test
    public void testGetReviewCount() {
        assertEquals(3, product.getReviewCount());
    }

    @Test
    public void testGetReviews() {
        assertNotNull(product.getReviews());
        assertEquals(3, product.getReviews().length);
    }

    @Test
    public void testGetRejectionReasonDefaultIsNull() {
        assertNull(product.getRejectionReason());
    }

    // ─── ADD (Create) Tests ───────────────────────────────────────────────────

    @Test
    public void testAddProductAllFieldsNotNull() {
        assertNotNull(product.getProductId());
        assertNotNull(product.getProductName());
        assertNotNull(product.getDescription());
        assertNotNull(product.getCategory());
        assertNotNull(product.getSubcategory());
        assertNotNull(product.getImageUrl());
        assertNotNull(product.getUserId());
        assertNotNull(product.getUserName());
        assertNotNull(product.getStatus());
    }

    @Test
    public void testAddProductInitialStatusIsPending() {
        assertEquals("pending", product.getStatus());
    }

    @Test
    public void testAddProductPriceMustBePositive() {
        assertTrue(product.getPrice() > 0);
    }

    @Test
    public void testAddProductStockMustBeNonNegative() {
        assertTrue(product.getStockQuantity() >= 0);
    }

    @Test
    public void testAddProductWithZeroStock() {
        ProductModel outOfStock = new ProductModel(
                "prod_002", "Silk Scarf", "Luxury silk scarf",
                "Accessories", "Scarves", 0, 1299.0,
                "https://cloudinary.com/stylica/scarf.jpg",
                false, false, "pending",
                "uid_vendor_02", "Sara Ahmed",
                new double[]{}, 0
        );
        assertEquals(0, outOfStock.getStockQuantity());
    }

    @Test
    public void testAddProductWithNoReviews() {
        ProductModel newProduct = new ProductModel(
                "prod_003", "Cotton Kurta", "Casual kurta",
                "Clothing", "Kurtas", 100, 799.0,
                "https://cloudinary.com/stylica/kurta.jpg",
                false, true, "pending",
                "uid_vendor_03", "Ayesha Malik",
                new double[]{}, 0
        );
        assertEquals(0, newProduct.getReviewCount());
        assertEquals(0, newProduct.getReviews().length);
    }

    // ─── UPDATE Tests ─────────────────────────────────────────────────────────

    @Test
    public void testUpdateProductName() {
        product.setProductName("Men's Suede Jacket");
        assertEquals("Men's Suede Jacket", product.getProductName());
    }

    @Test
    public void testUpdatePrice() {
        product.setPrice(5999.99);
        assertEquals(5999.99, product.getPrice(), 0.001);
    }

    @Test
    public void testUpdateStockQuantity() {
        product.setStockQuantity(30);
        assertEquals(30, product.getStockQuantity());
    }

    @Test
    public void testUpdateDescription() {
        product.setDescription("Updated premium leather jacket with inner lining");
        assertEquals("Updated premium leather jacket with inner lining", product.getDescription());
    }

    @Test
    public void testUpdateCategory() {
        product.setCategory("Men's Wear");
        assertEquals("Men's Wear", product.getCategory());
    }

    @Test
    public void testUpdateImageUrl() {
        product.setImageUrl("https://cloudinary.com/stylica/jacket_v2.jpg");
        assertEquals("https://cloudinary.com/stylica/jacket_v2.jpg", product.getImageUrl());
    }

    @Test
    public void testApproveProduct() {
        product.setStatus("approved");
        assertEquals("approved", product.getStatus());
    }

    @Test
    public void testRejectProductWithReason() {
        product.setStatus("rejected");
        product.setRejectionReason("Images are blurry and description is incomplete");
        assertEquals("rejected", product.getStatus());
        assertEquals("Images are blurry and description is incomplete", product.getRejectionReason());
    }

    @Test
    public void testResubmitRejectedProduct() {
        product.setStatus("rejected");
        product.setRejectionReason("Poor image quality");

        // Vendor fixes and resubmits
        product.setImageUrl("https://cloudinary.com/stylica/jacket_hd.jpg");
        product.setStatus("pending");
        product.setRejectionReason(null);

        assertEquals("pending", product.getStatus());
        assertNull(product.getRejectionReason());
    }

    @Test
    public void testMarkProductAsFeatured() {
        product.setFeatured(false);
        assertFalse(product.getFeatured());
        product.setFeatured(true);
        assertTrue(product.getFeatured());
    }

    @Test
    public void testMarkProductAsNotNew() {
        product.setNew(false);
        assertFalse(product.getNew());
    }

    @Test
    public void testUpdateReviews() {
        double[] updatedReviews = {4.5, 3.0, 5.0, 4.0};
        product.setReviews(updatedReviews);
        product.setReviewCount(4);

        assertEquals(4, product.getReviewCount());
        assertEquals(4, product.getReviews().length);
        assertEquals(4.0, product.getReviews()[3], 0.001);
    }

    // ─── DELETE Tests ─────────────────────────────────────────────────────────

    @Test
    public void testDeleteProductByNullingFields() {
        // Simulates a soft-delete by clearing critical fields
        product.setProductName(null);
        product.setStatus("deleted");

        assertNull(product.getProductName());
        assertEquals("deleted", product.getStatus());
    }

    @Test
    public void testDeleteProductClearsStock() {
        product.setStockQuantity(0);
        assertEquals(0, product.getStockQuantity());
    }

    @Test
    public void testDeleteProductIdRemainsAfterSoftDelete() {
        // ID must persist for Firestore document deletion reference
        product.setStatus("deleted");
        assertNotNull(product.getProductId());
        assertEquals("prod_001", product.getProductId());
    }

    // ─── Average Rating Helper Test ───────────────────────────────────────────

    @Test
    public void testAverageRatingCalculation() {
        double[] reviews = product.getReviews();
        double sum = 0;
        for (double r : reviews) sum += r;
        double avg = sum / reviews.length;

        assertEquals(4.166, avg, 0.001);
    }

    @Test
    public void testAverageRatingWithNoReviews() {
        product.setReviews(new double[]{});
        product.setReviewCount(0);

        assertEquals(0, product.getReviews().length);
        // No division by zero — guard this in your actual code
    }
}