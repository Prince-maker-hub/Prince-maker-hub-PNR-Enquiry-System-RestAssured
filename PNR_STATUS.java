import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;

import java.util.List;
import java.util.Map;

public class PNR_STATUS {

    public static void main(String[] args) {
        String baseURI = "http://localhost:3000";
        String pnrNumber = "2468013579";

        Response response = 
            given()
                .baseUri(baseURI)
                .pathParam("pnrNumber", pnrNumber)
            .when()
              
                .get("/trainStatus?pnrNumber={pnrNumber}")
            .then()
                .statusCode(200) 
                .time(lessThan(2000L)) 
                .header("Content-Type", containsString("application/json")) 
                .extract()
                .response();

 
        JsonPath jsonPath = new JsonPath(response.asString());
        List<Map<String, Object>> trainList = jsonPath.getList("");

        System.out.println("PNR Search Results:");
        for (Map<String, Object> train : trainList) {
            System.out.println("Train Name: " + train.get("trainName"));
            
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> passengers = (List<Map<String, Object>>) train.get("passengerDetails");
            for (Map<String, Object> passenger : passengers) {
                System.out.println(
                    passenger.get("name") + " | Seat: " + passenger.get("seatNumber") + " | Status: " + passenger.get("status")
                );

                String status = (String) passenger.get("status");
                if (!(status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("RAC") || status.equalsIgnoreCase("Waiting"))) {
                    throw new AssertionError("Invalid passenger status found: " + status);
                }
            }
        }
    }
}
