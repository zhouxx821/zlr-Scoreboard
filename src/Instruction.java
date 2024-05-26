public class Instruction {
    private String inst;
    private String op;
    private String Fi;
    private String Fj;
    private String Fk;
    private int issue=0;
    private int read=0;
    private int exe=0;
    private int writeback=0;
    private boolean finish=false;
    private int exeCycle=0;
    private FU fu;

    public String getInst() {
        return inst;
    }

    public String getOp() {
        return op;
    }

    public String getFi() {
        return Fi;
    }

    public String getFj() {
        return Fj;
    }

    public String getFk() {
        return Fk;
    }

    public int getIssue() {
        return issue;
    }

    public int getRead() {
        return read;
    }

    public int getExe() {
        return exe;
    }

    public int getWriteback() {
        return writeback;
    }

    public boolean getFinish()
    {
        return finish;
    }
    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public void setExe(int exe) {
        this.exe = exe;
    }

    public void setWriteback(int writeback) {
        this.writeback = writeback;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public void setExeCycle(int exeCycle) {
        this.exeCycle = exeCycle;
    }

    public int getExeCycle() {
        return exeCycle;
    }

    public void setFu(FU fu) {
        this.fu = fu;
    }

    public FU getFu() {
        return fu;
    }

    public Instruction(String input) {
        String[] parts = input.split("[,\\s()]");
        if (parts.length >= 4) {
            this.op = parts[0].trim();
            this.Fi = parts[1].trim();
            this.Fj = parts[2].trim();
            this.Fk = parts[3].trim();
        } else {
            System.out.println("Invalid input: " + input);
        }
    }
}
