package FindCellTowerApiOperation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostRequestCellTowLoc {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("mcc")
    @Expose
    private String mcc;
    @SerializedName("mnc")
    @Expose
    private String mnc;
    @SerializedName("cells")
    @Expose
    private Cells[] cells;

    public PostRequestCellTowLoc(String token, String mcc, String mnc, int lac, int cid) {
        this.token = token;
        this.mcc = mcc;
        this.mnc = mnc;
        Cells tmp[] = new Cells[1];
        tmp[0] = new Cells(lac, cid);
        this.cells = tmp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public Cells[] getCells() {
        return cells;
    }

    public void setCells(Cells[] cells) {
        this.cells = cells;
    }

    public class Cells {
        @SerializedName("lac")
        @Expose
        private int lac;
        @SerializedName("cid")
        @Expose
        private int cid;

        public Cells(int lac, int cid) {
            this.lac = lac;
            this.cid = cid;
        }

        public int getLac() {
            return lac;
        }

        public void setLac(int lac) {
            this.lac = lac;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        @Override
        public String toString() {
            return "[{lac:" + lac + "cid:" + cid + "}]";
        }
    }
}
