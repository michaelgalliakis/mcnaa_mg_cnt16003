package FindCellTowerApiOperation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppService {
    @POST("process.php")
    Call<CellTowerLocation> getCellTowerLocation(@Body PostRequestCellTowLoc prctl);

}

