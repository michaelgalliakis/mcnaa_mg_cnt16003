package FindCellTowerApiOperation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AppService {
    @POST("process.php")
    Call<CellTowerLocation> getCellTowerLocation(@Body PostRequestCellTowLoc prctl);
}

