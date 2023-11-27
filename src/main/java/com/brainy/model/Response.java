package com.brainy.model;

public class Response<T> {

	private ResponseStatus status;
	private T data;

	public Response(T data, ResponseStatus status) {
		this.data = data;
		this.status = status;
	}

	/**
	 * If no ResponseStatus is provided, SUCCESS is used.
	 */
	public Response(T data) {
		this(data, ResponseStatus.SUCCESS);
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T body) {
		this.data = body;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Response<T> other = (Response<T>) obj;
		if (status != other.status)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

}
