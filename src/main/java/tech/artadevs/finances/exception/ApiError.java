package tech.artadevs.finances.exception;

public class ApiError {

    private String detail;

    public ApiError(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
