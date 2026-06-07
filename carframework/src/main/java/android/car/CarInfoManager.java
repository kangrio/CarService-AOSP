package android.car;

public final class CarInfoManager {
    public CarInfoManager() {
        LogTrace.enter("CarInfoManager", "CarInfoManager");
        LogTrace.voidResult("CarInfoManager", "CarInfoManager");
    }

    public String getManufacturer() {
        LogTrace.enter("CarInfoManager", "getManufacturer");
        return LogTrace.result("CarInfoManager", "getManufacturer", "BYD");
    }

    public String getModel() {
        LogTrace.enter("CarInfoManager", "getModel");
        return LogTrace.result("CarInfoManager", "getModel", "ATTO3");
    }

    @Deprecated
    public String getModelYear() {
        LogTrace.enter("CarInfoManager", "getModelYear");
        return LogTrace.result("CarInfoManager", "getModelYear", "2023");
    }

    public int getModelYearInInteger() {
        LogTrace.enter("CarInfoManager", "getModelYearInInteger");
        return LogTrace.result("CarInfoManager", "getModelYearInInteger", 2023);
    }

    @Deprecated
    public String getVehicleId() {
        LogTrace.enter("CarInfoManager", "getVehicleId");
        return LogTrace.result("CarInfoManager", "getVehicleId", "FAKEVIN000000000");
    }

    public float getFuelCapacity() {
        LogTrace.enter("CarInfoManager", "getFuelCapacity");
        return LogTrace.result("CarInfoManager", "getFuelCapacity", 0f);
    }

    public int[] getFuelTypes() {
        LogTrace.enter("CarInfoManager", "getFuelTypes");
        return LogTrace.result("CarInfoManager", "getFuelTypes", new int[]{FuelType.ELECTRIC});
    }

    public float getEvBatteryCapacity() {
        LogTrace.enter("CarInfoManager", "getEvBatteryCapacity");
        return LogTrace.result("CarInfoManager", "getEvBatteryCapacity", 60_480f);
    }

    public int[] getEvConnectorTypes() {
        LogTrace.enter("CarInfoManager", "getEvConnectorTypes");
        return LogTrace.result("CarInfoManager", "getEvConnectorTypes", new int[]{EvConnectorType.COMBO_2});
    }

    public int getDriverSeat() {
        LogTrace.enter("CarInfoManager", "getDriverSeat");
        return LogTrace.result("CarInfoManager", "getDriverSeat", VehicleAreaSeat.SEAT_ROW_1_LEFT);
    }

    public int getEvPortLocation() {
        LogTrace.enter("CarInfoManager", "getEvPortLocation");
        return LogTrace.result("CarInfoManager", "getEvPortLocation", PortLocationType.FRONT_RIGHT);
    }

    public int getFuelDoorLocation() {
        LogTrace.enter("CarInfoManager", "getFuelDoorLocation");
        return LogTrace.result("CarInfoManager", "getFuelDoorLocation", PortLocationType.FRONT_RIGHT);
    }
}
