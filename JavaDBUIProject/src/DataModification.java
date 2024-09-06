public interface DataModification {
    void updateUserPassword(String username, String newPassword);

    void deleteUser(String username);

    void addCustomer(String customerName, String address, String postalCode, String phoneNumber, String country, String firstLevelDivision);

    void updateCustomer(int customerId, String customerName, String address, String postalCode, String phoneNumber, String country, String firstLevelDivision);

    void deleteCustomer(int customerId);

    void addAppointment(String title, String description, String location, String contact, String type, String startDateTime, String endDateTime, int customerId, int userId);

    void updateAppointment(int appointmentId, String title, String description, String location, String contact, String type, String startDateTime, String endDateTime, int customerId, int userId);

    void deleteAppointment(int appointmentId);

    void adjustAppointmentTimes();
}
