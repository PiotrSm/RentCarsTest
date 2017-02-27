package rentcarstest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Piotr S.
 */
public class RentCarsTest {

    public static void main(String[] args) throws MalformedURLException, IOException {

        List<Vehicle> vehicleList = getArrayListFromJson();
        printListAscendingPrice(vehicleList);
        System.out.println("\n");
        printSpecification(vehicleList);
        System.out.println("\n");
        vehicleList.sort(Comparator.comparing(Vehicle::getName));
        vehicleList.forEach((veh) -> {
            System.out.println(String.format("%-24s%-15s%-15s% 8.1f", veh.getName(), decodeSipp(veh.getSipp(), 0),
                     veh.getSupplier(), veh.getRating()));
        });
    }

    private static void printSpecification(List<Vehicle> vehicleList) {

        vehicleList.forEach((veh) -> {
            System.out.println(String.format("%-21s %-10s %-15s %-15s %-15s %1s",
                    veh.getName(), veh.getSipp(),
                    decodeSipp(veh.getSipp(), 0),
                    decodeSipp(veh.getSipp(), 1),
                    decodeSipp(veh.getSipp(), 2),
                    decodeSipp(veh.getSipp(), 3))
            );
        });
    }

    private static String decodeSipp(String sipp, int num) {

        Map types = new HashMap();
        types.put('M', "Mini");
        types.put('E', "Economy");
        types.put('C', "Compact");
        types.put('I', "Intermediate");
        types.put('S', "Standard");
        types.put('F', "Full size");
        types.put('L', "Luxury");
        types.put('X', "Special");
        types.put('S', "Standard");

        Map typeDoors = new HashMap();
        typeDoors.put('B', "2 doors");
        typeDoors.put('C', "4 doors");
        typeDoors.put('D', "5 doors");
        typeDoors.put('W', "Estate");
        typeDoors.put('T', "Convertible");
        typeDoors.put('F', "SUV");
        typeDoors.put('P', "Pick up");
        typeDoors.put('V', "Passenger Van");
        typeDoors.put('X', "Special");

        Map transmission = new HashMap();
        transmission.put('M', "Manual");
        transmission.put('A', "Automatic");

        Map airCon = new HashMap();
        airCon.put('N', "Petrol      no AC");
        airCon.put('R', "Petrol      AC");

        switch (num) {
            case 0:
                return (String) types.get(sipp.charAt(0));
            case 1:
                return (String) typeDoors.get(sipp.charAt(1));
            case 2:
                return (String) transmission.get(sipp.charAt(2));
            case 3:
                return (String) airCon.get(sipp.charAt(3));
            default:
                return "Invalid type";
        }

    }

    private static void printListAscendingPrice(List<Vehicle> vehicleList) {
        vehicleList.sort(Comparator.comparing(Vehicle::getPrice));// can be (v -> v.getPrice)
        vehicleList.forEach((Vehicle vehicle) -> {
            System.out.println(String.format("%-24s% 8.2f", vehicle.getName(), vehicle.getPrice()));
        });
    }

    private static List<Vehicle> getArrayListFromJson() throws JsonSyntaxException, IOException, JsonIOException, MalformedURLException {
        String sURL = "http://www.rentalcars.com/js/vehicles.json";
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootobj = root.getAsJsonObject();
        JsonObject search = rootobj.getAsJsonObject("Search");
        JsonElement vehicles = search.get("VehicleList");
        Gson gson = new GsonBuilder().create();
        Vehicle[] vehi = gson.fromJson(vehicles, Vehicle[].class);
        List<Vehicle> vehicleList = Arrays.asList(vehi);
        return vehicleList;
    }

}
