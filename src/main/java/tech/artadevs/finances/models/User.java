package tech.artadevs.finances.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Long id;

	private String name;
	private Integer age;

	@Column(unique = true, length = 100, nullable = false)
	private String email;

	@Column(unique = true, nullable = false)
	private Long accountNumber;

	@Column(nullable = false)
	private String password;

	@Column(name = "deleted_at", nullable = true)
	private Date deletedAt = null;

	@CreationTimestamp
	@Column(updatable = false, name = "created_at", nullable = false)
	private Date createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Date updatedAt;

	@Column(nullable = false)
	private boolean enabled = true;

	public User setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public User() {
	}

	public Long getId() {
		return id;
	}

	public User setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public User setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getAge() {
		return age;
	}

	public User setAge(Integer age) {
		this.age = age;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public User setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return deletedAt == null;
	}

	@Override
	public boolean isAccountNonLocked() {
		return deletedAt == null;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return deletedAt == null;
	}

	@Override
	public boolean isEnabled() {
		return enabled && deletedAt == null;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public User setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public User setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public User setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
		return this;
	}
}