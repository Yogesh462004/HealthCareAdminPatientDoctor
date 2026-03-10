package nimblix.in.HealthCareHub.request;

public class PatientRegistrationRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    private String confirmPassword;
    private String phoneNumber;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getPhoneNumber() {
        return phoneNumber;}

    public void setPhoneNumber(String phoneNumber){ this.phoneNumber=phoneNumber;}
}
