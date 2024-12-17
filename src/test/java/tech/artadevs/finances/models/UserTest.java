package tech.artadevs.finances.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testSetCreatedAt() {
        Date createdAt = new Date();
        user.setCreatedAt(createdAt);
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testSetUpdatedAt() {
        Date updatedAt = new Date();
        user.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testGetUpdatedAt() {
        Date updatedAt = new Date();
        user.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testIsEnabledAndNotDeleted() {
        user.setEnabled(true);
        user.setDeletedAt(null);
        assertTrue(user.isEnabled());
    }

    @Test
    void testIsNotEnabledAndNotDeleted() {
        user.setDeletedAt(null);
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testIsDeletedAndNotEnabled() {
        user.setDeletedAt(new Date());
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testIsDeletedAndEnabled() {
        user.setDeletedAt(new Date());
        user.setEnabled(true);
        assertFalse(user.isEnabled());
    }

    @Test
    void testIsAccountNonExpired() {
        user.setDeletedAt(null);
        assertTrue(user.isAccountNonExpired());

        user.setDeletedAt(new Date());
        assertFalse(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        user.setDeletedAt(null);
        assertTrue(user.isAccountNonLocked());

        user.setDeletedAt(new Date());
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        user.setDeletedAt(null);
        assertTrue(user.isCredentialsNonExpired());

        user.setDeletedAt(new Date());
        assertFalse(user.isCredentialsNonExpired());
    }
}
