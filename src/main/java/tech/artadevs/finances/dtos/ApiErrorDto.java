package tech.artadevs.finances.dtos;

public class ApiErrorDto {

    private String detail;

    public ApiErrorDto(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
