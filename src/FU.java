public class FU {
    private boolean busy=false;
    private String op=null;
    private String Fi=null;
    private String Fj=null;
    private String Fk=null;
    private String Qj=null;
    private String Qk=null;
    private boolean Rj=false;
    private boolean Rk=false;
    private String name;
    private int time;

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public void setFi(String fi) {
        Fi = fi;
    }

    public void setFj(String fj) {
        Fj = fj;
    }

    public void setFk(String fk) {
        Fk = fk;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setQj(String qj) {
        Qj = qj;
    }

    public void setQk(String qk) {
        Qk = qk;
    }

    public void setRj(boolean rj) {
        Rj = rj;
    }

    public void setRk(boolean rk) {
        Rk = rk;
    }

    public String getFj() {
        return Fj;
    }

    public String getFk() {
        return Fk;
    }

    public String getFi() {
        return Fi;
    }

    public String getOp() {
        return op;
    }

    public String getQj() {
        return Qj;
    }

    public String getQk() {
        return Qk;
    }
    public boolean getbusy(){
        return busy;
    }

    public String getName() {
        return name;
    }
    public boolean getRj(){
        return Rj;
    }
    public boolean getRk(){
        return Rk;
    }



    public int getTime() {
        return time;
    }

    public FU(String name,int time){
        this.name=name;
        this.time=time;
    }
    public FU(){

    }
}
