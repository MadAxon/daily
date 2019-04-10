package ru.vital.daily.repository.model;

public class CountryCodeModel {

    private String firstLetter;

    private final String countryName, phoneCode;

    public CountryCodeModel(String countryName, int phoneCode) {
        this.countryName = countryName;
        this.phoneCode = String.format("+%s", phoneCode);
    }

    public String getCountryName() {
        return countryName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }
}
