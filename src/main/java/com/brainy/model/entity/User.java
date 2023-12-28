package com.brainy.model.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	// One capital letter, one small letter, one number and at least 8 characters
	public final static String userPasswordValidationRegExpr =
			"^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$";

	@Id
	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@Column(name = "password", nullable = false, length = 100)
	@JsonIgnore
	private String password;

	@Column(name = "email", nullable = false, length = 50, unique = true)
	private String email;

	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Column(name = "password_change_date", nullable = false)
	@JsonIgnore
	private Timestamp passwordChangeDate;

	@Column(name = "logout_date", nullable = false)
	@JsonIgnore
	private Timestamp logoutDate;

	public User() {
		// We subtract one so that testing work
		Instant now = Instant.now().minus(1, ChronoUnit.MINUTES);

		passwordChangeDate = Timestamp.from(now);
		logoutDate = Timestamp.from(now);
	}

	public User(String username, String password, String email, String firstName, String lastName) {
		this();
		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public User(String username, String password, String email, String firstName, String lastName,
			Timestamp passwordChangeDate, Timestamp logoutDate) {

		this.username = username;
		this.password = password;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.passwordChangeDate = passwordChangeDate;
		this.logoutDate = logoutDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Timestamp getPasswordChangeDate() {
		return passwordChangeDate;
	}

	public void setPasswordChangeDate(Timestamp passwordChangeDate) {
		this.passwordChangeDate = passwordChangeDate;
	}

	public Timestamp getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(Timestamp logoutDate) {
		this.logoutDate = logoutDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;

		User other = (User) obj;
		return other.getUsername() == getUsername();
	}
}
