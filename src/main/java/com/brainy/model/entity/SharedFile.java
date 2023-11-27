package com.brainy.model.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shared_files")
@IdClass(SharedFilePK.class)
public class SharedFile {

	@OneToOne
	@JoinColumn(name = "file_owner")
	@Id
	@JsonIgnore // Handled in special method
	private User fileOwner;

	@OneToOne
	@JoinColumn(name = "shared_with")
	@Id
	@JsonIgnore // Handled in special method
	private User sharedWith;

	@Column(name = "filename")
	@Id
	private String filename;

	@Column(name = "can_edit")
	private boolean canEdit = false;

	public SharedFile() {
	}

	public SharedFile(User fileOwner, String filename, User sharedWith, boolean canEdit) {

		this.fileOwner = fileOwner;
		this.filename = filename;
		this.sharedWith = sharedWith;
		this.canEdit = canEdit;
	}

	@JsonProperty("fileOwner")
	public String getFileOwnerUsername() {
		return fileOwner.getUsername();
	}

	@JsonProperty("sharedWith")
	public String getSharedWithUsername() {
		return sharedWith.getUsername();
	}

	public User getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(User fileOwner) {
		this.fileOwner = fileOwner;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public User getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(User sharedWith) {
		this.sharedWith = sharedWith;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
}


class SharedFilePK implements Serializable {

	private User fileOwner;
	private User sharedWith;
	private String filename;

	public SharedFilePK() {
	}

	public SharedFilePK(User fileOwner, User sharedWith, String filename) {
		this.fileOwner = fileOwner;
		this.sharedWith = sharedWith;
		this.filename = filename;
	}

	public User getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(User fileOwner) {
		this.fileOwner = fileOwner;
	}

	public User getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(User sharedWith) {
		this.sharedWith = sharedWith;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileOwner == null) ? 0 : fileOwner.hashCode());
		result = prime * result + ((sharedWith == null) ? 0 : sharedWith.hashCode());
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedFilePK other = (SharedFilePK) obj;
		if (fileOwner == null) {
			if (other.fileOwner != null)
				return false;
		} else if (!fileOwner.equals(other.fileOwner))
			return false;
		if (sharedWith == null) {
			if (other.sharedWith != null)
				return false;
		} else if (!sharedWith.equals(other.sharedWith))
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}

}
